package com.bundlepricing.repos

import com.bundlepricing.{TestData, UnitSpec}

/**
 * Unit tests for ItemRepo
 */
class ItemRepoSpecs extends UnitSpec with TestData {

  "ItemRepo" must "retrieve ID from entity" in {
    val itemRepo = new ItemRepo
    
    Given("Item(\"Bread\", 1.99)")
    
    Then("the ID must be Bread")
    itemRepo.id(Bread) mustBe "Bread"
  }
  
  it must "insert an Item and retrieve it by ID" in {
    Given("instance of ItemRepo")
    val itemRepo = new ItemRepo
    
    When("insert Item(\"Bread\", 1.99)")
    itemRepo.insert(Bread)
    
    Then("Bread must be retrieved using ID 'Bread'")
    itemRepo.get("Bread") mustBe Some(Bread)
  }
  
  it must "retrieve None if ID is not found in repo" in {
    Given("instance of ItemRepo")
    val itemRepo = new ItemRepo
    
    When("insert Item(\"Apple\", 1.00)")
    itemRepo.insert(Apple)
    
    Then("None must be retrieved using ID 'Bread'")
    itemRepo.get("Bread") mustBe None
  }
  
  it must "retrieve all Items in repo" in {
    Given("instance of ItemRepo")
    val itemRepo = new ItemRepo
    
    When("insert Item(\"Apple\", 1.00), Item(\"Bread\", 1.99), Item(\"Milk\", 2.99)")
    itemRepo.insert(Apple)
    itemRepo.insert(Bread)
    itemRepo.insert(Milk)
    
    Then("getAll must return all three Items")
    val result = itemRepo.getAll
    result.size mustBe 3
    result("Apple") mustBe Apple
    result("Milk") mustBe Milk
    result("Bread") mustBe Bread
  }

}
