package com.bundlepricing.actors

import akka.actor._

import com.bundlepricing.domains.Item
import com.bundlepricing.repos.ItemMongoRepo

object ItemReaderActor {
  def props(implicit itemRepo: ItemMongoRepo) = Props(new ItemReaderActor(itemRepo))

  case class FetchItem(key: String)
  sealed trait ItemResult
  case class ItemFound(item: Item) extends ItemResult
  case class ItemNotFound(key: String) extends ItemResult
}

class ItemReaderActor(itemRepo: ItemMongoRepo) extends Actor with ActorLogging {
  import ItemReaderActor._
  
  def receive = {
    case FetchItem(key: String) => 
      itemRepo.getByKey(key) match {
        case Some(item) => sender ! ItemFound(item)
        case None => sender ! ItemNotFound(key)
      }
  }
}