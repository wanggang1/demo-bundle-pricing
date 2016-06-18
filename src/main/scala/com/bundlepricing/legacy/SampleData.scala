package com.bundlepricing.legacy

import com.bundlepricing.domains._
import scala.concurrent.{ExecutionContext, Future}

object SampleData {
    
  def populateItems(inventory: Inventory): Future[Unit] = {
    inventory.addItem("Milk", 2.99)
    inventory.addItem("Bread", 1.99)
    inventory.addItem("Cereal", 2.50)
    inventory.addItem("SlicedCheese", 4.50)
    inventory.addItem("PeanutButter", 2.50)
    inventory.addItem("Apple", 1.00)
  }

  def populateBundles(inventory: Inventory)(implicit ec: ExecutionContext): Future[Unit] = {
    //execute Futures in parallel
    val apple = inventory.getItem("Apple")
    val bread = inventory.getItem("Bread")
    val cereal = inventory.getItem("Cereal")
    val milk = inventory.getItem("Milk")
    val slicedCheese = inventory.getItem("SlicedCheese")
    val peanutbutter = inventory.getItem("PeanutButter")

    // buy 2 apples get peanutbutter half
    for {
      a <- apple
      aPricing = PricingPolicy(a.name, a.price.amount.toDouble, 1.0)
      p <- peanutbutter
      pbPricing =  PricingPolicy(p.name, p.price.amount.toDouble, 0.5)
    } yield inventory.addBundledPrice(List(aPricing, aPricing, pbPricing))

    //buy 2 breads get peanutbutter half
    for {
      b <- bread
      bPricing = PricingPolicy(b.name, b.price.amount.toDouble, 1.0)
      p <- peanutbutter
      pbPricing =  PricingPolicy(p.name, p.price.amount.toDouble, 0.5)
    } yield inventory.addBundledPrice(List(bPricing, bPricing, pbPricing))

    // buy 3 cereals get milk free
    for {
      c <- cereal
      cPricing = PricingPolicy(c.name, c.price.amount.toDouble, 1.0)
      m <- milk
      mPricing = PricingPolicy(m.name, m.price.amount.toDouble, 0.0)
    } yield inventory.addBundledPrice(List(cPricing, cPricing, cPricing, mPricing))

    //buy 2 milks get slicedcheese half
    for {
      m <- milk
      mPricing = PricingPolicy(m.name, m.price.amount.toDouble, 1.0)
      sc <- slicedCheese
      scPricing = PricingPolicy(sc.name, sc.price.amount.toDouble, 0.5)
    } yield inventory.addBundledPrice(List(mPricing, mPricing, scPricing))

    //buy 3 apples get the 4th apple free
    apple flatMap {a =>
      val aPricing = PricingPolicy(a.name, a.price.amount.toDouble, 1.0)
      val aFree = PricingPolicy(a.name, a.price.amount.toDouble, 0.0)
      inventory.addBundledPrice(List(aPricing, aPricing, aPricing, aFree))
    }

    //buy 1 bread get the 2nd one free
    bread flatMap {b => 
      val bPricing = PricingPolicy(b.name, b.price.amount.toDouble, 1.0)
      val bFree = PricingPolicy(b.name, b.price.amount.toDouble, 0.0)
      inventory.addBundledPrice(List(bPricing, bFree))
    }

    //buy 2 cereals get the 3rd half
    cereal flatMap {c =>
      val cPricing = PricingPolicy(c.name, c.price.amount.toDouble, 1.0)
      val cHalf = PricingPolicy(c.name, c.price.amount.toDouble, 0.5)
      inventory.addBundledPrice(List(cPricing, cPricing, cHalf))
    }

    //buy 1 milk get the 2nd milk free
    milk flatMap {m =>
      val mPricing = PricingPolicy(m.name, m.price.amount.toDouble, 1.0)
      val mFree = PricingPolicy(m.name, m.price.amount.toDouble, 0.0)
      inventory.addBundledPrice(List(mPricing, mFree))
    }

    //buy 1 peanutbutter get the 2nd half
    peanutbutter flatMap {p =>
      val pbPricing =  PricingPolicy(p.name, p.price.amount.toDouble, 1.0)
      val pbHalf =  PricingPolicy(p.name, p.price.amount.toDouble, 0.5)
      inventory.addBundledPrice(List(pbPricing, pbHalf))
    }

    //buy 1 slicedCheese get the 2nd half
    slicedCheese flatMap { sc =>
      val scPricing = PricingPolicy(sc.name, sc.price.amount.toDouble, 1.0)
      val scHalf = PricingPolicy(sc.name, sc.price.amount.toDouble, 0.5)
      inventory.addBundledPrice(List(scPricing, scHalf))
    }
  }

  def shoppingCart(inventory: Inventory)(implicit ec: ExecutionContext): Future[List[Item]] = {
    //execute Futures in parallel
    val bread = inventory.getItem("Bread")
    val peanutbutter = inventory.getItem("PeanutButter")
    val milk = inventory.getItem("Milk")
    val cereal = inventory.getItem("Cereal")

    for {
      b <- bread
      p <- peanutbutter
      m <- milk
      c <- cereal
    } yield List(b, b, p, m, c, c, c, m)
  }
    
}