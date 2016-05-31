package com.bundlepricing.repos

import com.bundlepricing.{TestData, UnitSpec}

/**
 * Unit tests for ItemRepo
 */
class ItemMemoryRepoSpecs extends UnitSpec with TestData {

  "ItemRepo" must "retrieve Key from entity" in new ItemRepoTestCxt {
    Given("Item(\"Bread\", 1.99)")
    
    Then("the Key must be Bread")
    itemRepoComponent.itemRepo.key(bread) mustBe "Bread"
  }
  
  it must "insert an Item and retrieve it by Key" in new ItemRepoTestCxt {
    Given("instance of ItemRepo")
    
    When("insert Item(\"Bread\", 1.99)")
    itemRepoComponent.itemRepo.insert(bread)
    
    Then("Bread must be retrieved using Key 'Bread'")
    itemRepoComponent.itemRepo.getByKey("Bread") mustBe Some(bread)
  }
  
  it must "retrieve None if Key is not found in repo" in new ItemRepoTestCxt {
    Given("instance of ItemRepo")
    
    When("insert Item(\"Apple\", 1.00)")
    itemRepoComponent.itemRepo.insert(apple)
    
    Then("None must be retrieved using Key 'Bread'")
    itemRepoComponent.itemRepo.getByKey("Bread") mustBe None
  }
  
  it must "retrieve all Items in repo" in new ItemRepoTestCxt {
    Given("instance of ItemRepo")
    
    When("insert Item(\"Apple\", 1.00), Item(\"Bread\", 1.99), Item(\"Milk\", 2.99)")
    itemRepoComponent.itemRepo.insert(apple)
    itemRepoComponent.itemRepo.insert(bread)
    itemRepoComponent.itemRepo.insert(milk)
    
    Then("getAll must return all three Items")
    val result = itemRepoComponent.itemRepo.getAll
    result.size mustBe 3
    result("Apple") mustBe apple
    result("Milk") mustBe milk
    result("Bread") mustBe bread
  }
  
  class ItemRepoTestCxt {
    val itemRepoComponent = new ItemRepoComponent {
      val itemRepo = new ItemRepo with InMemoryRepository
    }
  }
  
}
