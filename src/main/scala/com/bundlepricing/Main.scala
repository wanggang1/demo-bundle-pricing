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
    import scala.concurrent.duration._
    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val itemRepo = new ItemRepo
    implicit val bundleRepo = new BundleRepo
    implicit val inventory = new Inventory
    val bundlePrice = new BundlePrice

    Await.ready(populateItems(inventory), 100 milliseconds)
    inventory.showItems()
    inventory.showBundles()

    populateBundles(inventory)
    Thread.sleep(100)
    inventory.showBundles()
    
    println("--Purchase: Bread, Bread, PeanutButter, Milk, Cereal, Cereal, Cereal, Milk---")
    val shoppingcart: List[Item] = Await.result(shoppingCart(inventory), 100 milliseconds) 
    
    val optimizedPrice = Await.result(bundlePrice.pricing(shoppingcart), 1 second) 
    println("")
    println(s"Optimized Cost $$$optimizedPrice")
  }
  
  def populateItems(inventory: Inventory): Future[Unit] = {
    inventory.addItem("Milk", 2.99)
    inventory.addItem("Bread", 1.99)
    inventory.addItem("Cereal", 2.50)
    inventory.addItem("SlicedCheese", 4.50)
    inventory.addItem("PeanutButter", 2.50)
    inventory.addItem("Apple", 1.00)
  }

  def populateBundles(inventory: Inventory)(implicit ec: ExecutionContext): Future[Unit] = {
    for {
      apple <- inventory.getItem("Apple")
      unit = inventory.addBundledPrice(List.fill(4)(apple), buy3Get4thFree)
    } yield unit

    for {
      apple <- inventory.getItem("Apple")
      peanutbutter <- inventory.getItem("PeanutButter")
    } yield inventory.addBundledPrice(List(apple, apple, peanutbutter), buy2Get3rdHalf)

    for {
      bread <- inventory.getItem("Bread")
    } yield inventory.addBundledPrice(List.fill(2)(bread), buy1Get1Free)

    for {
      bread <- inventory.getItem("Bread")
      peanutbutter <- inventory.getItem("PeanutButter")
    } yield inventory.addBundledPrice(List(bread, bread, peanutbutter), buy2Get3rdHalf)

    for {
      cereal <- inventory.getItem("Cereal")
    } yield inventory.addBundledPrice(List.fill(3)(cereal), buy2Get3rdHalf)

    for {
      cereal <- inventory.getItem("Cereal")
      milk <- inventory.getItem("Milk")
    } yield inventory.addBundledPrice(List(cereal, cereal, cereal, milk), buy3Get4thFree)

    for {
      milk <- inventory.getItem("Milk")
    } yield inventory.addBundledPrice(List.fill(2)(milk), buy1Get1Free)

    for {
      milk <- inventory.getItem("Milk")
      slicedCheese <- inventory.getItem("SlicedCheese")
    } yield inventory.addBundledPrice(List(milk, milk, slicedCheese), buy2Get3rdHalf)

    for {
      peanutbutter <- inventory.getItem("PeanutButter")
    } yield inventory.addBundledPrice(List.fill(2)(peanutbutter), buy1Get2ndHalf)

    for {
      slicedCheese <- inventory.getItem("SlicedCheese")
    } yield inventory.addBundledPrice(List.fill(2)(slicedCheese), buy1Get2ndHalf)
  }

  def shoppingCart(inventory: Inventory)(implicit ec: ExecutionContext): Future[List[Item]] =
    for {
      bread <- inventory.getItem("Bread")
      peanutbutter <- inventory.getItem("PeanutButter")
      milk <- inventory.getItem("Milk")
      cereal <- inventory.getItem("Cereal")
    } yield List(bread, bread, peanutbutter, milk, cereal, cereal, cereal, milk)
  
}
