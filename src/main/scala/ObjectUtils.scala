package com.carlosedp.scalautils

object ObjectUtils {

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

  implicit class Long32Bit(n: Long) {
    /*
     * Truncstes a long to 32-bit.
     * @return The 32-bit long.
     */
    def to32Bit: Long = n & 0xffffffffL
  }

  /*
   * Truncstes a int to 32-bit and converts to long.
   * @return The 32-bit long.
   */
  implicit class Int32Bit(n: Int) {
    def to32Bit: Long = n.toLong & 0xffffffffL
  }
}
