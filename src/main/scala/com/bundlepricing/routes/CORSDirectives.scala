package com.bundlepricing.routes

import spray.http.HttpHeaders._
import spray.http.HttpMethods.{ OPTIONS, GET, POST, PUT, DELETE }
import spray.http._
import spray.routing.Directives._
import spray.routing._
import spray.util.LoggingContext

import scala.collection.immutable.Seq

trait CORSDirectives {

  import CORSDirectives._

  def cors(magnet: CORSMagnet): Directive0 = magnet()

}

sealed trait AcceptedOrigins
case object AllOriginsAccepted extends AcceptedOrigins
case class SomeOriginsAccepted(originList: Seq[HttpOrigin]) extends AcceptedOrigins
case object EchoAllOriginsAccepted extends AcceptedOrigins

object CORSDirectives {

  implicit class CORSMagnet(acceptedOrigins: AcceptedOrigins)(implicit eh: ExceptionHandler, rh: RejectionHandler, settings: RoutingSettings, log: LoggingContext) {
    def apply(): Directive0 =
      optionalHeaderValueByType[HttpHeaders.Origin](()).map { //Map request's "Origin" header into response's "Allow-Origin" header
        _.flatMap { requestOrigins ⇒
          acceptedOrigins match {
            case AllOriginsAccepted ⇒
              Some(`Access-Control-Allow-Origin`(AllOrigins))

            case SomeOriginsAccepted(origins) ⇒
              Some(requestOrigins.originList.filter(origins.contains)).filter(_.nonEmpty) //Create a list of request origins that match accepted origins
                .map(acceptedRequestOrigins ⇒ `Access-Control-Allow-Origin`(SomeOrigins(acceptedRequestOrigins)))

            case EchoAllOriginsAccepted ⇒
              //Allow-Credentials doesn't work w/ AllOrigins, so echo request's origin to bypass this limitation and still allow all origins
              Some(`Access-Control-Allow-Origin`(SomeOrigins(requestOrigins.originList)))
          }
        }
      }.flatMap { //Create the response using the "Allow-Origin" header we've created
        case Some(corsAllowOriginHeader) ⇒
          val corsHeaders = List(corsAllowOriginHeader, `Access-Control-Allow-Credentials`(true))
          val corsPreFlightHeaders = List(`Access-Control-Allow-Methods`(GET, POST, PUT, DELETE, OPTIONS),
            `Access-Control-Allow-Headers`("Origin", "X-Requested-With", "Content-Type", "Accept", "Accept-Encoding", "Accept-Language", "Host", "Referrer", "User-Agent",
              "Session", "Cookie"),
            `Access-Control-Max-Age`(1728000)) //1728000 seconds == 20 days

          extract(_.request.method == OPTIONS).flatMap { isPreFlightRequest ⇒
            addResponseHeaders(corsHeaders ++ (if (isPreFlightRequest) corsPreFlightHeaders else Nil)) &
              mapInnerRoute(_ ~ options(complete(StatusCodes.OK))) //Add extra route to deal with pre-flight requests that can't get past authentication directives
          }

        case None ⇒
          pass
      }
  }

  //Force "respondWithHeaders" to add headers even on exceptions or rejections
  private def addResponseHeaders(myHeaders: List[HttpHeader])(implicit eh: ExceptionHandler, rh: RejectionHandler, settings: RoutingSettings, log: LoggingContext) =
    respondWithHeaders(myHeaders: _*) & handleExceptions(eh orElse ExceptionHandler.default) & handleRejections(rh orElse RejectionHandler.Default)

}
