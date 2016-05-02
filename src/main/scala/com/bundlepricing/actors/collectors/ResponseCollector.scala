package org.bundlepricing.actors.collectors

import akka.actor._

import scala.concurrent.duration._
import scala.concurrent.{ Future, Promise }


object ResponseCollector {
  import akka.util.Timeout
  
  def props[T](tracker: ResponseTracker[T], result: Promise[Result[T]], matcher: PartialFunction[Any, T])
              (implicit timeout: Timeout): Props =
      Props(new ResponseCollector(timeout.duration, tracker, result, matcher))

  def apply[T](tracker: ResponseTracker[T], matcher: PartialFunction[Any, T])
              (implicit timeout: Timeout, factory: ActorRefFactory): (Future[Result[T]], ActorRef) = {
    val result = Promise[Result[T]]()
    val ref = factory.actorOf(props(tracker, result, matcher))
    (result.future, ref)
  }
  
  val sampleMatcher: PartialFunction[Any, String] = {
    case s: String => s
    case i: Int => i.toString
  }
  
}

class ResponseCollector[T](
    timeout: FiniteDuration,
    initialTracker: ResponseTracker[T],
    result: Promise[Result[T]],
    matcher: PartialFunction[Any, T])
  extends Actor with ActorLogging {

  import context.dispatcher

  context.system.scheduler.scheduleOnce(timeout, self, ReceiveTimeout)

  override def receive = ready(Vector.empty[T], initialTracker)
  
  private def ready(responses: Vector[T], tracker: ResponseTracker[T]): Receive = {
    case m if matcher.isDefinedAt(m) =>
      val response = matcher(m)
      val nextResponses = responses :+ response
      val nextTracker = tracker.addResponse(response)
  
      if (nextTracker.isDone) {
        log.info("All responses received.")
        result.success(Result(nextResponses, Full))
        context.stop(self)
      } else {
        context.become(ready(nextResponses, nextTracker))
      }
  
    case ReceiveTimeout =>
      log.warning("Response collection timed out")
      result.success(Result(responses, CollectorTimeout))
      context.stop(self)
  
    case m =>
      log.warning("Unknown message: {}", m)
  }
  
}