package com.bundlepricing.actors

import akka.actor._
import akka.event.LoggingAdapter

import com.bundlepricing.Settings
import com.bundlepricing.domains._
import com.bundlepricing.repos._

import scala.util.{Failure, Success, Try}

object ItemWriterActor {
  
  def props(bundleKeeper: ActorRef)(implicit itemRepo: ItemMongoRepo) = Props(new ItemWriterActor(itemRepo, bundleKeeper))
  
  val name = "item-writer"
  
  case class AddItem(item: Item)
  case object AddItemSuccess
  case object AddItemFailed
  
  /**
   * For demo purpose only
   */
  def showItems(items: Iterable[Item])(implicit log: LoggingAdapter): Unit = {
    log.info("-----------------Item Catalog------------------")
    items foreach { item => log.info(item.toString) }
    log.info("")
  }
}

class ItemWriterActor(itemRepo: ItemMongoRepo, bundleKeeper: ActorRef) extends Actor with ActorLogging {
  import ItemWriterActor._
  import BundleActor._
  
  implicit val logging = log
  
  override def preStart() = {
    showItems(itemRepo.getAll.values)
  }
  
  def receive = {
    case AddItem(item) =>
      Try(itemRepo.insert(item)) match {
        case Success(_) => 
          val bundle = Bundle(List(item), unitPrice)
          bundleKeeper ! AddBundle(bundle)
          sender ! AddItemSuccess
        case Failure(_) =>
          sender ! AddItemFailed
      }
  }
  
}