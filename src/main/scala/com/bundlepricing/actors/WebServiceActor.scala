package com.bundlepricing.actors

import com.bundlepricing.Settings
import com.bundlepricing.repos._
import com.bundlepricing.routes._

import akka.actor.ActorLogging
import akka.io.IO
import spray.can.Http
import spray.httpx.PlayJsonSupport
import spray.routing.{ ExceptionHandler, HttpServiceActor }

class WebServiceActor(host: String, port: Int,
  itemRepo: ItemMongoRepo, bundleRepo: BundleMongoRepo)
    extends HttpServiceActor
    with ActorLogging
    with CORSDirectives
    with BuildInfoRoute
    with ItemRoute {
  
  import WebServiceActor.exceptionHandler

  //Start this actor as a web server
  IO(Http)(context.system) ! Http.Bind(listener = self, interface = host, port = port)

  implicit val webServiceTimeout = Settings.webServiceTimeout
  import context.dispatcher

  //Aggregate all the routes
  def receive = runRoute(
    cors(EchoAllOriginsAccepted) {
      buildInfoRoute ~
      itemRoute(itemRepo)
    })
    
}

object WebServiceActor extends PlayJsonSupport {

  import com.bundlepricing.services.ValidationError

  import com.mongodb.DuplicateKeyException
  import scala.collection.immutable.Seq
  import spray.http.StatusCodes
  import spray.routing.Directives.complete

  //We MUST explicitly declare the type, ExceptionHandler, or it doesn't get properly, implicitly passed to runRoute
  implicit val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: DuplicateKeyException ⇒ complete((StatusCodes.BadRequest, Seq(ValidationError.DuplicateKey)))
  }

}
