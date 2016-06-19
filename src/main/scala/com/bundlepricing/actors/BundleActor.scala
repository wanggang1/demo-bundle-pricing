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
  
  sealed trait BundleMessage
  case object GetCachedBundles extends BundleMessage
  case object GetAllBundles extends BundleMessage
  case class AddBundle(bundle: Bundle) extends BundleMessage
  case object AddBundleSuccess extends BundleMessage
  case object AddBundleFailed extends BundleMessage
  case class AllBundles(all: Map[String, Bundle]) extends BundleMessage

  def allKeyPermutations(bundles: Map[String, Bundle]): Map[String, Bundle] = {
    val pairs = 
      for {
        bundle <- bundles.values
        keyPermutation <- Bundle.keyPermutations(bundle.pricings.map(_.itemName))
      } yield (keyPermutation -> bundle)
        
    pairs.toMap
  }
 
  /**
   * For demo purpose only
   */
  def showBundles(bundles: Map[String, Bundle])(implicit log: LoggingAdapter): Unit = {
    import Bundle._
    log.info("-----------------Bundled Price Catalog------------------")
    bundles.values foreach { bundle => log.info(s"${uniqueKey(bundle.pricings)} -> $$${price(bundle)}") }
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
    case GetCachedBundles =>
      sender ! AllBundles(bundles) 
    case GetAllBundles =>
      sender ! AllBundles( allKeyPermutations(bundles) )
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
