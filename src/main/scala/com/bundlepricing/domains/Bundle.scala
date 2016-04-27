package com.bundlepricing.domains

import com.novus.salat.annotations.Key
import org.bson.types.ObjectId
import squants.market.{ Money, USD }

import com.bundlepricing.core.Pricing

object Bundle {

  def apply(items: List[Item], applyPolicy: Pricing) =
    new Bundle(new ObjectId, items, bundleKey(items), USD(applyPolicy(items)))
  
  def bundleKey(items: List[Item]): String = items.map(_.name).mkString
  
  /*
   * string representations of permutation of item names
   */
  def keyPermutations(items: List[Item]): List[String] =
    items.map(_.name).permutations.map(_.mkString).toList
        
  val collectionName = "Bundles"

}

/**
 * will create _id in app have issue when app running on multiple instances of JVM???
 */
case class Bundle(
    @Key("_id") id: ObjectId,
    items: List[Item], 
    key: String, 
    price: Money)
