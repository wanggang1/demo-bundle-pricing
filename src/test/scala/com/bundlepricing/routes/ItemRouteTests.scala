package com.bundlepricing.routes

import com.bundlepricing.{ RouteSpec, TestData }
import com.bundlepricing.domains.{ Item, PartialItem }
import com.bundlepricing.repos._

import akka.util.Timeout
import com.typesafe.scalalogging.StrictLogging
import org.bson.types.ObjectId
import org.scalamock.scalatest.MockFactory
import org.scalatest.GivenWhenThen
import play.api.libs.json.{ Json, JsValue }
import scala.concurrent.duration._
import spray.http.StatusCodes
import squants.market.USD

object ItemRouteTests extends TestData {
  
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

class ItemRouteTests extends RouteSpec
    with MockFactory
    with GivenWhenThen
    with StrictLogging
    with ItemRoute 
    with TestData {

  implicit val timeout: Timeout = 1.second

  private val rootPath = "/bundlepricing/items"

  "ItemRoute" must ("get all items") in new RouteCtx {
    import ItemRouteTests._
    
    Given("All items")
    (itemRepo.getAll _).expects().returning(allItems)

    When("request is made for all items")
    Get(s"$rootPath") ~>
      addHeader("Accept", "application/json") ~>
      itemRoute(itemRepo) ~>
      check {
        Then("status code 200 is returned")
        status mustBe StatusCodes.OK
        And("all item are returned")
        val response = responseAs[List[Item]]
        response.size mustBe 6
        response.map(_.name).sorted mustBe allItemList.map(_.name)
      }
  }
  
  it must ("create new Item") in new RouteCtx {
    Given("an Item object")
    val orangeJuice = Item(new ObjectId, "OrangeJuice", USD(2.49))
    val objectId = Json.obj("id" -> orangeJuice.id.toString)

    And("mock the repo insertion")
    (itemRepo.insert _).expects(orangeJuice)

    When("request is made to create Item")
    Post(s"$rootPath", orangeJuice) ~>
      addHeader("Accept", "application/json") ~>
      itemRoute(itemRepo) ~>
      check {
        Then("status code 201 is returned")
        status mustBe StatusCodes.Created
        And("the created Item id is returned")
        val response = responseAs[JsValue]
        response mustBe objectId
      }
  }
  
  it must ("update an existing Item") in new RouteCtx {
    Given("an existing Item milk in repo and a partially changed object")
    val partial = PartialItem(name = Some("Milk"), price = Some(USD(0.99)))
    val modified = milk.copyPartial(partial)

    (itemRepo.get _).expects(milk.id).returning(Some(milk))
    (itemRepo.upsert _).expects(modified).returning(EntityUpdated)

    When("request is made to modify Item milk")
    Post(s"$rootPath/${milk.id}", partial) ~>
      addHeader("Accept", "application/json") ~>
      itemRoute(itemRepo) ~>
      check {
        Then("status code 200 is returned")
        status mustBe StatusCodes.OK
      }
  }
  
  it must ("delete an existing Item") in new RouteCtx {
    Given("an existing Item apple in Repo")
    (itemRepo.get _).expects(apple.id).returning(Some(apple))
    (itemRepo.delete _).expects(apple.id)

    When("request is made to delete this Item")
    Delete(s"$rootPath/${apple.id}") ~>
      addHeader("Accept", "application/json") ~>
      itemRoute(itemRepo) ~>
      check {
        Then("status code 200 is returned")
        status mustBe StatusCodes.OK
      }
  }
  
  it must ("get an existing Item by name") in new RouteCtx {
    Given("an existing Item cereal")
    (itemRepo.getByKey _).expects(cereal.name).returning(Some(cereal))

    When("request is made to get Item by name")
    Get(s"$rootPath/${cereal.name}") ~>
      addHeader("Accept", "application/json") ~>
      itemRoute(itemRepo) ~>
      check {
        Then("status code 200 is returned")
        status mustBe StatusCodes.OK
        And("Item cereal is returned")
        val response = responseAs[Item]
        response mustBe cereal
      }
  }

  trait RouteCtx {
    import com.bundlepricing.repos.Implicits.Salat._
    
    val itemRepo = mock[ItemMongoRepo]
  }

}
