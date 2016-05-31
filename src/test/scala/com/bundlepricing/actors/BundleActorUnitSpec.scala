package com.bundlepricing.actors

import com.bundlepricing.{ActorSpec, TestData}
import com.bundlepricing.domains._
import com.bundlepricing.repos.BundleMongoRepo

import akka.actor.{ActorRef, Props}
import akka.testkit.{ TestActorRef, ImplicitSender, TestProbe }
import org.scalamock.scalatest.MockFactory
import org.scalatest.GivenWhenThen
import scala.concurrent.duration._


class BundleActorUnitSpec extends ActorSpec with ImplicitSender with MockFactory with GivenWhenThen with TestData {

  import scala.concurrent.ExecutionContext.Implicits.global
  import BundleActor._

  val bundleMilk = Bundle(List(milk), unitPrice)
  val bundleBread = Bundle(List(bread), unitPrice)
  val bundleBreadBread = Bundle(List(bread, bread), buy1Get1Free)
  val bundleMilkBread = Bundle(List(milk, bread), buy1Get1Free)
  val baseBundles = Map(
    Bundle.bundleKey(List(milk)) -> bundleMilk,
    Bundle.bundleKey(List(bread)) -> bundleBread,
    Bundle.bundleKey(List(bread, bread)) -> bundleBreadBread,
    Bundle.bundleKey(List(milk, bread)) -> bundleMilkBread
  )
    
  "A BundleActor" must "generate bundles by their key permutations" in {
    Given("baseBundles")

    When("call allKeyPermutations with base bundles")
    val allBundles = allKeyPermutations(baseBundles)
    
    Then("all permutations of bundles must be returned")
    allBundles.keys.size mustBe 5
    val allKeys = allBundles.keys.toList
    allKeys.contains("Milk") mustBe true
    allKeys.contains("Bread") mustBe true
    allKeys.contains("BreadBread") mustBe true
    allKeys.contains("MilkBread") mustBe true
    allKeys.contains("BreadMilk") mustBe true
    
    And("correct bundles associated with each key permutation")
    allBundles("Milk") mustBe bundleMilk
    allBundles("Bread") mustBe bundleBread
    allBundles("BreadBread") mustBe bundleBreadBread
    allBundles("MilkBread") mustBe bundleMilkBread
    allBundles("BreadMilk") mustBe bundleMilkBread
  }

  it must "return all bundle permutations" in new Ctx {
    Given("bundleRepo that returns baseBundles")
    (bundleRepo.getAll _).expects().returning(baseBundles).once
    
    And("inject bundleRepo into a BundleActor")
    val bundleActor =  system.actorOf(Props(new BundleActor(bundleRepo)))
    
    When("BundleActor receives GetAllBundles")
    bundleActor ! GetAllBundles
    
    Then("it must return all permutatin of bundles")
    val result = expectMsgType[AllBundles](500 millis)
    
    val allKeys = result.all.keys.toList
    allKeys.size mustBe 5
    allKeys.contains("Milk") mustBe true
    allKeys.contains("Bread") mustBe true
    allKeys.contains("BreadBread") mustBe true
    allKeys.contains("MilkBread") mustBe true
    allKeys.contains("BreadMilk") mustBe true
    result.all("BreadMilk") mustBe result.all("MilkBread")
  }
  
  it must "add a new bundle" in new Ctx {
    val newBundle = Bundle(List(cereal), unitPrice)
    Given("bundleRepo that returns baseBundles and insert new bundles")
    inSequence {
      //NOTE: getAll only called once at actor initiation time
      (bundleRepo.getAll _).expects().returning(baseBundles).once
      (bundleRepo.insert _).expects(newBundle).returning(()).once
    }
    
    And("inject bundleRepo into a BundleActor")
    val bundleActor =  system.actorOf(Props(new BundleActor(bundleRepo)))
    
    When("BundleActor receives AddBundle")
    bundleActor ! AddBundle(newBundle)
    
    Then("it must return AddBundleSuccess")
    expectMsg(500 millis, AddBundleSuccess)
    
    When("request GetAllBundles again")
    bundleActor ! GetAllBundles
    
    And("it must return all bundles including the new bundle")
    val result = expectMsgType[AllBundles](500 millis)
    
    val allKeys = result.all.keys.toList
    allKeys.size mustBe 6
    allKeys.contains("Cereal") mustBe true
  }
  
  trait Ctx {
    import com.bundlepricing.repos.Implicits.Salat._
    
    val bundleRepo = mock[BundleMongoRepo]
  }
  
}
