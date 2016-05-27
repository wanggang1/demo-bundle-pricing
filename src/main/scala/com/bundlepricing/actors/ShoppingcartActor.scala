package com.bundlepricing.actors

import akka.actor._
import akka.util.Timeout
import scala.concurrent.ExecutionContext

object ShoppingcartActor {
  
  def props(bundles: ActorRef, itemRouter: ActorRef)
           (implicit execContext: ExecutionContext, timeout: Timeout) = 
    Props(new ShoppingcartActor(bundles, itemRouter))
  
  val name = "shoppingcart-actor"
  
  case class Checkout(items: List[String])
  
}

class ShoppingcartActor(bundleActor: ActorRef, itemRouter: ActorRef)
                       (implicit execContext: ExecutionContext, timeout: Timeout)
  extends Actor with ActorLogging {

  import akka.pattern.{ask, pipe}
  import ShoppingcartActor._
  import ItemReaderRouter._
  import BundleActor._
  import PricingActor._
  
  def receive = {
    case Checkout(keys) =>
      val fItemResults = (itemRouter ? Get(keys)).mapTo[ItemResults]
      val fAllBundles = (bundleActor ? GetAllBundles).mapTo[AllBundles]
      val pricingActor = context.actorOf(PricingActor.props)
      val requester = sender
      for {
        items <- fItemResults.map(_.items)
        allBundles <- fAllBundles.map(_.all)
      } yield pricingActor ! Pricing(items, allBundles, requester)
    case OptimizedPrice(price, requester) =>
      requester ! price
  }
  
}
