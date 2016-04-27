package com.bundlepricing

import com.bundlepricing.core._
import com.bundlepricing.domains._
import com.bundlepricing.repos._

import scala.concurrent.{Await, ExecutionContext, Future}

object DemoMongoDB {

  /**
   * Demo Bundled Price APIs
   */
  def main (args: Array[String]): Unit = {
    import scala.concurrent.duration._
    import scala.concurrent.ExecutionContext.Implicits.global

    implicit val inventory = new Inventory with ItemRepoComponent with BundleRepoComponent {
      import com.bundlepricing.repos.Implicits.Salat._
      
      val itemRepo = new ItemMongoRepo(Settings.dbName, Item.collectionName) with SalatRepository
      val bundleRepo = new BundleMongoRepo(Settings.dbName, Bundle.collectionName) with SalatRepository
    }
    
    val bundlePrice = new BundlePrice
    import SampleData._

    /*
    Await.ready(populateItems(inventory), 1000 milliseconds)
    inventory.showItems()
    inventory.showBundles()
    * 
    */

    println("--Purchase: Bread, Bread, PeanutButter, Milk, Cereal, Cereal, Cereal, Milk---")
    val shoppingcart: List[Item] = Await.result(shoppingCart(inventory), 1000 milliseconds) 
    println("")

    /*
    Await.ready(populateBundles(inventory), 1000 milliseconds)
    inventory.showBundles()
    * 
    */
    
    val optimizedPrice = Await.result(bundlePrice.pricing(shoppingcart), 1 second) 
    println("")
    println(s"Optimized Cost $$$optimizedPrice")

  }
  
}

