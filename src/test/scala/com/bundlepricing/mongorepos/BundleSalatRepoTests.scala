package com.bundlepricing.mongorepos

import com.bundlepricing.{TestData, UnitSpec}
import com.bundlepricing.domains._
import com.bundlepricing.repos.{BundleRepoComponent, BundleMongoRepo, SalatRepository}

import com.mongodb.casbah.MongoClient
import squants.market.USD

/**
 * Unit tests for BundleRepo with SalatRepository
 * 
 * This test class is added to EmbeddedMongoSuite.  Or, by itself, it will not be
 * picked up by running 'sbt test' because class name is not ended with 'Spec(s)???
 */
class BundleSalatRepoTests(mongoClient: ⇒ MongoClient) extends UnitSpec with TestData {
  import Bundle._
  
  "BundleSalatRepo" must "retrieve Key from entity" in new BundleRepoTestCxt {
    Given("a bundle of Bread and Milk")
    val bundle = createBundle(List(milkPrice, breadHalf))
    
    Then("the Key must be MilkBread")
    bundleRepoComponent.bundleRepo.key(bundle) mustBe "MilkBread"
  }

  it must "insert a Bundle and retrieve it by Key" in new BundleRepoTestCxt {
    Given("instance of BundleRepo")

    When("insert a bundle of Bread and Milk")
    val bundle = createBundle(List(milkPrice, breadHalf))
    bundleRepoComponent.bundleRepo.insert(bundle)
    
    Then("bundle must be retrieved using Key 'MilkBread'")
    bundleRepoComponent.bundleRepo.getByKey("MilkBread") mustBe Some(bundle)
  }
  
  it must "retrieve Bundle by id" in new BundleRepoTestCxt {
    Given("instance of BundleRepo a bundle of Bread and Milk inserted")
    val bundle = createBundle(List(milkPrice, breadHalf))
    bundleRepoComponent.bundleRepo.insert(bundle)
    
    Then("bundle must be retrieved id")
    bundleRepoComponent.bundleRepo.get(bundle.id) mustBe Some(bundle)
  }
  
  it must "retrieve None if Key is not found in repo" in new BundleRepoTestCxt {
    Given("instance of BundleRepo")

    When("insert a bundle of Bread and Milk")
    val bundle = createBundle(List(milkPrice, breadHalf))
    bundleRepoComponent.bundleRepo.insert(bundle)
    
    Then("None must be retrieved using Key 'AppleApple'")
    bundleRepoComponent.bundleRepo.getByKey("AppleApple") mustBe None
  }
  
  it must "retrieve all Bundles in repo" in new BundleRepoTestCxt {
    Given("instance of BundleRepo")

    When("insert three bundles")
    val bundle1 = createBundle(List(milkPrice, breadHalf))
    bundleRepoComponent.bundleRepo.insert(bundle1)
    val bundle2 = createBundle(List(applePrice, appleFree))
    bundleRepoComponent.bundleRepo.insert(bundle2)
    val bundle3 = createBundle(List(cerealPrice, cerealPrice, cerealPrice, cerealFree))
    bundleRepoComponent.bundleRepo.insert(bundle3)
    
    Then("getAll must return all three Bundles")
    val result = bundleRepoComponent.bundleRepo.getAll
    result.size mustBe 3
    result("AppleApple") mustBe bundle2
    result("MilkBread") mustBe bundle1
    result("CerealCerealCerealCereal") mustBe bundle3
  }

  it must "delete Bundle by id" in new BundleRepoTestCxt {
    Given("instance of BundleRepo and a bundle of Milk and Bread")
    val bundle = createBundle(List(milkPrice, breadHalf))
    bundleRepoComponent.bundleRepo.insert(bundle)
    
    When("delete the bundle by id")
    bundleRepoComponent.bundleRepo.delete(bundle.id)
    
    Then("the bundle must NOT be in MongoBD")
    bundleRepoComponent.bundleRepo.get(bundle.id) mustBe None
  }
  
  it must "update Bundle in MongoDB" in new BundleRepoTestCxt {
    Given("instance of BundleRepo and a bundle of Milk and Bread")
    val bundle = createBundle(List(milkPrice, breadHalf))
    bundleRepoComponent.bundleRepo.insert(bundle)
    
    When("update the bundle with new pricing $0.99")
    val bundleNewPrice = bundle.copy(pricings = List(milkPrice, breadFree))
    bundleRepoComponent.bundleRepo.upsert(bundleNewPrice)
    
    Then("this bundle must have new price")
    bundleRepoComponent.bundleRepo.get(bundle.id) mustBe Some(bundleNewPrice)
  }
  
  class BundleRepoTestCxt {
    import java.util.UUID
    
    val bundleRepoComponent = new BundleRepoComponent {
      import com.bundlepricing.repos.Implicits.Salat.salatContext
      implicit val mc = mongoClient //use test mongoClient instead the one from application
      val bundleRepo = new BundleMongoRepo(dbName = "test-db", collectionName = UUID.randomUUID.toString) with SalatRepository
    }
  }
    
}
