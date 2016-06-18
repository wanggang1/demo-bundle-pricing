package com.bundlepricing

import com.mongodb.casbah.Imports._
import com.novus.salat.transformers.CustomTransformer
import org.bson.types.ObjectId
import play.api.data.validation.ValidationError
import play.api.libs.json._
import scala.util.{ Failure, Success, Try }
import squants.{ UnitOfMeasure, Quantity }
import squants.market.Money

package object domains {

  implicit val objectIdFormatter = new Format[ObjectId] {
    def reads(json: JsValue) = json match {
      case JsString(objectIdString) ⇒
        Try(new ObjectId(objectIdString)) match {
          case Success(objectId)  ⇒ JsSuccess(objectId)
          case Failure(throwable) ⇒ JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.objectid")))) 
        }//TODO: write the proper error with the proper path
      case _ ⇒ JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jsstring"))))
    }

    def writes(objectId: ObjectId) = JsString(objectId.toString)
  }

  implicit val moneyFormatter = new Format[Money] {
    def writes(money: Money) = JsString(money.toString())
    def reads(json: JsValue) = {
      Money(json.as[String]) match {
        case Success(money) ⇒ JsSuccess(money)
        case Failure(throwable)   ⇒ JsError(throwable.getMessage)
      }
    }
  }
  
  class QuantityTransformer[T <: Quantity[T]: Manifest](unitOfMeasure: UnitOfMeasure[T]) extends CustomTransformer[T, DBObject] {
    def deserialize(dbObject: DBObject) = unitOfMeasure(dbObject.getAsOrElse[Double]("value", 0.0))
    def serialize(q: T) = DBObject("value" -> q.to(unitOfMeasure), "unit" -> unitOfMeasure.symbol)
  }

  ///////////////// Below are obsolete, Bundle pricing now uses PricingPolicy case class /////////////////
  type Pricing = List[Item] => Double

  val unitPrice: Pricing = (items: List[Item]) => {
    require(items.size == 1)
    items.head.price.value
  }

  val buy1Get1Free: Pricing = (items: List[Item]) => {
      require(items.size == 2)
      items.head.price.value
    }
  
  val buy1Get2ndHalf: Pricing = (items: List[Item]) => {
      require(items.size == 2)
      items.head.price.value + items.last.price.value / 2
    }
  
  val buy2Get3rdHalf: Pricing = (items: List[Item]) => {
      require(items.size == 3)
      items(0).price.value + items(1).price.value + items(2).price.value / 2
    }
  
  val buy3Get4thFree: Pricing = (items: List[Item]) => {
      require(items.size == 4)
      items.dropRight(1).map(_.price.value).foldLeft(0.0)(_ + _)
    }

}
