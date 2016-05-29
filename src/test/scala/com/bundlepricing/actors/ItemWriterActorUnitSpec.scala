package com.bundlepricing.actors

import com.bundlepricing.{ActorSpec, TestData}
import com.bundlepricing.domains._
import com.bundlepricing.repos.ItemMongoRepo

import akka.actor.{ActorRef, Props}
import akka.testkit.{ TestActorRef, ImplicitSender, TestProbe }
import org.scalamock.scalatest.MockFactory
import org.scalatest.GivenWhenThen
import scala.concurrent.duration._


class ItemWriterActorUnitSpec extends ActorSpec with ImplicitSender with MockFactory with GivenWhenThen with TestData {

  import scala.concurrent.ExecutionContext.Implicits.global
  import ItemWriterActor._
  import BundleActor._
  
  "A ItemWriterActor" must "add a new item" in new Ctx {
    val newItem = peanutbutter
    Given("itemRepo that inserts new item")
    inSequence {
      //NOTE: getAll called at actor initiation time for display purpose
      (itemRepo.getAll _).expects().returning(Map.empty[String, Item]).once
      (itemRepo.insert _).expects(newItem).returning(()).once
    }
    
    And("inject itemRepo into a ItemWriterActor")
    val bundleKeeper = TestProbe("bunderActor")(system)
    val itemWriterActor =  system.actorOf(Props(new ItemWriterActor(itemRepo, bundleKeeper.ref)))
    
    When("ItemWriterActor receives AddItem")
    itemWriterActor ! AddItem(newItem)
    
    Then("it must return AddItemSuccess")
    expectMsg(500 millis, AddItemSuccess)
    
    And("bundleKeeper must receive an AddBundle message with unitPrice")
    bundleKeeper.expectMsgPF(500 millis, "new bundle with unitPrice"){
      case AddBundle(Bundle(_, List(peanutbutter), "PeanutButter", unitPrice)) => ()
    }

  }
  
  trait Ctx {
    import com.bundlepricing.repos.Implicits.Salat._
    
    val itemRepo = mock[ItemMongoRepo]
  }
  
}
