package com.bundlepricing.mongorepos

import com.mongodb.casbah.MongoClient
import com.bundlepricing.{TestData, UnitSpec}
import com.bundlepricing.repos.{ItemRepoComponent, ItemMongoRepo, SalatRepository}

import squants.market.USD

/**
 * Unit tests for ItemRepo with SalatRepository
 * 
 * This test class is added to EmbeddedMongoSuite.  Or, by itself, it will not be
 * picked up by running 'sbt test' because class name is not ended with 'Spec(s)???
 */
class ItemSalatRepoTests(mongoClient: ⇒ MongoClient) extends UnitSpec with TestData {

  "ItemSalatRepo" must "retrieve Key from entity" in new ItemRepoTestCxt {
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
  
  it must "retrieve item by id" in new ItemRepoTestCxt {
    Given("instance of ItemRepo and Item(\"Bread\", 1.99) in MongoBD")
    itemRepoComponent.itemRepo.insert(bread)

    Then("Bread must be retrieved using id")
    itemRepoComponent.itemRepo.get(bread.id) mustBe Some(bread)
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
  
  it must "delete item by id" in new ItemRepoTestCxt {
    Given("instance of ItemRepo and Item(\"Bread\", 1.99) in MongoBD")
    itemRepoComponent.itemRepo.insert(bread)
    
    When("delete Item(\"Bread\", 1.99) by id")
    itemRepoComponent.itemRepo.delete(bread.id)
    
    Then("Bread must NOT be in MongoBD")
    itemRepoComponent.itemRepo.get(bread.id) mustBe None
  }
  
  it must "update item in MongoDB" in new ItemRepoTestCxt {
    Given("instance of ItemRepo and Item(\"Bread\", 1.99) in MongoBD")
    itemRepoComponent.itemRepo.insert(bread)
    
    When("update Item(\"Bread\", 1.99) with new price $2.05")
    val breadNewPrice = bread.copy(price = USD(2.05))
    itemRepoComponent.itemRepo.upsert(breadNewPrice)
    
    Then("Bread must have new price")
    itemRepoComponent.itemRepo.get(bread.id) mustBe Some(breadNewPrice)
  }
  
  class ItemRepoTestCxt {
    import java.util.UUID

    val itemRepoComponent = new ItemRepoComponent {
      import com.bundlepricing.repos.Implicits.Salat.salatContext
      implicit val mc = mongoClient //use test mongoClient instead the one from application
      val itemRepo = new ItemMongoRepo(dbName = "test-db", collectionName = UUID.randomUUID.toString) with SalatRepository
    }
  }
  
}
