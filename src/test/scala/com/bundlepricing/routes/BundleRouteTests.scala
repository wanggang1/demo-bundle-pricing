package com.bundlepricing.routes

import com.bundlepricing.{ RouteSpec, TestData }
import com.bundlepricing.actors.BundleActor
import com.bundlepricing.domains.Bundle

import akka.util.Timeout
import akka.testkit.TestProbe
import com.typesafe.scalalogging.StrictLogging
import org.bson.types.ObjectId
import org.scalamock.scalatest.MockFactory
import org.scalatest.GivenWhenThen
import play.api.libs.json.{ Json, JsValue }
import scala.concurrent.duration._
import spray.http.StatusCodes
import squants.market.USD

object BundleRouteTests extends TestData {
  
  val allItems = Map(
    milk.name -> milk,
    bread.name -> bread,
    cereal.name -> cereal,
    cheese.name -> cheese,
    peanutbutter.name -> peanutbutter,
    apple.name -> apple
  )
  
  val allItemList = List(milk, bread, cereal, cheese, peanutbutter, apple).sortBy {_.name}

}

class BundleRouteTests extends RouteSpec
    with MockFactory
    with GivenWhenThen
    with StrictLogging
    with BundleRoute 
    with TestData {

  implicit val timeout: Timeout = 1.second

  private val rootPath = "/bundlepricing/bundles"

  "BundleRoute" must ("get all bundles") in new RouteCtx {
    import BundleRouteTests._
    import BundleActor._
    
    Given("All bundles")
    bundleActor.expectMsg(GetCachedBundles)
    bundleActor.reply(AllBundles(Map.empty[String, Bundle]))

    When("request is made for all bundles")
    Get(s"$rootPath") ~>
      addHeader("Accept", "application/json") ~>
      bundleRoute(bundleActor.ref) ~>
      check {
        Then("status code 200 is returned")
        status mustBe StatusCodes.OK
        And("all bundles are returned")
        val response = responseAs[List[Bundle]]
        response.size mustBe 0
      }
  }
  
  trait RouteCtx {
      
    val bundleActor = TestProbe()

  }

}
