package com.bundlepricing.routes

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

trait ItemRoute extends Directives with DomainDirectives with PlayJsonSupport {

  def itemRoute(repo: ItemMongoRepo)(implicit ec: ExecutionContext, timeout: Timeout, log: LoggingContext) = {
    // format: OFF
    pathPrefix("bundlepricing" / "items") {
      pathEndOrSingleSlash {
        get {
          complete {
            Future {
              repo.getAll.values.toList
            }
          }
        } ~
        (post & entity(as[Item])) {
          domainValidate(_) { item ⇒
            complete {
              Future {
                repo.insert(item)
                (StatusCodes.Created, Json.obj("id" -> item.id))
              }
            }
          }
        }
      } ~
      path(MongoObjectId ~ Slash.?) { itemId =>
        repoEntity(repo.get(itemId)) { item =>
          (post & entity(as[PartialItem])) {
            domainValidate(_){ partial ⇒ 
              complete {
                Future {
                  repo.upsert(item.copyPartial(partial))
                  StatusCodes.OK
                }
              }
            }
          } ~
          delete {
            complete {
              Future {
                repo.delete(item.id)
                StatusCodes.OK
              }
            }
          } ~
          get {
            complete(StatusCodes.OK, item)
          }
        }
      } ~
      path(Segment ~ Slash.?) { itemName ⇒
        get { 
          repoEntity(repo.getByKey(itemName)) { item =>
            complete(StatusCodes.OK, item)
          }
        } 
      }
    }
    // format: ON
  }
  
}
