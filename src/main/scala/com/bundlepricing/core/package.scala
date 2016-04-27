package com.bundlepricing

import com.bundlepricing.domains.Item

package object core {
  
  type Pricing = List[Item] => Double
  
  /**
   * define common discount pricing
   */
  val unitPrice: Pricing = (items: List[Item]) => {
    require(items.size == 1)
    items.head.price.value
  }

  val buy1Get1Free: Pricing = (items: List[Item]) => {
      require(items.size == 2)
      items.head.price.value
    }
  
  val buy1Get2ndHalf: Pricing = (items: List[Item]) => {
      require(items.size == 2)
      items.head.price.value + items.last.price.value / 2
    }
  
  val buy2Get3rdHalf: Pricing = (items: List[Item]) => {
      require(items.size == 3)
      items(0).price.value + items(1).price.value + items(2).price.value / 2
    }
  
  val buy3Get4thFree: Pricing = (items: List[Item]) => {
      require(items.size == 4)
      items.dropRight(1).map(_.price.value).foldLeft(0.0)(_ + _)
    }
  
}
