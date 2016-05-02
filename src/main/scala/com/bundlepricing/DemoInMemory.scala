package com.bundlepricing

import com.bundlepricing.legacy._
import com.bundlepricing.domains.Item
import com.bundlepricing.repos._

import scala.concurrent.{Await, ExecutionContext, Future}

object DemoInMemory {

  /**
   * Demo Bundled Price APIs
   */
  def main (args: Array[String]): Unit = {
    import scala.concurrent.duration._
    import scala.concurrent.ExecutionContext.Implicits.global

    implicit val inventory = new Inventory with ItemRepoComponent with BundleRepoComponent {
      val itemRepo = new ItemRepo with InMemoryRepository
      val bundleRepo = new BundleRepo with InMemoryRepository
    }

    val bundlePrice = new BundlePrice
    import SampleData._

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
