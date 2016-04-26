package com.bundlepricing.domains

import com.bundlepricing.core.Pricing

object Bundle {
  
  def apply(items: List[Item], applyPolicy: Pricing) = new Bundle(items, bundleKey(items), applyPolicy(items))
  
  def bundleKey(items: List[Item]): String = items.map(_.name).mkString
  
  /*
   * string representations of permutation of item names
   */
  def keyPermutations(items: List[Item]): List[String] =
    items.map(_.name).permutations.map(_.mkString).toList
  
}

/**
 * make this a simple case class, so it's easy to use Salat to serialize
 */
case class Bundle(items: List[Item], key: String, price: Double)
