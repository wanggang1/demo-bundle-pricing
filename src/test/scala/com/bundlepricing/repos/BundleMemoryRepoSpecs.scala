package com.bundlepricing.repos

import com.bundlepricing.{TestData, UnitSpec}
import com.bundlepricing.domains._

/**
 * Unit tests for BundleRepo
 */
class BundleMemoryRepoSpecs extends UnitSpec with TestData {
  import Bundle._

  "BundleRepo" must "retrieve Key from entity" in new BundleRepoTestCxt {
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

  class BundleRepoTestCxt {
    val bundleRepoComponent = new BundleRepoComponent {
      val bundleRepo = new BundleRepo with InMemoryRepository
    }
  }
    
}
