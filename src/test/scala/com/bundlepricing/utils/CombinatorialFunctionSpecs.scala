package com.bundlepricing.utils

import com.bundlepricing.{TestData, UnitSpec}

/**
 * Unit tests for CombinatorialFunction
 */
class CombinatorialFunctionSpecs extends UnitSpec with TestData {

  import CombinatorialFunction._
  
  "CombinatorialFunction.subs" must "generate all possible sub-combinations with given items" in {
    Given("items List(Milk, Bread, Apple, Bread)")
    val items = List(Milk, Bread, Apple, Bread)

    Then("there must be 16 possible combinations")
    val combinations = subsets(items)
    combinations.size mustBe 16
    
    And("must contain all possible combinations")
    combinations.contains(List()) mustBe true
    combinations.contains(List(Bread)) mustBe true
    combinations.contains(List(Apple)) mustBe true
    combinations.contains(List(Milk)) mustBe true
    combinations.contains(List(Bread, Milk)) mustBe true
    combinations.contains(List(Apple, Milk)) mustBe true
    combinations.contains(List(Apple, Bread)) mustBe true
    combinations.contains(List(Bread, Bread)) mustBe true
    combinations.contains(List(Bread, Apple)) mustBe true
    combinations.contains(List(Apple, Bread, Milk)) mustBe true
    combinations.contains(List(Bread, Bread, Milk)) mustBe true
    combinations.contains(List(Bread, Apple, Milk)) mustBe true
    combinations.contains(List(Bread, Apple, Bread)) mustBe true
    combinations.contains(List(Bread, Apple, Bread, Milk)) mustBe true
    
    And("must contain 2 List(Bread) and 2 List(Bread, Milk)")
    combinations.filter(_ == List(Bread)).size mustBe 2
    combinations.filter(_ == List(Bread, Milk)).size mustBe 2
  }

  it must "generate an list(Nil) with empty items" in {
    Given("items List()")
    val items = List()

    Then("the combinations must be List(Nil)")
    subsets(items) mustBe List(Nil)
  }

}
