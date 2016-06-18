package com.bundlepricing

import com.bundlepricing.domains._
import org.bson.types.ObjectId
import squants.market.USD

trait TestData {
  import Bundle._
  
  val milk = Item(new ObjectId, "Milk", USD(2.99))
  val bread = Item(new ObjectId, "Bread", USD(1.99))
  val cereal = Item(new ObjectId, "Cereal", USD(2.50))
  val cheese = Item(new ObjectId, "SlicedCheese", USD(4.50))
  val peanutbutter = Item(new ObjectId, "PeanutButter", USD(2.50))
  val apple = Item(new ObjectId, "Apple", USD(1.00))
    
  val applePrice = PricingPolicy(apple.name, apple.price.amount.toDouble, 1.0)
  val appleFree = PricingPolicy(apple.name, apple.price.amount.toDouble, 0.0)
  
  val breadPrice = PricingPolicy(bread.name, bread.price.amount.toDouble, 1.0)
  val breadHalf = PricingPolicy(bread.name, bread.price.amount.toDouble, 0.5)
  val breadFree = PricingPolicy(bread.name, bread.price.amount.toDouble, 0.0)
  
  val cerealPrice = PricingPolicy(cereal.name, cereal.price.amount.toDouble, 1.0)
  val cerealHalf = PricingPolicy(cereal.name, cereal.price.amount.toDouble, 0.5)
  val cerealFree = PricingPolicy(cereal.name, cereal.price.amount.toDouble, 0.0)
  
  val cheesePrice = PricingPolicy(cheese.name, cheese.price.amount.toDouble, 1.0)
  val cheeseHalf = PricingPolicy(cheese.name, cheese.price.amount.toDouble, 0.5)
  
  val milkPrice = PricingPolicy(milk.name, milk.price.amount.toDouble, 1.0)
  val milkHalf = PricingPolicy(milk.name, milk.price.amount.toDouble, 0.5)
  val milkFree = PricingPolicy(milk.name, milk.price.amount.toDouble, 0.0)
  
  val pbPrice = PricingPolicy(peanutbutter.name, peanutbutter.price.amount.toDouble, 1.0)
  val pbHalf = PricingPolicy(peanutbutter.name, peanutbutter.price.amount.toDouble, 0.5)

  val bundles = Map(
    uniqueKey(List(milkPrice)) -> createBundle(List(milkPrice)),
    uniqueKey(List(breadPrice)) -> createBundle(List(breadPrice)),
    uniqueKey(List(cerealPrice)) -> createBundle(List(cerealPrice)),
    uniqueKey(List(cheesePrice)) -> createBundle(List(cheesePrice)),
    uniqueKey(List(pbPrice)) -> createBundle(List(pbPrice)),
    uniqueKey(List(applePrice)) -> createBundle(List(applePrice)),
    uniqueKey(List(applePrice, applePrice, pbHalf)) -> createBundle(List(applePrice, applePrice, pbHalf)),
    uniqueKey(List(breadPrice, breadPrice, pbHalf)) -> createBundle(List(breadPrice, breadPrice, pbHalf)),
    uniqueKey(List(milkPrice, milkPrice, cheeseHalf)) -> createBundle(List(milkPrice, milkPrice, cheeseHalf)),
    uniqueKey(List(cerealPrice, cerealPrice, cerealHalf)) -> createBundle(List(cerealPrice, cerealPrice, cerealHalf)),
    uniqueKey(List(cerealPrice, cerealPrice, cerealPrice, milkFree)) -> createBundle(List(cerealPrice, cerealPrice, cerealPrice, milkFree)),
    uniqueKey(List(applePrice, applePrice, applePrice, appleFree)) -> createBundle(List(applePrice, applePrice, applePrice, appleFree)),
    uniqueKey(List(breadPrice, breadFree)) -> createBundle(List(breadPrice, breadFree)),
    uniqueKey(List(milkPrice, milkFree)) -> createBundle(List(milkPrice, milkFree)),
    uniqueKey(List(pbPrice, pbHalf)) -> createBundle(List(pbPrice, pbHalf)),
    uniqueKey(List(cheesePrice, cheeseHalf)) -> createBundle(List(cheesePrice, cheeseHalf))
  )
  
}
