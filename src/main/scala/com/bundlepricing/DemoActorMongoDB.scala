package com.bundlepricing

import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

import com.bundlepricing.actors._

object DemoActorMongoDB {

  /**
   * Demo Bundled Price ActorSystem
   */
  def main (args: Array[String]): Unit = {
    
    implicit val timeout: Timeout = Settings.webServiceTimeout
    implicit val system = ActorSystem("store-akka-stream", ConfigFactory.load().getConfig("grocery-store"))
    //implicit val ec = system.dispatcher

    system.actorOf(ItemWriterActor.props, ItemWriterActor.name)
    //system.actorOf(ItemReaderActor.props, "item-reader")
  }
  
}

