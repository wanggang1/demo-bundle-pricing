package com.bundlepricing.legacy

import com.bundlepricing.{TestData, UnitSpec}
import com.bundlepricing.domains._
import com.bundlepricing.repos._

import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Await

/**
 * Unit tests for Inventory
 */
class InventorySpecs extends UnitSpec with TestData with ScalaFutures {

  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global
    
  "Inventory" must "create Item" in new InventoryTestCxt {
    Given("instance of Inventory")
    
    When("\"Bread\" for 1.99 is added")
    Await.ready(inventory.addItem("Bread", 1.99), 100 milliseconds)
    
    Then("Bread must be in ItemRepo")
    inventory.itemRepo.getByKey("Bread") match {
      case Some(Item(_, name, price)) =>
        name mustBe "Bread"
        price.value mustBe 1.99
      case None => fail("create Item failed")
    }
  }
  
  it must "retrieve Item by name" in new InventoryTestCxt {
    Given("instance of Inventory")
    
    When("\"Bread\" for 1.99 is added")
    Await.ready(inventory.addItem("Bread", 1.99), 100 milliseconds)
    
    Then("getItem must return Bread")
    val resultFuture = inventory.getItem("Bread")
    whenReady(resultFuture) { item =>
      item.name mustBe "Bread"
      item.price.value mustBe 1.99
    }
  }
  
  it must "fail if Item does not exist" in new InventoryTestCxt {
    Given("instance of Inventory")

    When("nothing is added")
    
    Then("getItem must return Failure")
    val resultFuture = inventory.getItem("Bread")
    whenReady(resultFuture.failed) { e =>
      e.getMessage mustBe "Item Not Found"
    }
  }

  it must "create Bundle" in new InventoryTestCxt {
    Given("instance of Inventory")

    When("a bundled price is added")
    val expected = Bundle(List(milk, bread), buy1Get2ndHalf)
    Await.ready(inventory.addBundledPrice(List(milk, bread), buy1Get2ndHalf), 100 milliseconds)

    Then("the bundle must be in BundleRepo")
    inventory.bundleRepo.getByKey("MilkBread") match {
      case Some(bundle) => 
        bundle.items mustBe expected.items
        bundle.key mustBe expected.key
        bundle.price mustBe expected.price
      case None => fail("create Bundle failed")
    }
  }

  it must "retrieve all bundles associated with permutation of bundle key" in new InventoryTestCxt {
    Given("instance of Inventory")

    When("add bundled prices")
    val expected1 = Bundle(List(milk, bread), buy1Get2ndHalf)
    Await.ready(inventory.addBundledPrice(List(milk, bread), buy1Get2ndHalf), 100 milliseconds)
    val expected2 = Bundle(List(cereal, cereal, milk), buy2Get3rdHalf)
    Await.ready(inventory.addBundledPrice(List(cereal, cereal, milk), buy2Get3rdHalf), 100 milliseconds)

    Then("getBundles must return all bundles associated with bundle key permutation")
    val resultFuture = inventory.getBundles
    whenReady(resultFuture) { bundles =>
      bundles.size mustBe 5
      val b2_1 = bundles("MilkBread")
      val b2_2 = bundles("BreadMilk")
      b2_1.items mustBe expected1.items
      b2_1.key mustBe expected1.key
      b2_1.price mustBe expected1.price
      b2_2 mustBe b2_1
      val b3_1 = bundles("CerealCerealMilk")
      val b3_2 = bundles("CerealMilkCereal")
      val b3_3 = bundles("MilkCerealCereal")
      b3_1.items mustBe expected2.items
      b3_1.key mustBe expected2.key
      b3_1.price mustBe expected2.price
      b3_2 mustBe b3_1
      b3_3 mustBe b3_1
    }
  }
  
  class InventoryTestCxt {
    val inventory = new Inventory with ItemRepoComponent with BundleRepoComponent {
      val itemRepo = new ItemRepo with InMemoryRepository
      val bundleRepo = new BundleRepo with InMemoryRepository
    }
  }

}
