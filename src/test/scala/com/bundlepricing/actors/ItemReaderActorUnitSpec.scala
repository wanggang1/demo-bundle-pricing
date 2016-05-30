package com.bundlepricing.actors

import com.bundlepricing.{ActorSpec, TestData}
import com.bundlepricing.domains._
import com.bundlepricing.repos.ItemMongoRepo

import akka.actor.{ActorRef, Props}
import akka.testkit.{ TestActorRef, ImplicitSender, TestProbe }
import org.scalamock.scalatest.MockFactory
import org.scalatest.GivenWhenThen
import scala.concurrent.duration._


class ItemReaderActorUnitSpec extends ActorSpec with ImplicitSender with MockFactory with GivenWhenThen with TestData {

  import scala.concurrent.ExecutionContext.Implicits.global
  import ItemReaderActor._
  import BundleActor._
  
  "A ItemReaderActor" must "return an item when existing in database" in new Ctx {
    Given("itemRepo that returns an item by key")
    (itemRepo.getByKey _).expects(peanutbutter.name).returning(Some(peanutbutter)).once
    
    And("inject itemRepo into a ItemReaderActor")
    val itemReaderActor =  TestActorRef(new ItemReaderActor(itemRepo))
    
    When("ItemReaderActor receives FetchItem")
    itemReaderActor ! FetchItem("PeanutButter")
    
    Then("it must return item fetched")
    expectMsg(500 millis, ItemFound(peanutbutter))
  }
  
  it must "return ItemNotFound when item is not existing in database" in new Ctx {
    Given("itemRepo that returns None by key")
    (itemRepo.getByKey _).expects(peanutbutter.name).returning(None).once
    
    And("inject itemRepo into a ItemReaderActor")
    val itemReaderActor =  TestActorRef(new ItemReaderActor(itemRepo))
    
    When("ItemReaderActor receives FetchItem")
    itemReaderActor ! FetchItem("PeanutButter")
    
    Then("it must return ItemNotFound")
    expectMsg(500 millis, ItemNotFound("PeanutButter"))
  }
  
  trait Ctx {
    import com.bundlepricing.repos.Implicits.Salat._
    
    val itemRepo = mock[ItemMongoRepo]
  }
  
}
