package com.bundlepricing.routes

import org.bson.types.ObjectId
import shapeless.HNil
import spray.http.Uri.Path
import spray.routing.PathMatcher.{ Matched, Unmatched }
import spray.routing.PathMatcher1

object MongoObjectId extends PathMatcher1[ObjectId] {
  def apply(path: Path) = path match {
    case Path.Segment(segment, tail) if ObjectId.isValid(segment) ⇒ Matched(tail, new ObjectId(segment) :: HNil)
    case _ ⇒ Unmatched
  }
}
