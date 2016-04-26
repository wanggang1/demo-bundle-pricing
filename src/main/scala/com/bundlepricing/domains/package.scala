package com.bundlepricing

import com.mongodb.casbah.Imports._
import com.novus.salat.transformers.CustomTransformer
import squants.{ UnitOfMeasure, Quantity }

package object domains {

  class QuantityTransformer[T <: Quantity[T]: Manifest](unitOfMeasure: UnitOfMeasure[T]) extends CustomTransformer[T, DBObject] {
    def deserialize(dbObject: DBObject) = unitOfMeasure(dbObject.getAsOrElse[Double]("value", 0.0))
    def serialize(q: T) = DBObject("value" -> q.to(unitOfMeasure), "unit" -> unitOfMeasure.symbol)
  }

}
