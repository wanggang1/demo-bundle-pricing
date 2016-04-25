package com.bundlepricing.repos

import com.bundlepricing.{TestData, UnitSpec}
import com.bundlepricing.core._
import com.bundlepricing.domains.Bundle

/**
 * Unit tests for BundleRepo
 */
class BundleRepoSpecs extends UnitSpec with TestData {

  "BundleRepo" must "retrieve ID from entity" in new BundleRepoTestCxt {
    Given("a bundle of Bread and Milk")
    val bundle = Bundle(List(Milk, Bread), buy1Get2ndHalf)
    
    Then("the ID must be MilkBread")
    bundleRepoComponent.bundleRepo.id(bundle) mustBe "MilkBread"
  }
  
  it must "insert a Bundle and retrieve it by ID" in new BundleRepoTestCxt {
    Given("instance of BundleRepo")

    When("insert a bundle of Bread and Milk")
    val bundle = Bundle(List(Milk, Bread), buy1Get2ndHalf)
    bundleRepoComponent.bundleRepo.insert(bundle)
    
    Then("bundle must be retrieved using ID 'MilkBread'")
    bundleRepoComponent.bundleRepo.get("MilkBread") mustBe Some(bundle)
  }
  
  it must "retrieve None if ID is not found in repo" in new BundleRepoTestCxt {
    Given("instance of BundleRepo")

    When("insert a bundle of Bread and Milk")
    val bundle = Bundle(List(Milk, Bread), buy1Get2ndHalf)
    bundleRepoComponent.bundleRepo.insert(bundle)
    
    Then("None must be retrieved using ID 'AppleApple'")
    bundleRepoComponent.bundleRepo.get("AppleApple") mustBe None
  }
  
  it must "retrieve all Bundles in repo" in new BundleRepoTestCxt {
    Given("instance of BundleRepo")

    When("insert three bundles")
    val bundle1 = Bundle(List(Milk, Bread), buy1Get2ndHalf)
    bundleRepoComponent.bundleRepo.insert(bundle1)
    val bundle2 = Bundle(List(Apple, Apple), buy1Get1Free)
    bundleRepoComponent.bundleRepo.insert(bundle2)
    val bundle3 = Bundle(List(Cereal, Cereal, Cereal, Cereal), buy3Get4thFree)
    bundleRepoComponent.bundleRepo.insert(bundle3)
    
    Then("getAll must return all three Bundles")
    val result = bundleRepoComponent.bundleRepo.getAll
    result.size mustBe 3
    result("AppleApple") mustBe bundle2
    result("MilkBread") mustBe bundle1
    result("CerealCerealCerealCereal") mustBe bundle3
  }

  class BundleRepoTestCxt {
    val bundleRepoComponent = new BundleRepoComponent {
      val bundleRepo = new BundleRepo with InMemoryRepository
    }
  }
    
}
