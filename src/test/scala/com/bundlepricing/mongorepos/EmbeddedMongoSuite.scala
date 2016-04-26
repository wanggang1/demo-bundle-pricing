package com.bundlepricing.mongorepos

import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers
import de.flapdoodle.embed.mongo.config.{ MongodConfigBuilder, RuntimeConfigBuilder }
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.{ Command, MongodStarter }
import de.flapdoodle.embed.process.config.io.ProcessOutput
import org.scalatest.{ BeforeAndAfterAll, Suites }
import EmbeddedMongoSuite._

/**
 * Spins up an instance of MongoDB and initializes a connection pool to access it
 * All specs that need this resource must be initialized in the 'Suites' constructor below and passed a client reference by name
 */
class EmbeddedMongoSuite
    extends Suites(new BundleSalatRepoTests(mongoClient), new ItemSalatRepoTests(mongoClient))
    with BeforeAndAfterAll {

  RegisterJodaTimeConversionHelpers()

  lazy val mongodExecutable = {
    val runtimeConfig = new RuntimeConfigBuilder().defaults(Command.MongoD).processOutput(ProcessOutput.getDefaultInstanceSilent).build
    val mongodConfig = new MongodConfigBuilder().version(Version.Main.V3_0).build

    MongodStarter.getInstance(runtimeConfig).prepare(mongodConfig)
  }

  override def beforeAll(): Unit = {
    val mongodProcess = mongodExecutable.start
    mongoClient = MongoClient(mongodProcess.getConfig.net.getBindIp, mongodProcess.getConfig.net.getPort)
  }

  override def afterAll(): Unit = {
    mongoClient.close()
    mongodExecutable.stop() //This stops mongodProcess as well
  }

}

object EmbeddedMongoSuite {

  //Updated during the lifecycle of the suite and passed, by name, to all the suite's specs
  private var mongoClient: MongoClient = _

}
