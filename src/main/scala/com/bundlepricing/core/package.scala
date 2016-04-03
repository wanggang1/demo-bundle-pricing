package com.bundlepricing

import com.bundlepricing.domains.Item

package object core {
  type Pricing = List[Item] => Double
  
  val unitPrice: Pricing = (items: List[Item]) => {
    require(items.size == 1)
    items.head.price
  }
}
