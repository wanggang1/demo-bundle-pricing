package org.bundlepricing.actors.collectors

sealed trait ResultState
case object Full extends ResultState
case object CollectorTimeout extends ResultState
case object NotFound extends ResultState

case class Result[T](values: Iterable[T], state: ResultState)