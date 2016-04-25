package com.bundlepricing.core

import scala.concurrent.{ExecutionContext, Future, Promise}
import com.bundlepricing.repos._
import com.bundlepricing.domains._

class Inventory(implicit ec: ExecutionContext) {
  self: ItemRepoComponent with BundleRepoComponent =>
    
  def addItem(name: String, price: Double): Future[Unit] = Future {
    val item = Item(name, price)
    itemRepo.insert(item)
    
    val bundle = Bundle(List(item), unitPrice)
    bundleRepo.insert(bundle)
  }
  
  def getItem(name: String): Future[Item] = {
    val p = Promise[Item]()
    Future {
      itemRepo.get(name).fold(p.failure(new Exception("Item Not Found"))){
        item => p.success(item)
      }
    }
    p.future
  }
  
  def addBundledPrice(items: List[Item], bundledPrice: Pricing): Future[Unit] = Future {
    bundleRepo.insert( Bundle(items, bundledPrice) )
  }
  
  def getBundles: Future[Map[String, Bundle]] = Future {
    val baseBundles: Map[String, Bundle] = bundleRepo.getAll
    
    val pairs = for {
      bundle <- baseBundles.values
      keyPermutation <- Bundle.keyPermutations(bundle.items)
    } yield (keyPermutation -> bundle)
    
    pairs.toMap
  }
  
  /**
   * For demo purpose only
   */
  def showBundles(): Unit = {
    import Bundle._
    println("-----------------Bundled Price Catalog------------------")
    bundleRepo.getAll.values foreach { bundle => println(s"${bundleKey(bundle.items)} -> $$${bundle.price}") }
    println("")
  }
  
  /**
   * For demo purpose only
   */
  def showItems(): Unit = {
    import Bundle._
    println("-----------------Item Catalog------------------")
    itemRepo.getAll.values foreach { println(_) }
    println("")
  }
  
}