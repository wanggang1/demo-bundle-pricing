package com.bundlepricing.utils

import com.bundlepricing.{TestData, UnitSpec}

/**
 * Unit tests for CombinatorialFunction
 */
class CombinatorialFunctionSpecs extends UnitSpec with TestData {

  import CombinatorialFunction._
  
  "CombinatorialFunction.subs" must "generate all possible sub-combinations with given items" in {
    Given("items List(Milk, Bread, Apple, Bread)")
    val items = List(milk, bread, apple, bread)

    Then("there must be 16 possible combinations")
    val combinations = subsets(items)
    combinations.size mustBe 16
    
    And("must contain all possible combinations")
    combinations.contains(List()) mustBe true
    combinations.contains(List(bread)) mustBe true
    combinations.contains(List(apple)) mustBe true
    combinations.contains(List(milk)) mustBe true
    combinations.contains(List(bread, milk)) mustBe true
    combinations.contains(List(apple, milk)) mustBe true
    combinations.contains(List(apple, bread)) mustBe true
    combinations.contains(List(bread, bread)) mustBe true
    combinations.contains(List(bread, apple)) mustBe true
    combinations.contains(List(apple, bread, milk)) mustBe true
    combinations.contains(List(bread, bread, milk)) mustBe true
    combinations.contains(List(bread, apple, milk)) mustBe true
    combinations.contains(List(bread, apple, bread)) mustBe true
    combinations.contains(List(bread, apple, bread, milk)) mustBe true
    
    And("must contain 2 List(Bread) and 2 List(Bread, Milk)")
    combinations.filter(_ == List(bread)).size mustBe 2
    combinations.filter(_ == List(bread, milk)).size mustBe 2
  }

  it must "generate an list(Nil) with empty items" in {
    Given("items List()")
    val items = List()

    Then("the combinations must be List(Nil)")
    subsets(items) mustBe List(Nil)
  }

}
