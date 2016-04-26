package com.bundlepricing.repos

import com.mongodb.casbah.{MongoClient, MongoClientURI}
import com.novus.salat._
import squants.thermal.{ Fahrenheit, Temperature }
import squants.motion.{ UsMilesPerHour, Velocity }
import squants.radio.{ Irradiance, WattsPerSquareMeter }

import com.bundlepricing.domains.QuantityTransformer

/**
 * Standard implicits.
 */
object Implicits {
  
  object Salat {

    implicit val mongoClient = MongoClient(MongoClientURI("mongodb://localhost:27017"))
    
    implicit val salatContext = new Context() {
      override val name = "demo-bundle-price-salat-context"
      override val typeHintStrategy = StringTypeHintStrategy(when = TypeHintFrequency.WhenNecessary, typeHint = "_typeHint")

      registerCustomTransformer(new QuantityTransformer[Temperature](Fahrenheit))
      registerCustomTransformer(new QuantityTransformer[Velocity](UsMilesPerHour))
      registerCustomTransformer(new QuantityTransformer[Irradiance](WattsPerSquareMeter))
    }
  }

}
