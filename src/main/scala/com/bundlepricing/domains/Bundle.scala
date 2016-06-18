package com.bundlepricing.domains

import com.novus.salat.annotations.Key
import org.bson.types.ObjectId
import squants.market.{ Money, USD }

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
  
}

/**
 * will create _id in app have issue when app running on multiple instances of JVM???
 */
case class Bundle(@Key("_id") id: ObjectId = new ObjectId, key: String, pricings: List[PricingPolicy])

case class PricingPolicy(itemName: String, unitPrice: Double, discount: Double)
