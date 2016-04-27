package com.bundlepricing.domains

import com.novus.salat.annotations.Key
import org.bson.types.ObjectId
import squants.market.{ Money, USD }

object Item {

  def apply(name: String, price: Double) = new Item(new ObjectId, name, USD(price))
    
  val collectionName = "Items"
  
}

/**
 * will create _id in app have issue when app running on multiple instances of JVM???
 */
case class Item(
    @Key("_id") id: ObjectId,
    name: String, 
    price: Money)

case class PartialItem(name: Option[String], price: Option[Money])