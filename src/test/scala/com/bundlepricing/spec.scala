package com.bundlepricing

import org.scalatest._

trait UnitSpec extends FlatSpecLike with MustMatchers with GivenWhenThen {

  /*
   * Disable "should" and "can" verbs to force consistent use of "must".
   * Disabled through overriding the implicit conversions
   */
  override def convertToStringShouldWrapper(o: String) = super.convertToStringShouldWrapper(o)
  override def convertToStringCanWrapper(o: String) = super.convertToStringCanWrapper(o)

}
