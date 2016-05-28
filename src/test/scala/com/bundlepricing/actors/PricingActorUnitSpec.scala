package com.bundlepricing.actors

import com.bundlepricing.ActorSpec
import com.bundlepricing.domains._

import akka.testkit.{ TestActorRef, ImplicitSender }
import org.bson.types.ObjectId
import scala.collection.immutable.IndexedSeq
import scala.concurrent.duration._

object PricingActorUnitSpec {
  val milk = Item("Milk", 2.99)
  val bread = Item("Bread", 1.99)
  val cereal = Item("Cereal", 2.50)
  val cheese = Item("SlicedCheese", 4.50)
  val peanutbutter = Item("PeanutButter", 2.50)
  val apple = Item("Apple", 1.00)

  val bundles = Map(
    Bundle.bundleKey(List(milk)) -> Bundle(List(milk), unitPrice),
    Bundle.bundleKey(List(bread)) -> Bundle(List(bread), unitPrice),
    Bundle.bundleKey(List(cereal)) -> Bundle(List(cereal), unitPrice),
    Bundle.bundleKey(List(cheese)) -> Bundle(List(cheese), unitPrice),
    Bundle.bundleKey(List(peanutbutter)) -> Bundle(List(peanutbutter), unitPrice),
    Bundle.bundleKey(List(apple)) -> Bundle(List(apple), unitPrice),
    Bundle.bundleKey(List(apple, apple, peanutbutter)) -> Bundle(List(apple, apple, peanutbutter), buy2Get3rdHalf),
    Bundle.bundleKey(List(bread, bread, peanutbutter)) -> Bundle(List(bread, bread, peanutbutter), buy2Get3rdHalf),
    Bundle.bundleKey(List(milk, milk, cheese)) -> Bundle(List(milk, milk, cheese), buy2Get3rdHalf),
    Bundle.bundleKey(List(cereal, cereal, cereal)) -> Bundle(List(cereal, cereal, cereal), buy2Get3rdHalf),
    Bundle.bundleKey(List(cereal, cereal, cereal, milk)) -> Bundle(List(cereal, cereal, cereal, milk), buy3Get4thFree),
    Bundle.bundleKey(List(apple, apple, apple, apple)) -> Bundle(List(apple, apple, apple, apple), buy3Get4thFree),
    Bundle.bundleKey(List(bread, bread)) -> Bundle(List(bread, bread), buy1Get1Free),
    Bundle.bundleKey(List(milk, milk)) -> Bundle(List(milk, milk), buy1Get1Free),
    Bundle.bundleKey(List(peanutbutter, peanutbutter)) -> Bundle(List(peanutbutter, peanutbutter), buy1Get2ndHalf),
    Bundle.bundleKey(List(cheese, cheese)) -> Bundle(List(cheese, cheese), buy1Get2ndHalf)
  )
}

class PricingActorUnitSpec extends ActorSpec with ImplicitSender {

  import scala.concurrent.ExecutionContext.Implicits.global
  import PricingActor._
  import PricingActorUnitSpec._

  "A PricingActor" must "calculate price" in new Ctx {
    val pricingActor = TestActorRef(new PricingActor)
    val purchases = List(bread, bread, peanutbutter, milk, cereal, cereal, cereal, milk)

    pricingActor ! Pricing(purchases, BundleActor.allKeyPermutations(bundles), requester)
    val result = expectMsgType[OptimizedPrice](500 millis)

    result.price mustBe 13.73
    result.requester mustBe requester
  }

  trait Ctx {
    import akka.actor.{Actor, Props}
    val requester = system.actorOf(Props(new Actor { def receive = { case _ => } }))
  }
  
}
