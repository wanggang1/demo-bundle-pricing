package com.bundlepricing

import com.bundlepricing.domains._
import org.bson.types.ObjectId
import squants.market.USD

trait TestData {

  val milk = Item(new ObjectId, "Milk", USD(2.99))
  val bread = Item(new ObjectId, "Bread", USD(1.99))
  val cereal = Item(new ObjectId, "Cereal", USD(2.50))
  val cheese = Item(new ObjectId, "SlicedCheese", USD(4.50))
  val peanutbutter = Item(new ObjectId, "PeanutButter", USD(2.50))
  val apple = Item(new ObjectId, "Apple", USD(1.00))

  val bundles = Map(
    Bundle.bundleKey(List(milk)) -> Bundle(List(milk), unitPrice),
    Bundle.bundleKey(List(bread)) -> Bundle(List(bread), unitPrice),
    Bundle.bundleKey(List(cereal)) -> Bundle(List(cereal), unitPrice),
    Bundle.bundleKey(List(cheese)) -> Bundle(List(cheese), unitPrice),
    Bundle.bundleKey(List(peanutbutter)) -> Bundle(List(peanutbutter), unitPrice),
    Bundle.bundleKey(List(apple)) -> Bundle(List(apple), unitPrice),
    Bundle.bundleKey(List(apple, apple, peanutbutter)) -> Bundle(List(apple, apple, peanutbutter), buy2Get3rdHalf),
    Bundle.bundleKey(List(bread, bread, peanutbutter)) -> Bundle(List(bread, bread, peanutbutter), buy2Get3rdHalf),
    Bundle.bundleKey(List(milk, milk, cheese)) -> Bundle(List(milk, milk, cheese), buy2Get3rdHalf),
    Bundle.bundleKey(List(cereal, cereal, cereal)) -> Bundle(List(cereal, cereal, cereal), buy2Get3rdHalf),
    Bundle.bundleKey(List(cereal, cereal, cereal, milk)) -> Bundle(List(cereal, cereal, cereal, milk), buy3Get4thFree),
    Bundle.bundleKey(List(apple, apple, apple, apple)) -> Bundle(List(apple, apple, apple, apple), buy3Get4thFree),
    Bundle.bundleKey(List(bread, bread)) -> Bundle(List(bread, bread), buy1Get1Free),
    Bundle.bundleKey(List(milk, milk)) -> Bundle(List(milk, milk), buy1Get1Free),
    Bundle.bundleKey(List(peanutbutter, peanutbutter)) -> Bundle(List(peanutbutter, peanutbutter), buy1Get2ndHalf),
    Bundle.bundleKey(List(cheese, cheese)) -> Bundle(List(cheese, cheese), buy1Get2ndHalf)
  )
  
}
