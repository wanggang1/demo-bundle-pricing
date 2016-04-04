package com.bundlepricing.core

import com.bundlepricing.UnitSpec
import com.bundlepricing.domains.Item
import com.bundlepricing.repos._

import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.Await

/**
 * Unit tests for Inventory
 */
class InventorySpecs extends UnitSpec with ScalaFutures {

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
  
  "Inventory" must "retrieve Item by name" in {
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
  
  "Inventory" must "fail if Item does not exist" in {
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

}
