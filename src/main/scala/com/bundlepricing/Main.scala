package com.bundlepricing

import akka.actor.{ ActorSystem, Props }
import com.mongodb.casbah.{ MongoClientURI, MongoClient }
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import com.bundlepricing.actors._
import com.bundlepricing.domains._
import com.bundlepricing.repos._

object Main extends App with StrictLogging {
  import com.bundlepricing.repos.Implicits.Salat._
  import com.mongodb.casbah.Imports._

  RegisterJodaTimeConversionHelpers()

  //Init repo
  implicit val itemRepo = new ItemMongoRepo(Settings.dbName, Item.collectionName) with SalatRepository {
    //Ensure item name are unique
    salatDao.collection.ensureIndex(keys = Map("name" -> 1), name = "item_name_index", unique = true)
  }
  implicit val bundleRepo = new BundleMongoRepo(Settings.dbName, Bundle.collectionName) with SalatRepository {
    //Ensure bundle key are unique
    salatDao.collection.ensureIndex(keys = Map("key" -> 1), name = "bundle_key_index", unique = true)
  }

  //Init actor system
  implicit val actorSystem = ActorSystem("store-akka-stream", ConfigFactory.load().getConfig("grocery-store"))
  import actorSystem.dispatcher

  implicit val webServiceTimeout = Settings.webServiceTimeout

  //Init web service
  actorSystem.actorOf(Props(
    new WebServiceActor(
        Settings.host, 
        Settings.port,
        itemRepo,
        bundleRepo)), name = "web-service")

  //Add hook for graceful shutdown
  sys.addShutdownHook {
    actorSystem.log.info("Shutting down")
    actorSystem.shutdown()
    actorSystem.awaitTermination()
    logger.info(s"Actor system '${actorSystem.name}' successfully shut down")

    logger.info("Shutting down mongo client")
    mongoClient.close()
    logger.info("Mongo client successfully shut down")
  }

}
