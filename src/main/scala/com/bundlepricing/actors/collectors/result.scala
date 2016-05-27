package com.bundlepricing.actors.collectors

sealed trait ResultState
case object Full extends ResultState
case object Partial extends ResultState

case class Result[T](values: Iterable[T], state: ResultState)
