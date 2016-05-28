package com.bundlepricing

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

import com.bundlepricing.actors._
import com.bundlepricing.domains._
import com.bundlepricing.repos._

/**
 * Demo Bundled Price ActorSystem
 */
object DemoActorSystem extends App with StrictLogging {

  import com.bundlepricing.repos.Implicits.Salat._
  import com.mongodb.casbah.Imports._
  
  implicit val timeout: Timeout = Settings.webServiceTimeout
  implicit val system = ActorSystem("store-akka-stream", ConfigFactory.load().getConfig("grocery-store"))
  implicit val ec = system.dispatcher

  implicit val itemRepo = new ItemMongoRepo(Settings.dbName, Item.collectionName) with SalatRepository {
    //Ensure item name are unique
    salatDao.collection.ensureIndex(keys = Map("name" -> 1), name = "item_name_index", unique = true)
  }
  implicit val bundleRepo = new BundleMongoRepo(Settings.dbName, Bundle.collectionName) with SalatRepository {
    //Ensure bundle key are unique
    salatDao.collection.ensureIndex(keys = Map("key" -> 1), name = "bundle_key_index", unique = true)
  }
  
  val bundleKeeper = system.actorOf(BundleActor.props, BundleActor.name)
  
  val itemWriter = system.actorOf(ItemWriterActor.props(bundleKeeper), ItemWriterActor.name)
  
  val itemReaderRouter = system.actorOf(ItemReaderRouter.props, ItemReaderRouter.name)
  
  val shoppingcartActor = system.actorOf(ShoppingcartActor.props(bundleKeeper, itemReaderRouter), ShoppingcartActor.name)
  
  //give 0.5 second to initialize all actors
  Thread.sleep(500)
  
  import ShoppingcartActor._
  println("--Purchase: Bread, Bread, PeanutButter, Milk, Cereal, Cereal, Cereal, Milk---")
  val purchasees = List("Bread", "Bread", "PeanutButter", "Milk", "Cereal", "Cereal", "Cereal", "Milk")
  val fPrice = (shoppingcartActor ? Checkout(purchasees)).mapTo[Double]
  val price = Await.result(fPrice, 1000 milliseconds) 
  println(s"optimized price: $price")

  /**
   * Add hook for graceful shutdown
   * 
   * NOTE: application will not terminate unless Ctrl-c is entered
   */
  sys.addShutdownHook {
    system.log.info("Shutting down....")
    system.shutdown()
    system.awaitTermination()
    logger.info(s"Actor system '${system.name}' successfully shut down")

    logger.info("Shutting down mongo client")
    mongoClient.close()
    logger.info("Mongo client successfully shut down")
  }

}

