package com.bundlepricing.legacy

import com.bundlepricing.domains._
import scala.concurrent.{ExecutionContext, Future}

object SampleData {
    
  def populateItems(inventory: Inventory): Future[Unit] = {
    inventory.addItem("Milk", 2.99)
    inventory.addItem("Bread", 1.99)
    inventory.addItem("Cereal", 2.50)
    inventory.addItem("SlicedCheese", 4.50)
    inventory.addItem("PeanutButter", 2.50)
    inventory.addItem("Apple", 1.00)
  }

  def populateBundles(inventory: Inventory)(implicit ec: ExecutionContext): Future[Unit] = {
    //execute Futures in parallel
    val apple = inventory.getItem("Apple")
    val bread = inventory.getItem("Bread")
    val cereal = inventory.getItem("Cereal")
    val milk = inventory.getItem("Milk")
    val slicedCheese = inventory.getItem("SlicedCheese")
    val peanutbutter = inventory.getItem("PeanutButter")

    for {
      a <- apple
      p <- peanutbutter
    } yield inventory.addBundledPrice(List(a, a, p), buy2Get3rdHalf)

    for {
      b <- bread
      p <- peanutbutter
    } yield inventory.addBundledPrice(List(b, b, p), buy2Get3rdHalf)

    for {
      c <- cereal
      m <- milk
    } yield inventory.addBundledPrice(List(c, c, c, m), buy3Get4thFree)

    for {
      m <- milk
      sc <- slicedCheese
    } yield inventory.addBundledPrice(List(m, m, sc), buy2Get3rdHalf)

    apple flatMap {a => inventory.addBundledPrice(List.fill(4)(a), buy3Get4thFree)}

    bread flatMap {b => inventory.addBundledPrice(List.fill(2)(b), buy1Get1Free)}

    cereal flatMap {c => inventory.addBundledPrice(List.fill(3)(c), buy2Get3rdHalf)}

    milk flatMap {m => inventory.addBundledPrice(List.fill(2)(m), buy1Get1Free)}

    peanutbutter flatMap {p => inventory.addBundledPrice(List.fill(2)(p), buy1Get2ndHalf)}

    slicedCheese flatMap { sc => inventory.addBundledPrice(List.fill(2)(sc), buy1Get2ndHalf)}
  }

  def shoppingCart(inventory: Inventory)(implicit ec: ExecutionContext): Future[List[Item]] = {
    //execute Futures in parallel
    val bread = inventory.getItem("Bread")
    val peanutbutter = inventory.getItem("PeanutButter")
    val milk = inventory.getItem("Milk")
    val cereal = inventory.getItem("Cereal")

    for {
      b <- bread
      p <- peanutbutter
      m <- milk
      c <- cereal
    } yield List(b, b, p, m, c, c, c, m)
  }
    
}