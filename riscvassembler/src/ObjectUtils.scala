package com.carlosedp.riscvassembler

object ObjectUtils {
  implicit class StringWithPad(
    s: String,
  ) {

    /**
     * Left-pads a string to a specified length with a specified character.
     * @param length
     *   The length to pad to.
     * @param padChar
     *   The character to pad with.
     */
    def padStr(
      length:  Int,
      padChar: Char,
    ): String =
      s.reverse.padTo(length, padChar).reverse

    /**
     * Left-pads a string to a certain length with zero (mostly used for binary
     * strings).
     * @param length
     *   The length to pad to.
     */
    def padZero(
      length: Int,
    ): String =
      s.padStr(length, '0')
  }

  /**
   * Number manipulation functions
   */
  implicit class NumericManipulation[T: Numeric](
    x: T,
  )(
    implicit n: Numeric[T],
  ) {

    /**
     * Truncates a number to 32-bit and returns a Long.
     * @return
     *   The 32-bit long.
     */
    def to32Bit: Long = n.toLong(x) & 0xffffffffL
  }

  /**
   * Convert a string in a specified base to a Long
   * @return
   *   The Long converted from the string.
   */
  implicit class StringToLong(
    digits: String,
  ) {

    /**
     * Convert a string in a specified base to a Long
     * @param b
     *   The base of the string.
     * @return
     *   The Long converted from the string.
     */
    private def base(
      b: Int,
    ): Long = BigInt(digits, b).toLong

    /**
     * Convert a string to a base 2 (binary) Long
     * @param b
     *   The base of the string.
     * @return
     *   The Long converted from the string.
     */
    def b = base(2)

    /**
     * Convert a string to a base 8 (octal) Long
     * @param b
     *   The base of the string.
     * @return
     *   The Long converted from the string.
     */
    def o = base(8)

    /**
     * Convert a string to a base 16 (hex) Long
     * @param b
     *   The base of the string.
     * @return
     *   The Long converted from the string.
     */
    def h = base(16)
  }
}
