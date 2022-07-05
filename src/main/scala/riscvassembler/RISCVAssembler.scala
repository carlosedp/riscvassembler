package com.carlosedp.scalautils.riscvassembler

import scala.io.Source

import util.control.Breaks._

object RISCVAssembler {

  /** Generate an hex string output fom the assembly source file
    *
    * Usage:
    *
    * {{{
    * val outputHex = RISCVAssembler.fromFile("input.asm")
    * }}}
    *
    * @param fileName
    *   the assembly source file
    * @return
    *   the output hex string
    */
  def fromFile(filename: String): String = {
    val f = Source.fromFile(filename).getLines().mkString("\n")
    fromString(f)
  }

  /** Generate the binary output for the input instruction
    * @param input
    *   the input instruction (eg. add x1, x2, x3)
    * @return
    *   the binary output in string format
    */
  def binOutput(input: String, width: Int = 32): String = {
    val (op, opdata) = InstructionParser(input)
    FillInstruction(op("inst_type"), opdata, op).takeRight(width)
  }

  /** Generate an hex string output fom the assembly string
    *
    * Usage:
    *
    * {{{
    * val input =
    *       """
    *       addi x1 , x0,   1000
    *       addi x2 , x1,   2000
    *       addi x3 , x2,  -1000
    *       addi x4 , x3,  -2000
    *       addi x5 , x4,   1000
    *       """.stripMargin
    *     val outputHex = RISCVAssembler.fromString(input)
    * }}}
    *
    * @param input
    *   input assembly string to assemble (multiline string)
    * @return
    *   the assembled hex string
    */
  def fromString(
    input: String
  ): String = {
    var outputString = ""
    val instList     = input.split("\n").toList.filter(_.nonEmpty).filter(!_.trim().isEmpty()).map(_.trim)

    val ignores = Seq(".", "_", "/")
    for (instruction <- instList) {
      // Ignore asm labels and directives
      breakable {
        for (i <- ignores) {
          if (instruction.trim.startsWith(i)) break()
        }
        // Look for labels and remove them if found
        val hasLabel = instruction.indexOf(":")
        val inst =
          if (hasLabel != -1) instruction.substring(hasLabel + 1)
          else instruction
        // Parse arguments
        val binString = binOutput(inst)
        outputString += GenHex(binString)
        outputString += "\n"
      }
    }
    outputString
  }
}
