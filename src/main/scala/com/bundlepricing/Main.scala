package com.bundlepricing

import com.bundlepricing.core._
import com.bundlepricing.domains.Item
import com.bundlepricing.repos._

import scala.concurrent.{Await, ExecutionContext, Future}

object Main {

  /**
   * Demo Bundled Price APIs
   */
  def main (args: Array[String]): Unit = {
    import Demo._
    import scala.concurrent.duration._
    import scala.concurrent.ExecutionContext.Implicits.global

    implicit val itemRepo = new ItemRepo
    implicit val bundleRepo = new BundleRepo
    implicit val inventory = new Inventory
    val bundlePrice = new BundlePrice

    Await.ready(populateItems(inventory), 100 milliseconds)
    inventory.showItems()
    inventory.showBundles()

    println("--Purchase: Bread, Bread, PeanutButter, Milk, Cereal, Cereal, Cereal, Milk---")
    val shoppingcart: List[Item] = Await.result(shoppingCart(inventory), 100 milliseconds) 
    println("")

    Await.ready(populateBundles(inventory), 100 milliseconds)
    inventory.showBundles()
    
    val optimizedPrice = Await.result(bundlePrice.pricing(shoppingcart), 1 second) 
    println("")
    println(s"Optimized Cost $$$optimizedPrice")
  }
  
}

object Demo {
    
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
