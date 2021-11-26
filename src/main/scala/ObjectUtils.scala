package com.carlosedp.scalautils

object ObjectUtils {

  /** String padding functions
    *
    * @param s
    */
  implicit class StringWithPad(s: String) {

    /** Left-pads a string to a certain length with a certain character.
      * @param length
      *   The length to pad to.
      * @param padChar
      *   The character to pad with.
      */
    def padStr(length: Int, padChar: Char): String =
      s.reverse.padTo(length, padChar).reverse

    /** Left-pads a string to a certain length with zero (mostly used for binary strings).
      * @param length
      *   The length to pad to.
      */
    def padZero(length: Int): String =
      s.padStr(length, '0')
  }

  /** Number manipulation functions
    */
  implicit class NumericManipulation[T: Numeric](x: T)(implicit n: Numeric[T]) {

    /** Truncates a number to 32-bit and returns a Long.
      * @return
      *   The 32-bit long.
      */
    def to32Bit: Long = n.toLong(x) & 0xffffffffL
  }
}
