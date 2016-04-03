package com.bundlepricing.utils

object CombinatorialFunction {
  /*
  def subsets[T](xs: List[T]): List[List[T]] = xs match {
    case Nil => List(Nil)
    case y :: ys => 
      val zss = subs(ys)
      zss ++ zss.map(zs => y :: zs)
  }
  */
  def subsets[T](xs: List[T]): List[List[T]] =
    xs.foldLeft(List(List.empty[T])) { (acc, el) => acc ++ acc.map(el :: _) }
}
