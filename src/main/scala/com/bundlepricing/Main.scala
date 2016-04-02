package com.bundlepricing

object Main {

  import Bundle._
  import CombinatorialFunction._
  import Item._
   
  /**
   * Build library
   * CRUD Item??
   * CRUD PrivePolicy??
   * CRUD Bundle
   * 
   * DAO - DI with Cake
   * BundleCatalog DB API (with Future) - in memory implementation (see banco-blanco)
   * Item DB API ?? - in memory implementation
   * Pricing DB API ?? - in memory implementation
   * 
   */
  def main (args: Array[String]): Unit = {
    BundleCatalog.show

    println("--Purchase: Bread, Bread, PeanutButter, Milk, Cereal, Cereal, Cereal, Milk---")
    val shoppingcart = List(Bread, Bread, PeanutButter, Milk, Cereal, Cereal, Cereal, Milk)
    
    val cost = checkout(shoppingcart)
    println("")
    println(s"Optimized Cost $$$cost")  
  }
  
  private def checkout(purchasedItems: List[Item]): Double = {
    val possibleBundles = applicableBundles(purchasedItems)
    showBundles(possibleBundles)

    val purchases: List[List[Bundle]] = convertToBundles(purchasedItems, possibleBundles)
    showPurchases(purchases)

    optimizedPrice(purchases)
  }
  
  private def applicableBundles(purchasedItems: List[Item]): List[Bundle] =
    subsets(purchasedItems).collect {
      case groupedItems if isSavingBundle(groupedItems) => BundleCatalog.bundles( bundleKey(groupedItems) )
    }
  
  private def isSavingBundle(groupedItems: List[Item]) = BundleCatalog.bundles.contains(bundleKey(groupedItems)) 
  
  private def convertToBundles(purchasedItems: List[Item], applicableBundles: List[Bundle]): List[List[Bundle]] = {
    val purchasePermutations: List[String] = keyPermutations(purchasedItems)
    
    subsets(applicableBundles).filter {
      bundles: List[Bundle] => purchasePermutations.contains( bundles.map(_.key).mkString )
    }
  }
  
  private def optimizedPrice(purchases: List[List[Bundle]]): Double = {
    val costs = purchases.map { _.foldLeft(0.0)(_ + _.price) }
    BigDecimal(costs.min).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }
  
  private def showBundles(possibleBundles: List[Bundle]) = {
    println("possible bundles:")
    possibleBundles foreach {println(_)}
    println("")
  }
  
  private def showPurchases(bundleCombos: List[List[Bundle]]) = {
    println(s"possible bundled prices: ${bundleCombos.size}")
    bundleCombos.map { purchase: List[Bundle] =>
      val cost = purchase.foldLeft(0.0)(_ + _.price)
      println(s"$purchase -> $$$cost")
    }
    println("")
  }

}
