package com.bundlepricing.repos

import com.mongodb.casbah.{MongoClient, MongoClientURI}
import com.novus.salat._
import squants.thermal.{ Fahrenheit, Temperature }
import squants.motion.{ UsMilesPerHour, Velocity }
import squants.radio.{ Irradiance, WattsPerSquareMeter }
import squants.market.{ Money, USD }

import com.bundlepricing.domains.QuantityTransformer
import com.bundlepricing.Settings

/**
 * Standard implicits.
 */
object Implicits {
  
  object Salat {

    implicit val mongoClient = MongoClient(MongoClientURI(Settings.dbUri))
    
    implicit val salatContext = new Context() {
      override val name = "demo-bundle-price-salat-context"
      override val typeHintStrategy = StringTypeHintStrategy(when = TypeHintFrequency.WhenNecessary, typeHint = "_typeHint")

      registerCustomTransformer(new QuantityTransformer[Temperature](Fahrenheit))
      registerCustomTransformer(new QuantityTransformer[Velocity](UsMilesPerHour))
      registerCustomTransformer(new QuantityTransformer[Irradiance](WattsPerSquareMeter))
      registerCustomTransformer(new QuantityTransformer[Money](USD))
    }
  }

}
