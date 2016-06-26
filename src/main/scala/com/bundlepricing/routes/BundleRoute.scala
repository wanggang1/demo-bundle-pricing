package com.bundlepricing.routes

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import spray.http._
import spray.httpx.PlayJsonSupport
import spray.routing._
import spray.routing.authentication.ContextAuthenticator
import spray.util.LoggingContext

import com.bundlepricing.repos.ItemMongoRepo
import com.bundlepricing.domains._

trait BundleRoute extends Directives with DomainDirectives with PlayJsonSupport {

  import com.bundlepricing.actors.BundleActor._
  
  def bundleRoute(bundleActor: ActorRef)(implicit ec: ExecutionContext, timeout: Timeout, log: LoggingContext) = {
    // format: OFF
    pathPrefix("bundlepricing" / "bundles") {
      pathEndOrSingleSlash {
        get {
          onSuccess( (bundleActor ? GetCachedBundles).mapTo[AllBundles] ) { bundles =>
            complete(StatusCodes.OK, bundles.all.values.toList)
          }
        }
      } 
    }
    // format: ON
  }

}
