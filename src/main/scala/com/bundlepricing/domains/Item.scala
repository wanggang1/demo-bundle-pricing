package com.bundlepricing.domains

import com.novus.salat.annotations.Key
import org.bson.types.ObjectId
import play.api.libs.functional.syntax._
import play.api.libs.json._
import squants.market.{ Money, USD }

import com.bundlepricing.services.ValidationService._

object Item {

  val collectionName = "Items"
  
  private val reads: Reads[Item] = {
    val idReads = (__ \ 'id).read[ObjectId] orElse Reads.pure(new ObjectId)

    (idReads and
      (__ \ 'name).read[String] and
      (__ \ 'price).read[Money])(Item.apply _)
  }

  private val writes: Writes[Item] = Json.writes[Item]

  implicit val formatter: Format[Item] = Format(reads, writes)
  
  implicit val validator: Validator[Item] = item ⇒ {
    import item._

    val validationErrors = PartialItem.validationErrors(Some(name), Some(price))
    createValidated(success = item, errors = validationErrors)
  }
  
}

/**
 * will create _id in app have issue when app running on multiple instances of JVM???
 */
case class Item(
    @Key("_id") id: ObjectId,
    name: String, 
    price: Money) {
  
  def copyPartial(p: PartialItem): Item =
    copy(name = p.name.getOrElse(name), price = p.price.getOrElse(price))
}
    

object PartialItem {
  import com.bundlepricing.services.ValidationError._

  implicit val formatter: Format[PartialItem] = Json.format[PartialItem]

  implicit val validator: Validator[PartialItem] = partialStation ⇒ {
    import partialStation._

    createValidated(success = partialStation, errors = validationErrors(name, price))
  }

  def validationErrors(name: Option[String], price: Option[Money]) = {
    import scalaz.std.option._
    import scalaz.syntax.traverse._

    coalesceValidationErrors(
      name.traverse[Validated, String](checkNonEmptyString(_, ItemNameRequired)),
      checkNonEmptyOption(price, ItemPriceRequired))
  }
}

case class PartialItem(name: Option[String], price: Option[Money])