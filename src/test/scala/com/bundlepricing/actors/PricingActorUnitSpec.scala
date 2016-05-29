package com.bundlepricing.actors

import com.bundlepricing.{ActorSpec, TestData}
import com.bundlepricing.domains._

import akka.testkit.{ TestActorRef, ImplicitSender, TestProbe }
import scala.concurrent.duration._

class PricingActorUnitSpec extends ActorSpec with ImplicitSender with TestData {

  import scala.concurrent.ExecutionContext.Implicits.global
  import PricingActor._

  "A PricingActor" must "calculate price" in new Ctx {
    val pricingActor = TestActorRef(new PricingActor)
    val purchases = List(bread, bread, peanutbutter, milk, cereal, cereal, cereal, milk)

    pricingActor ! Pricing(purchases, BundleActor.allKeyPermutations(bundles), requester.ref)
    val result = expectMsgType[OptimizedPrice](500 millis)

    result.price mustBe 13.73
    result.requester mustBe requester.ref
  }

  trait Ctx {
    val requester = TestProbe()
  }
  
}
