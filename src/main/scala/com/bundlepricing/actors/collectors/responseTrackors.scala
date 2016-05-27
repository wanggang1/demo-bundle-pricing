package com.bundlepricing.actors.collectors

/**
 * tracking responses
 */
trait ResponseTracker[T] {
  def addResponse(response: T): ResponseTracker[T]
  def isDone: Boolean
}

/**
 * tracking that certain number of responses have been received
 */
object Countdown {
  def apply(expectedMessagesCount: Int) = new Countdown(expectedMessagesCount)
}

class Countdown(expectedMessagesCount: Int) extends ResponseTracker[Any] {
  require(expectedMessagesCount >= 0)

  def isDone: Boolean = expectedMessagesCount == 0

  def addResponse(response: Any): Countdown = Countdown((expectedMessagesCount - 1) max 0)
}

/**
 *  tracking IDs in certain responses.
 */
object MatchIds {
  def apply[Msg, Id](expectedIds: Set[Id], toId: Msg => Id): MatchIds[Msg, Id] =
    new MatchIds(expectedIds, toId)
}

class MatchIds[Msg, Id](expectedIds: Set[Id], toId: Msg => Id)
  extends ResponseTracker[Msg] {

  def isDone: Boolean = expectedIds.isEmpty

  def addResponse(response: Msg): MatchIds[Msg, Id] = MatchIds(expectedIds - toId(response), toId)
}
