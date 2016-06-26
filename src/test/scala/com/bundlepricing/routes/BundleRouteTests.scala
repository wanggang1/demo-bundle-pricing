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

}

class BundleRouteTests extends RouteSpec
    with MockFactory
    with GivenWhenThen
    with StrictLogging
    with BundleRoute {

  import BundleRouteTests._
  implicit val timeout: Timeout = 1.second

  private val rootPath = "/bundlepricing/bundles"

  "BundleRoute" must ("get all bundles") in new RouteCtx {
    import BundleActor._

    When("request is made for all bundles")
    val getAllBundle = Get(s"$rootPath") ~>
        addHeader("Accept", "application/json") ~>
        bundleRoute(bundleActor.ref)

    //TestProbe is not mock, the expectMsg needs to be called at the right timing
    bundleActor.expectMsg(GetCachedBundles)
    bundleActor.reply(AllBundles(bundles))

    getAllBundle ~> check {
      Then("status code 200 is returned")
      status mustBe StatusCodes.OK
      And("all bundles are returned")
      val response = responseAs[List[Bundle]]
      response.size mustBe 16
      response mustBe bundles.values.toList
    }
  }
  
  trait RouteCtx {
    val bundleActor = TestProbe()
  }

}
