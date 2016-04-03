package com.bundlepricing

import com.bundlepricing.domains.Bundle
import com.bundlepricing.domains.Item

/**
 * Unit tests for Bundle
 */
class BundleSpecs extends UnitSpec with TestData {

  import Bundle._
  import Item._
  
  "Bundle" must "generate bundle key with given items" in {
    Given("items List(Milk, Bread, Apple, Bread)")
    val items = List(Milk, Bread, Apple, Bread)

    Then("the key must be MilkBreadAppleBread")
    bundleKey(items) mustBe "MilkBreadAppleBread"
  }

  it must "generate collection of keys for permutaion of items" in {
    Given("items List(Milk, Bread, Apple, Bread)")
    val items = List(Milk, Bread, Apple, Bread)

    Then("there must be 12 possible keys")
    val possibleKeys = keyPermutations(items)
    possibleKeys.size mustBe 12
    
    And("must contain alll possible keys")
    possibleKeys.contains("MilkBreadBreadApple") mustBe true
    possibleKeys.contains("MilkBreadAppleBread") mustBe true
    possibleKeys.contains("MilkAppleBreadBread") mustBe true
    possibleKeys.contains("BreadMilkBreadApple") mustBe true
    possibleKeys.contains("BreadMilkAppleBread") mustBe true
    possibleKeys.contains("BreadBreadMilkApple") mustBe true
    possibleKeys.contains("BreadBreadAppleMilk") mustBe true
    possibleKeys.contains("BreadAppleMilkBread") mustBe true
    possibleKeys.contains("BreadAppleBreadMilk") mustBe true
    possibleKeys.contains("AppleMilkBreadBread") mustBe true
    possibleKeys.contains("AppleBreadMilkBread") mustBe true
    possibleKeys.contains("AppleBreadBreadMilk") mustBe true
  }
  
  it must "generate an empty key with empty items" in {
    Given("items List()")
    val items = List()

    Then("the keys must be only 1 empty string")
    keyPermutations(items) mustBe List("")
  }

}
