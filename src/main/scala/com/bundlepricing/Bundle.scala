package com.bundlepricing

import BundlePrice.SavingPolicy

object Bundle {
  
  def bundleKey(items: List[Item]): String = items.map(_.name).mkString
  
  /*
   * string representations of permutation of item names
   */
  def keyPermutations(items: List[Item]): List[String] =
    items.map(_.name).permutations.map(_.mkString).toList
  
}

case class Bundle(items: List[Item], applyPolicy: SavingPolicy) {
  import Bundle._

  val key = bundleKey(items)
  val price = applyPolicy(items)
}
