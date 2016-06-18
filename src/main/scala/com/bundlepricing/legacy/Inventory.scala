package com.bundlepricing.legacy

import org.bson.types.ObjectId
import scala.concurrent.{ExecutionContext, Future, Promise}
import squants.market.USD

import com.bundlepricing.repos._
import com.bundlepricing.domains._

class Inventory(implicit ec: ExecutionContext) {
  self: ItemRepoComponent with BundleRepoComponent =>
    
  import Bundle._
  
  def addItem(name: String, price: Double): Future[Unit] = Future {
    val item = Item(new ObjectId, name, USD(price))
    itemRepo.insert(item)
    
    val unitPrice = PricingPolicy(item.name, item.price.amount.toDouble, 1.0)
    bundleRepo.insert( createBundle(List(unitPrice)) )
  }
  
  def getItem(name: String): Future[Item] = {
    val p = Promise[Item]()
    Future {
      itemRepo.getByKey(name).fold(p.failure(new Exception("Item Not Found"))){
        item => p.success(item)
      }
    }
    p.future
  }
  
  def addBundledPrice(pricings: List[PricingPolicy]): Future[Unit] = Future {
    bundleRepo.insert( createBundle(pricings) )
  }
  
  def getBundles: Future[Map[String, Bundle]] = Future {
    val baseBundles: Map[String, Bundle] = bundleRepo.getAll
    
    val pairs = for {
      bundle <- baseBundles.values
      keyPermutation <- keyPermutations(bundle.pricings.map(_.itemName))
    } yield (keyPermutation -> bundle)
    
    pairs.toMap
  }
  
  /**
   * For demo purpose only
   */
  def showBundles(): Unit = {
    import Bundle._
    println("-----------------Bundled Price Catalog------------------")
    bundleRepo.getAll.values foreach { bundle => println(s"${uniqueKey(bundle.pricings)} -> $$${price(bundle)}") }
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