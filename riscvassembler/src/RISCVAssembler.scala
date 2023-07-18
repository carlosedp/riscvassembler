package com.carlosedp.riscvassembler

import scala.io.Source

import ObjectUtils._

object RISCVAssembler {

  /**
   * AppInfo contains version and build information for the library
   *
   * Fields:
   *   - `appName`: artifactName
   *   - `appVersion`: Version derived from git tag or git `tag+1-SNAPSHOT`
   *   - `revision`: Generated revision based on tag, commit and number of
   *     commits after last tag
   *   - `buildCommit`: Commit ID
   *   - `commitDate`: Last commit date
   *   - `buildDate`: Build date
   */
  val AppInfo = BuildInfo

  /**
   * Generate an hex string output fom the assembly source file
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
  def fromFile(
    filename: String,
  ): String =
    fromString(Source.fromFile(filename).getLines().mkString("\n"))

  /**
   * Generate an hex string output fom the assembly string
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
    input: String,
  ): String = {
    val (instructions, addresses, labels) = LineParser(input)
    (instructions zip addresses).map { case (i: String, a: String) => { binOutput(i, a, labels) } }
      .map(hexOutput(_))
      .mkString("\n") + "\n"
  }

  /**
   * Generate the binary output for the input instruction
   * @param input
   *   the input instruction (eg. "add x1, x2, x3")
   * @return
   *   the binary output in string
   */
  def binOutput(
    instruction: String,
    address:     String = "0",
    labelIndex:  Map[String, String] = Map[String, String](),
    width:       Int = 32,
  ): String = {
    val cleanInst = "\\/\\*.*\\*\\/".r.replaceAllIn(instruction, "").toLowerCase.trim

    InstructionParser(cleanInst, address, labelIndex) match {
      case Some((op, opdata)) => FillInstruction(op, opdata).takeRight(width)
      case _                  => "0" * width
    }

  }

  /**
   * Generate the hex string of the instruction from binary
   *
   * @param input
   *   the binary string of the instruction
   * @return
   *   the hex string of the instruction in string
   */
  def hexOutput(
    input: String,
  ): String = {
    val x = input.b
    f"0x$x%08X".toString.takeRight(8)
  }
}
