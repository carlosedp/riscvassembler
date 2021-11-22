package com.carlosedp.scalautils.riscvassembler

import scala.io.Source

object RISCVAssembler {

  /**
   * Generate an hex string output fom the assembly source file
   *
   * Usage:
   *
   * {{{
   * val outputHex = RISCVAssembler.fromFile("input.asm")
   * }}}
   *
   * @param fileName the assembly source file
   * @return the output hex string
   */
  def fromFile(filename: String): String = {
    val f = Source.fromFile(filename).getLines().mkString("\n")
    fromString(f)
  }

  /**
   * Generate an hex string output fom the assembly string
   *
   * Usage:
   *
   * {{{
   * val input =
   *      """
   *      addi x1 , x0,   1000
   *      addi x2 , x1,   2000
   *      addi x3 , x2,  -1000
   *      addi x4 , x3,  -2000
   *      addi x5 , x4,   1000
   *        """.stripMargin
   *    val outputHex = RISCVAssembler.fromString(input)
   * }}}
   *
   * @param input input assembly string to assemble (multiline string)
   * @return the assembled hex string
   */
  def fromString(
    input: String
  ): String = {
    var outputString = ""
    val instList     = input.split("\n").toList.filter(_.nonEmpty).filter(!_.isBlank()).map(_.trim)
    var numInst      = instList.length

    for (instruction <- instList) {
      val (op, opdata) = InstructionParser(instruction)
      val outputBin    = FillInstruction(op("inst_type"), opdata, op)
      outputString += GenHex(outputBin)
      if (numInst > 1) outputString += "\n"
      numInst -= 1
    }
    outputString
  }
}
