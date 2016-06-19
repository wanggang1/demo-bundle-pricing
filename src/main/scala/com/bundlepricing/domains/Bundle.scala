package com.bundlepricing.domains

import com.novus.salat.annotations.Key
import org.bson.types.ObjectId
import play.api.libs.functional.syntax._
import play.api.libs.json._

object Bundle {
          
  val collectionName = "Bundles"

  def createBundle(ps: List[PricingPolicy]) = Bundle(key = uniqueKey(ps), pricings = ps)
 
 /**
   * bundleKey made from the item names, bundleKey needs to be unique in the system
   */
  def uniqueKey(ps: List[PricingPolicy]): String =
    ps.sortBy(_.discount).reverse.map(_.itemName).mkString
      
  /**
   * string representations of permutation of item names
   */
  def keyPermutations(itemNames: List[String]): List[String] =
    itemNames.permutations.map(_.mkString).toList

  def bundleKey(items: List[Item]): String = items.map(_.name).mkString

  def price(b: Bundle): Double = {
    val price = b.pricings.foldLeft(0.0){(acc, p) => p.unitPrice * p.discount + acc}
    BigDecimal(price).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }
  
  ////Json serialization /////////////////
  private val reads: Reads[Bundle] = {
    val idReads = (__ \ 'id).read[ObjectId] orElse Reads.pure(new ObjectId)
  
    (idReads and
      (__ \ 'key).read[String] and
      (__ \ 'pricings).read[List[PricingPolicy]])(Bundle.apply _)
  }

  private val writes: Writes[Bundle] = Json.writes[Bundle]

  implicit val formatter: Format[Bundle] = Format(reads, writes)

}

/**
 * will create _id in app have issue when app running on multiple instances of JVM???
 */
case class Bundle(@Key("_id") id: ObjectId = new ObjectId, key: String, pricings: List[PricingPolicy])

object PricingPolicy {
  implicit val formatter: Format[PricingPolicy] = Json.format[PricingPolicy]
}

case class PricingPolicy(itemName: String, unitPrice: Double, discount: Double)
