package com.bundlepricing

import akka.util.Timeout
import com.typesafe.config.{ ConfigFactory, Config }

class Settings(val config: Config) {

  val storeConfig = config.getConfig("grocery-store")
  import storeConfig._

  val host = if (hasPath("host")) getString("host") else "127.0.0.1"
  val port = getInt("port")
  val webServiceTimeout = Timeout(getFiniteDuration("web-service-timeout"))

  val dbName = getString("db.name")
  val dbUri = getString("db.uri")

  def getFiniteDuration(path: String) = {
    import scala.concurrent.duration._
    getDuration(path, java.util.concurrent.TimeUnit.MILLISECONDS).millis
  }

}

object Settings extends Settings(ConfigFactory.load)
