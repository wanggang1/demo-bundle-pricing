package com.bundlepricing.actors

import akka.actor._
import akka.event.LoggingAdapter

import com.bundlepricing.Settings
import com.bundlepricing.domains._
import com.bundlepricing.repos._

import scala.util.{Failure, Success, Try}

object BundleActor {
  
  def props(implicit bundleRepo: BundleMongoRepo) = Props(new BundleActor(bundleRepo))
  
  val name = "bundle-actor"
  
  case object GetAllBundles
  case class AddBundle(bundle: Bundle)
  case object AddBundleSuccess
  case object AddBundleFailed
  case class AllBundles(all: Map[String, Bundle])
  
  /**
   * For demo purpose only
   */
  def showBundles(bundles: Map[String, Bundle])(implicit log: LoggingAdapter): Unit = {
    import Bundle._
    log.info("-----------------Bundled Price Catalog------------------")
    bundles.values foreach { bundle => log.info(s"${bundleKey(bundle.items)} -> $$${bundle.price}") }
    log.info("")
  }
  
}

class BundleActor(bundleRepo: BundleMongoRepo) extends Actor with ActorLogging  {

  import BundleActor._
  
  implicit val logging = log
  
  var bundles: Map[String, Bundle] = _
  
  override def preStart() = {
    bundles = bundleRepo.getAll
    showBundles(bundles)
  }
  
  def receive = {
    case GetAllBundles =>
      val pairs = for {
          bundle <- bundles.values
          keyPermutation <- Bundle.keyPermutations(bundle.items)
        } yield (keyPermutation -> bundle)
      sender ! AllBundles(pairs.toMap)
    case AddBundle(bundle) =>
      Try( bundleRepo.insert(bundle) ) match {
        case Success(_) => 
          sender ! AddBundleSuccess
          bundles += (bundle.key -> bundle)
        case Failure(_) =>
          sender ! AddBundleFailed
      }
  }
  
}