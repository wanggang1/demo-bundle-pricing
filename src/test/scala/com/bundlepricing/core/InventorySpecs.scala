package com.bundlepricing.core

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
    
  "Inventory" must "create Item" in {
    implicit val itemRepo = new ItemRepo
    implicit val bundleRepo = new BundleRepo
    
    Given("instance of Inventory")
    val inventory = new Inventory
    
    When("\"Bread\" for 1.99 is added")
    Await.ready(inventory.addItem("Bread", 1.99), 100 milliseconds)
    
    Then("Bread must be in ItemRepo")
    itemRepo.get("Bread") mustBe Some(Item("Bread", 1.99))
  }
  
  it must "retrieve Item by name" in {
    implicit val itemRepo = new ItemRepo
    implicit val bundleRepo = new BundleRepo
    
    Given("instance of Inventory")
    val inventory = new Inventory
    
    When("\"Bread\" for 1.99 is added")
    Await.ready(inventory.addItem("Bread", 1.99), 100 milliseconds)
    
    Then("getItem must return Bread")
    val resultFuture = inventory.getItem("Bread")
    whenReady(resultFuture) { item =>
      item mustBe Item("Bread", 1.99)
    }
  }
  
  it must "fail if Item does not exist" in {
    implicit val itemRepo = new ItemRepo
    implicit val bundleRepo = new BundleRepo
    
    Given("instance of Inventory")
    val inventory = new Inventory
    
    When("nothing is added")
    
    Then("getItem must return Failure")
    val resultFuture = inventory.getItem("Bread")
    whenReady(resultFuture.failed) { e =>
      e.getMessage mustBe "Item Not Found"
    }
  }

  it must "create Bundle" in {
    implicit val itemRepo = new ItemRepo
    implicit val bundleRepo = new BundleRepo

    Given("instance of Inventory")
    val inventory = new Inventory

    When("a bundled price is added")
    Await.ready(inventory.addBundledPrice(List(Milk, Bread), buy1Get2ndHalf), 100 milliseconds)

    Then("the bundle must be in BundleRepo")
    bundleRepo.get("MilkBread") mustBe Some(Bundle(List(Milk, Bread), buy1Get2ndHalf))
  }

  it must "retrieve all bundles associated with permutation of bundle key" in {
    implicit val itemRepo = new ItemRepo
    implicit val bundleRepo = new BundleRepo

    Given("instance of Inventory")
    val inventory = new Inventory

    When("add bundled prices")
    val expected1 = Bundle(List(Milk, Bread), buy1Get2ndHalf)
    Await.ready(inventory.addBundledPrice(List(Milk, Bread), buy1Get2ndHalf), 100 milliseconds)
    val expected2 = Bundle(List(Cereal, Cereal, Milk), buy2Get3rdHalf)
    Await.ready(inventory.addBundledPrice(List(Cereal, Cereal, Milk), buy2Get3rdHalf), 100 milliseconds)

    Then("getBundles must return all bundles associated with bundle key permutation")
    val resultFuture = inventory.getBundles
    whenReady(resultFuture) { bundles =>
      bundles.size mustBe 5
      bundles("MilkBread") mustBe expected1
      bundles("BreadMilk") mustBe expected1
      bundles("CerealCerealMilk") mustBe expected2
      bundles("CerealMilkCereal") mustBe expected2
      bundles("MilkCerealCereal") mustBe expected2
    }
  }

}
