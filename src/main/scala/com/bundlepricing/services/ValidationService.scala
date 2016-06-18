package com.bundlepricing.services

import org.joda.time.DateTime
import play.api.libs.json.{ JsString, JsValue, Writes }

import scala.collection.immutable.Seq
import scalaz.Validation.FlatMap._
import scalaz.{ NonEmptyList, Failure, Success, ValidationNel }
import scalaz.syntax.validation._

object ValidationService {

  import ValidationError._

  @annotation.implicitNotFound(msg = "No validator defined for ${T}. Try placing one in ${T}'s companion object.")
  type Validator[T] = T ⇒ Validated[T]
  
  type Validated[T] = ValidationNel[ValidationError, T]

  def validate[T](validatable: T)(implicit validator: Validator[T]): Validated[T] = validator(validatable)

  //TODO: For now we're manually aggregating errors and creating the final Validated. When we upgrade we can use more shapeless/scalaz magic
  //TODO: see https://github.com/typelevel/shapeless-contrib and sequencing validations
  def coalesceValidationErrors[T](validations: ValidationNel[ValidationError, _]*): Seq[ValidationError] =
    validations.toList.collect { case Failure(validationErrorNel) ⇒ validationErrorNel.list }.flatten

  def createValidated[T](success: T, errors: Seq[ValidationError]): ValidationNel[ValidationError, T] =
    errors match {
      case Nil          ⇒ Success(success)
      case head :: tail ⇒ Failure(NonEmptyList(head, tail: _*))
    }

  //Field checks
  def checkNonEmptyString(string: String, error: ValidationError = EmptyString): Validated[String] =
    if (string.trim.length > 0) string.successNel else error.failureNel

  def checkPositiveNumber[U](number: U, error: ValidationError = NegativeNumber)(implicit numeric: Numeric[U]): Validated[U] =
    if (numeric.compare(number, numeric.zero) >= 0) number.successNel else error.failureNel

  def checkNonEmptyOption[U](option: Option[U], error: ValidationError = EmptyOption): Validated[U] =
    option.map(_.successNel).getOrElse(error.failureNel)

  def checkNonEmptyStringOpt(stringOpt: Option[String], error: ValidationError = EmptyString): Validated[String] =
    checkNonEmptyOption(stringOpt).flatMap(string ⇒ checkNonEmptyString(string, error))

  def checkNonEmpty[U <: Traversable[_]](traversable: U, error: ValidationError = Empty): Validated[U] =
    if (traversable.nonEmpty) traversable.successNel else error.failureNel

  def checkTiming(first: DateTime, second: Option[DateTime], error: ValidationError = TimeWarp): Validated[DateTime] =
    if (second.isEmpty || first.isBefore(second.get)) first.successNel else error.failureNel

  def checkTimingOpt(firstOpt: Option[DateTime], secondOpt: Option[DateTime], error: ValidationError = TimeWarp): Validated[Option[DateTime]] =
    (firstOpt, secondOpt) match {
      case (Some(first), Some(second)) if first isBefore second ⇒ firstOpt.successNel
      case (Some(first), None) ⇒ firstOpt.successNel
      case (None, None) ⇒ firstOpt.successNel
      case _ ⇒ error.failureNel
    }

  def checkMinimumStringLength(input: String, min: Int, error: ValidationError = StringLength): Validated[String] =
    checkNonEmptyString(input, error) flatMap {
      case s if s.trim.length < min ⇒ error.failureNel
      case _                        ⇒ input.successNel
    }

}

/**
 * Standard validation errors
 */
case class ValidationError(key: String, msg: Option[String] = None)

object ValidationError {

  val EmptyString = ValidationError("EmptyString")
  val NegativeNumber = ValidationError("NegativeNumber")
  val EmptyOption = ValidationError("EmptyOption")
  val Empty = ValidationError("Empty")
  val TimeWarp = ValidationError("TimeWarp")
  val StringLength = ValidationError("StringLength")
  val DuplicateKey = ValidationError("DuplicateKey")

  val ItemIdRequired = ValidationError("ItemIdRequired")
  val ItemNameRequired = ValidationError("ItemNameRequired")
  val ItemPriceRequired = ValidationError("ItemPriceRequired")
  val BundleIdRequired = ValidationError("ItemIdRequired")

  implicit val writer = new Writes[ValidationError] { //Mimicking vpower-scada's validation error output (i.e. list of key strings)
    def writes(validationError: ValidationError): JsValue = JsString(validationError.key)
  }

}
