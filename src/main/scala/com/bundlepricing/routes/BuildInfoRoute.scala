package com.bundlepricing.routes

import com.bundlepricing.BuildInfo
import play.api.libs.json.Json
import spray.httpx.PlayJsonSupport
import spray.routing.Directives

trait BuildInfoRoute extends Directives with PlayJsonSupport {

  //don't give context path because each micro service runs on designated port
  val buildInfoRoute =
    path("buildinfo" ~ Slash.?) {
      get {
        complete(Json.toJson(BuildInfo.toMap.mapValues(_.toString)))
      }
    }

}
