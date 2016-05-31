package com.bundlepricing.actors.collectors

import com.bundlepricing.{ActorSpec, Settings}

import akka.actor.{ActorRef, Props}
import akka.testkit.ImplicitSender
import akka.util.Timeout
import org.scalatest.GivenWhenThen
import scala.concurrent.Await
import scala.concurrent.duration._

class ResponseCollectorUnitSpec extends ActorSpec with ImplicitSender with GivenWhenThen {

  import scala.concurrent.ExecutionContext.Implicits.global

  "A ResponseCollector" must "collect all responses" in {
    Given("CountDown tracker and a response matcher")
    val countTracker = Countdown(3)
    val responseMatcher: PartialFunction[Any, String] = { case s: String => s }
    
    And("create a response collector and its result in Future")
    implicit val timeout: Timeout = Settings.webServiceTimeout
    val (fResult, collector) = ResponseCollector(countTracker, responseMatcher, system)
    
    When("send response collector 3 response")
    collector ! "1"
    collector ! "2"
    collector ! "3"
    
    Then("it must return 3 responses collected")
    val result = Await.result(fResult.mapTo[Result[String]], 500 millis)
    result.values mustBe Vector("1", "2", "3")
    result.state mustBe Full
  }
  
  it must "only collect partial responses when time out" in {
    Given("CountDown tracker for 4 and a response matcher")
    val countTracker = Countdown(4)
    val responseMatcher: PartialFunction[Any, String] = { case s: String => s }
    
    And("create a response collector and result in Future")
    implicit val timeout: Timeout = 100 millis
    val (fResult, collector) = ResponseCollector(countTracker, responseMatcher, system)
    
    When("send response collector only 3 response")
    collector ! "1"
    collector ! "2"
    collector ! "3"
    
    Then("it must return Partial with less than 4 responses collected")
    val result = Await.result(fResult.mapTo[Result[String]], 500 millis)
    result.state mustBe Partial
    lessThan(result.values.size, 4) mustBe true
  }
  
  private def lessThan(size: Int, limit: Int) = size < limit
  
}
