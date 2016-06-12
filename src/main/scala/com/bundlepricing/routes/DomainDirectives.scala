package com.bundlepricing.routes

import com.bundlepricing.services.ValidationService
import com.bundlepricing.services.ValidationService.Validator
import scala.concurrent.{ ExecutionContext, Future }
import spray.http.StatusCodes
import spray.httpx.PlayJsonSupport
import spray.routing.Directive1
import spray.routing.Directives._

import scalaz.{ Failure, Success }

trait DomainDirectives {

  import DomainDirectives._

  def domainValidate[T](magnet: DomainValidateMagnet[T]): Directive1[T] = magnet()

  def repoEntity[T](magnet: RepoEntityMagnet[T]): Directive1[T] = magnet()

  def requireOption[T](option: Option[T], errorMsg: String): Directive1[T] =
    validate(option.isDefined, errorMsg).hflatMap(_ ⇒ provide(option.get))

}

object DomainDirectives extends PlayJsonSupport {
  
  implicit class DomainValidateMagnet[T: Validator](validatable: T) {
    def apply(): Directive1[T] =
      ValidationService.validate(validatable) match {
        case Success(validated) ⇒ provide(validated)
        case Failure(errors)    ⇒ complete((StatusCodes.BadRequest, errors.list))
      }
  }

  implicit class RepoEntityMagnet[T](getEntity: ⇒ Option[T])(implicit ec: ExecutionContext) {
    def apply(): Directive1[T] =
      onSuccess(Future(getEntity)).flatMap {
        case Some(entity) ⇒ provide(entity)
        case None         ⇒ complete(StatusCodes.NotFound)
      }
  }

}
