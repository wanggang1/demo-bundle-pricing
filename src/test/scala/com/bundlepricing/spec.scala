package com.bundlepricing

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.typesafe.config.Config
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import spray.testkit.ScalatestRouteTest

trait UnitSpec extends FlatSpecLike with MustMatchers with GivenWhenThen with ScalaFutures with OptionValues with EitherValues {

  /*
   * Disable "should" and "can" verbs to force consistent use of "must".
   * Disabled through overriding the implicit conversions
   */
  override def convertToStringShouldWrapper(o: String) = super.convertToStringShouldWrapper(o)
  override def convertToStringCanWrapper(o: String) = super.convertToStringCanWrapper(o)

}

abstract class ActorSpec private (_system: ActorSystem) extends TestKit(_system) with UnitSpec with BeforeAndAfterAll {

  def this() = this(ActorSystem(ActorSpec.actorSystemName))
  def this(config: Config) = this(ActorSystem(ActorSpec.actorSystemName, config))

  def shutdown(): Unit = ()

  final override def afterAll(): Unit = {
    shutdown()
    TestKit.shutdownActorSystem(system)
    super.afterAll()
  }

}

object ActorSpec {

  /**
   * Used for naming actor systems after the spec that creates them
   * Similar to [[spray.util.Utils.actorSystemNameFrom]] except it works even when called from a constructor
   */
  private def actorSystemName =
    Thread.currentThread.getStackTrace.map(_.getClassName)
      .drop(1).dropWhile(_ matches ".*ActorSpec.?$")
      .head.replaceFirst(""".*\.""", "").replaceAll("[^a-zA-Z_0-9]", "-")

}

abstract class RouteSpec extends UnitSpec with ScalatestRouteTest {
  import scala.concurrent.duration._

  //implicit val routeTestTimeout = RouteTestTimeout(2 seconds)

  def shutdown(): Unit = ()

  final override def afterAll(): Unit = {
    shutdown()
    TestKit.shutdownActorSystem(system)
    super.afterAll()
  }

}
