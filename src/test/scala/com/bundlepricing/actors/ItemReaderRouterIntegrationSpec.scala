package com.bundlepricing.actors

import com.bundlepricing.{ActorSpec, TestData, Settings}
import com.bundlepricing.domains._

import akka.actor.{ActorRef, Props}
import akka.testkit.{ TestActorRef, ImplicitSender, TestProbe, TestActor }
import akka.util.Timeout
import org.scalamock.scalatest.MockFactory
import org.scalatest.GivenWhenThen
import scala.concurrent.duration._

class ItemReaderRouterIntegrationSpec extends ActorSpec with ImplicitSender with MockFactory with GivenWhenThen with TestData {
  import ItemReaderRouter._
  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val timeout: Timeout = Settings.webServiceTimeout
  
  "A ItemReaderRouter" must "collect Items" in new Ctx {
    Given("itemRepo that returns items by their keys")
    inAnyOrder {
      (itemRepo.getByKey _).expects(peanutbutter.name).returning(Some(peanutbutter)).once
      (itemRepo.getByKey _).expects(milk.name).returning(Some(milk)).once
      (itemRepo.getByKey _).expects(bread.name).returning(Some(bread)).once
      (itemRepo.getByKey _).expects(apple.name).returning(Some(apple)).twice
    }
    
    And("an ItemReaderRouter instance")
    implicit val repo = itemRepo
    val itemReaderRouter =  system.actorOf(Props(new ItemReaderRouter))
    
    When("a list of Item keys is sent to ItemReaderRouter")
    itemReaderRouter ! Get(List("PeanutButter", "Milk", "Apple", "Bread", "Apple"))

    Then("a list of Items must be returned")  
    val result: ItemResults = expectMsgClass(500 millis, classOf[ItemResults])
    result.items.size mustBe 5
    result.items.contains(milk) mustBe true
    result.items.contains(bread) mustBe true
    result.items.contains(peanutbutter) mustBe true
    val apples = result.items.collect {case i if i == apple => i}
    apples.size mustBe 2
  }
  
  it  must "collect NO Items if one of the keys not exist" in new Ctx {
    Given("itemRepo that returns items by their keys")
    inAnyOrder {
      (itemRepo.getByKey _).expects(peanutbutter.name).returning(Some(peanutbutter)).once
      (itemRepo.getByKey _).expects(milk.name).returning(Some(milk)).once
      (itemRepo.getByKey _).expects(bread.name).returning(Some(bread)).once
      (itemRepo.getByKey _).expects(apple.name).returning(Some(apple)).once
      (itemRepo.getByKey _).expects(cereal.name).returning(None).once
    }
    
    And("an ItemReaderRouter instance")
    implicit val repo = itemRepo
    val itemReaderRouter =  system.actorOf(Props(new ItemReaderRouter))
    
    When("a list of Item keys is sent containing a key not existing (Cereal)")
    itemReaderRouter ! Get(List("PeanutButter", "Milk", "Cereal", "Bread", "Apple"))

    Then("an empty list of Items must be returned")  
    val result: ItemResults = expectMsgClass(500 millis, classOf[ItemResults])
    result.items mustBe Nil
  }

  trait Ctx {
    import com.bundlepricing.repos.ItemMongoRepo
    import com.bundlepricing.repos.Implicits.Salat._
    
    implicit val itemRepo = mock[ItemMongoRepo]
  }
  
}
