package com.bundlepricing.actors

import akka.actor._
import akka.routing.{ ActorRefRoutee, RoundRobinRoutingLogic, Router }
import akka.util.Timeout
import scala.concurrent.ExecutionContext

import com.bundlepricing.actors.collectors._
import com.bundlepricing.domains.Item
import com.bundlepricing.repos.ItemMongoRepo

object ItemReaderRouter {
  
  def props(implicit itemRepo: ItemMongoRepo, timeout: Timeout, execContext: ExecutionContext) = Props(new ItemReaderRouter)
  
  val name = "item-reader-router"
  
  case class Get(keys: List[String])
  case class ItemResults(items: List[Item])
}

class ItemReaderRouter(implicit itemRepo: ItemMongoRepo, timeout: Timeout, execContext: ExecutionContext)
  extends Actor with Stash with ActorLogging {

  import akka.pattern.pipe
  import ItemReaderRouter._
  import ItemReaderActor._
  
  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(ItemReaderActor.props(itemRepo))
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }
  
  var client: ActorRef = _
  
  //setup matcher for type ItemFound and ItemNotFound
  val responseMatcher: PartialFunction[Any, ItemResult] = { 
    case i: ItemFound => i
    case inf: ItemNotFound => inf
  }
  
  def receive = {
    case Get(keys) =>
      client = sender
      val countTracker = Countdown(keys.size)
      val (fResult, collector) = ResponseCollector(countTracker, responseMatcher, context)
      keys foreach {key => router.route(FetchItem(key), collector)}
      fResult pipeTo self
      context become (handleResponse, discardOld = false)
    case Terminated(a) =>
      updateRoutee(a)
  }

  private def handleResponse: Receive = {
    case Result(reponses, Full) =>
      val (itemsFound, itemsNotFound) = reponses partition { hasItem(_) }
      if (itemsNotFound.isEmpty) {
        val items = itemsFound collect {case ItemFound(item) => item}
        client ! ItemResults(items.toList)
      } else {
        val keys = itemsNotFound collect {case ItemNotFound(key) => key}
        log.error(s"No Item(s) found for: ${keys.toList}.")
        client ! ItemResults(List.empty[Item])
      }
      switchToReceive()
    case Result(_, Partial) =>
      log.error(s"Time out while waiting for Items.")
      client ! ItemResults(List.empty[Item])
      switchToReceive()
    case Terminated(a) =>
      updateRoutee(a)
    case _ =>
      stash()
  }

  private def switchToReceive() = {
    context unbecome()
    unstashAll()
  }
  
  private def updateRoutee(a: ActorRef) = {
    router = router.removeRoutee(a)
    val r = context.actorOf(ItemReaderActor.props(itemRepo))
    context watch r
    router = router.addRoutee(r)
  }

  private def hasItem(result: Any) = result match {
    case ItemFound(_) => true
    case ItemNotFound(_) => false
  }

}
