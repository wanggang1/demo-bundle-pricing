package com.bundlepricing.actors

import com.bundlepricing.{ActorSpec, TestData, Settings}
import com.bundlepricing.domains._

import akka.actor.{ActorRef, Props}
import akka.testkit.{ TestActorRef, ImplicitSender, TestProbe, TestActor }
import akka.util.Timeout
import scala.concurrent.duration._

class ShoppingcartActorIntegrationSpec extends ActorSpec with ImplicitSender with TestData {
  import ShoppingcartActor._
  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val timeout: Timeout = Settings.webServiceTimeout
  
  "A ShoppingcartActor" must "return a calculated price" in new Ctx {
    val shoppingcart =  system.actorOf(Props(new ShoppingcartActor(bundleActor.ref, itemRouter.ref)))
    
    val items = List(bread, bread, peanutbutter, milk, cereal, cereal, cereal, milk)
    val purchasees = List("Bread", "Bread", "PeanutButter", "Milk", "Cereal", "Cereal", "Cereal", "Milk")

    shoppingcart ! Checkout(purchasees)
    val result = expectMsgType[Double](500 millis)

    result mustBe 13.73
  }

  trait Ctx {
    import BundleActor._
    import ItemReaderRouter._

    def items: List[Item]
    
    val bundleActor = TestProbe()(system)
    bundleActor.setAutoPilot {
      new TestActor.AutoPilot {
        override def run(sender: ActorRef, msg: Any): TestActor.AutoPilot =
          msg match {
            case GetAllBundles ⇒
              sender ! AllBundles( allKeyPermutations(bundles) )
              keepRunning
            case _ ⇒
              noAutoPilot
          }
      }
    }
    
    val itemRouter = TestProbe()(system)
    itemRouter.setAutoPilot {
      new TestActor.AutoPilot {
        override def run(sender: ActorRef, msg: Any): TestActor.AutoPilot =
          msg match {
            case Get(keys) ⇒
              sender ! ItemResults(items)
              keepRunning
            case _ ⇒
              noAutoPilot
          }
      }
    }
  }
  
}
