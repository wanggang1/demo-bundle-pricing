package com.bundlepricing.actors

import akka.actor._

import com.bundlepricing.Settings
import com.bundlepricing.domains.Item
import com.bundlepricing.repos.{ItemRepoComponent, SalatRepository}

object ItemReaderActor {
  def props = Props(new ItemReaderActor with ItemRepoComponent {
    import com.bundlepricing.repos.Implicits.Salat._
    
    val itemRepo = new ItemMongoRepo(Settings.dbName, Item.collectionName) with SalatRepository
  })

  case class FetchItem(key: String)
}

class ItemReaderActor extends Actor with ActorLogging {
  self: ItemRepoComponent =>
    
  import ItemReaderActor._
  
  def receive = {
    case FetchItem(key: String) => itemRepo.getByKey(key).foreach(item => sender ! item)
  }
  
}