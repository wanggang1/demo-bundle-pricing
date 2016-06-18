package com.bundlepricing.actors

import akka.actor._
import akka.event.LoggingAdapter

import com.bundlepricing.domains.{Item, Bundle}
import com.bundlepricing.utils.CombinatorialFunction

object PricingActor {
  
  //////////// Pricing specific functions //////////////////////////////
  import CombinatorialFunction._
  import Bundle._

  def pricing(purchasedItems: List[Item], bundles: Map[String, Bundle])
             (implicit log: LoggingAdapter): Double = {
    val possibleBundles = applicableBundles(purchasedItems, bundles)
    showBundles(possibleBundles)
    val possiblePurchases = convertToBundles(purchasedItems, possibleBundles)
    showPurchases(possiblePurchases)
    
    optimizedPrice(possiblePurchases)
  }

  def applicableBundles(purchasedItems: List[Item], bundles: Map[String, Bundle]): List[Bundle] =
    subsets(purchasedItems).collect {
      case groupedItems if bundleDefined(groupedItems, bundles) => bundles( bundleKey(groupedItems) )
    }

  def bundleDefined(groupedItems: List[Item], bundles: Map[String, Bundle]) = bundles.contains(bundleKey(groupedItems)) 
  
  def convertToBundles(purchasedItems: List[Item], applicableBundles: List[Bundle]): List[List[Bundle]] = {
    val purchasePermutations: List[String] = keyPermutations(purchasedItems.map(_.name))
    
    subsets(applicableBundles).filter {
      bundles => purchasePermutations.contains( bundles.map(_.key).mkString )
    }
  }
  
  def optimizedPrice(purchases: List[List[Bundle]]): Double = {
    val costs = purchases.map { _.foldLeft(0.0)((acc, b) => price(b) + acc) }
    costs.min
  }
  
  /**
   * for demo purpose only
   */
  private def showBundles(bundles: List[Bundle])(implicit log: LoggingAdapter) = {
    log.info("applicable bundles:")
    bundles foreach {bundle => log.info(bundle.key)}
    log.info("")
  }
  
  /**
   * for demo purpose only
   */
  private def showPurchases(bundleCombos: List[List[Bundle]])(implicit log: LoggingAdapter) = {
    log.info(s"possible bundled prices: ${bundleCombos.size}")
    bundleCombos.map { purchase: List[Bundle] =>
      val cost = purchase.foldLeft(0.0)((acc, b) => price(b) + acc)
      log.info(s"$purchase -> $$$cost")
    }
    log.info("")
  }
  
  //////////// Actor specific functions//////////////////////////////
  def props = Props[PricingActor]

  case class Pricing(items: List[Item], bundles: Map[String, Bundle], requester: ActorRef)
  case class OptimizedPrice(price: Double, requester: ActorRef)
}

class PricingActor extends Actor with ActorLogging  {

  import PricingActor._
  
  implicit val logging = log
  
  def receive = {
    case Pricing(items, bundles, requester) =>
      log.debug("")
      val price = pricing(items, bundles)
      sender ! OptimizedPrice(price, requester)
      context stop self
  }

}
