package com.bundlepricing.legacy

import com.bundlepricing.{TestData, UnitSpec}
import com.bundlepricing.domains._
import com.bundlepricing.repos._

import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Await

/**
 * Unit tests for BundlePrice
 */
class BundlePriceSpecs extends UnitSpec with TestData with ScalaFutures {

  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global
    
  "BundlePrice" must "return optimized price" in {
    implicit val inventory = new Inventory with ItemRepoComponent with BundleRepoComponent {
      val itemRepo = new ItemRepo with InMemoryRepository
      val bundleRepo = new BundleRepo with InMemoryRepository
    }
    
    Given("initialized Inventory")
    Await.ready(SampleData.populateItems(inventory), 200 milliseconds)
    Await.ready(SampleData.populateBundles(inventory), 200 milliseconds)
  
    When("giving a cart of items")
    val cart = List(bread, bread, peanutbutter, milk, cereal, cereal, cereal, milk)

    Then("optimized price must be calculated")
    val bundlePrice = new BundlePrice
    val price = Await.result(bundlePrice.pricing(cart), 1 second) 
    price mustBe 13.73
  }
  
}
