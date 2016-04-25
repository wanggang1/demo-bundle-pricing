package com.bundlepricing.utils

object CombinatorialFunction {

  /**
   * concatenate combinations for all number of elements
   * from xs.combinations(0) to xs.combinations(xs.length -1)
   * 
   * This method will not eliminates the duplicates as List.combinations does, so given 
   * val breads = List("Bread", "Bread", "Bread", "Bread"), breads.combinations(2)
   * will produce only List(List("Bread", "Bread")).  But this method will return all
   * the elements even if they are the same.  This is intended in this computation.
   */
  def subsets[T](xs: List[T]): List[List[T]] =
    xs.foldLeft(List(List.empty[T])) { (acc, el) => acc ++ acc.map(el :: _) }
  
  /*
  def subsets[T](xs: List[T]): List[List[T]] = xs match {
    case Nil => List(Nil)
    case y :: ys => 
      val zss = subs(ys)
      zss ++ zss.map(zs => y :: zs)
  }
  */
  
}
