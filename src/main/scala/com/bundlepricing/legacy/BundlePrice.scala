package com.bundlepricing.legacy

import com.bundlepricing.domains._
import com.bundlepricing.utils.CombinatorialFunction

import scala.BigDecimal
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Failure}

object BundlePrice {
  /**
   * for demo purpose only
   */
  def showBundles(bundles: List[Bundle]) = {
    println("applicable bundles:")
    bundles foreach {println(_)}
    println("")
  }
  
  /**
   * for demo purpose only
   */
  def showPurchases(bundleCombos: List[List[Bundle]]) = {
    println(s"possible bundled prices: ${bundleCombos.size}")
    bundleCombos.map { purchase: List[Bundle] =>
      val cost = purchase.foldLeft(0.0){(acc, b) => Bundle.price(b) + acc}
      println(s"$purchase -> $$$cost")
    }
    println("")
  }
}

class BundlePrice(implicit inventory: Inventory, ec: ExecutionContext)  {
  import BundlePrice._
  import CombinatorialFunction._
  import Bundle._

  def pricing(purchasedItems: List[Item]): Future[Double] = {
    val bundlesFuture = inventory.getBundles
    for {
      possibleBundles <- applicableBundles(purchasedItems, bundlesFuture)
      _ = showBundles(possibleBundles) //for demo purpose only
      purchases = convertToBundles(purchasedItems, possibleBundles)
      _ = showPurchases(purchases) //for demo purpose only
    } yield optimizedPrice(purchases)
  }

  def applicableBundles(purchasedItems: List[Item], bundlesFuture: Future[Map[String, Bundle]]): Future[List[Bundle]] =
    bundlesFuture.map {bundles =>
      subsets(purchasedItems).collect {
        case groupedItems if bundleDefined(groupedItems, bundles) => bundles( bundleKey(groupedItems) )
      }
    }

  def bundleDefined(groupedItems: List[Item], bundles: Map[String, Bundle]) = bundles.contains(bundleKey(groupedItems)) 
  
  def convertToBundles(purchasedItems: List[Item], applicableBundles: List[Bundle]): List[List[Bundle]] = {
    val purchasePermutations: List[String] = keyPermutations(purchasedItems.map(_.name))
    
    subsets(applicableBundles).filter {
      bundles: List[Bundle] => purchasePermutations.contains( bundles.map(_.key).mkString )
    }
  }
  
  def optimizedPrice(purchases: List[List[Bundle]]): Double = {
    val costs = purchases.map { _.foldLeft(0.0)((acc, b) => price(b) + acc) }
    costs.min
  }
  
}