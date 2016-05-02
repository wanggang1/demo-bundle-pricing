package com.bundlepricing.actors

import akka.actor._

import com.bundlepricing.Settings
import com.bundlepricing.domains._
import com.bundlepricing.repos._

object ItemWriterActor {
  
  def props = Props(new ItemReaderActor with ItemRepoComponent with BundleRepoComponent {
    import com.bundlepricing.repos.Implicits.Salat._
    
    val itemRepo = new ItemMongoRepo(Settings.dbName, Item.collectionName) with SalatRepository
    val bundleRepo = new BundleMongoRepo(Settings.dbName, Bundle.collectionName) with SalatRepository
  })
  
  val name = "item-writer"
  
  case class AddItem(item: Item)
}

class ItemWriterActor extends Actor with ActorLogging {
  self: ItemRepoComponent with BundleRepoComponent =>
    
  import ItemWriterActor._
  
  def receive = {
    case AddItem(item) =>
      itemRepo.insert(item)
      val bundle = Bundle(List(item), unitPrice)
      bundleRepo.insert(bundle)
  }
  
}