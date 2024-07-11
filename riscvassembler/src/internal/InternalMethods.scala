package com.carlosedp.riscvassembler

import scala.collection.mutable.ArrayBuffer
import scala.util.Try

import com.carlosedp.riscvassembler.ObjectUtils._

protected object LineParser {

  /**
   * Parses input string lines to generate the list of instructions, addresses
   * and label addresses
   *
   * @param input
   *   input multiline assembly string
   * @return
   *   a tuple containing:
   *   - `ArrayBuffer[String]` with the assembly instruction
   *   - `ArrayBuffer[String]` with the assembly instruction address
   *   - `Map[String, String]` with the assembly label addresses
   */
  def apply(input: String): (ArrayBuffer[String], ArrayBuffer[String], Map[String, String]) = {
    val instList = input.split("\n").toList.map(_.trim).filter(_.nonEmpty)
    val ignores  = Seq(".", "/")

    // Filter lines which begin with characters from `ignores`
    val instListFilter = instList.filterNot(l => ignores.contains(l.trim().take(1))).toIndexedSeq

    // Remove inline comments
    val instListNocomment = instListFilter.map(_.split("/")(0).trim).toIndexedSeq

    var idx              = 0
    val instructions     = ArrayBuffer.empty[String]
    val instructionsAddr = ArrayBuffer.empty[String]
    val labelIndex       = scala.collection.mutable.Map[String, String]()

    instListNocomment.foreach { data =>
      // That's an ugly parser, but works for now :)
      // println(s"-- Processing line: $data, address: ${(idx * 4L).toHexString}")
      val hasLabel = data.indexOf(":")
      if (hasLabel != -1) {
        if (""".+:\s*(\/.*)?$""".r.findFirstIn(data).isDefined) {
          // Has label without code, this label points to next address
          labelIndex(data.split(":")(0).replace(":", "")) = ((idx + 1) * 4L).toHexString
          idx += 1
        } else {
          // Has label and code in the same line, this label points to this address
          labelIndex(data.split(':')(0).replace(":", "").trim) = (idx * 4L).toHexString
          instructions.append(data.split(':')(1).trim)
          instructionsAddr.append((idx * 4L).toHexString)
          idx += 1
        }
      } else {
        instructions.append(data.trim)
        instructionsAddr.append((idx * 4L).toHexString)
        idx += 1
      }
    }
    (instructions, instructionsAddr, labelIndex.toMap)
  }
}

protected object InstructionParser {

  /**
   * Parse an assembly instruction and return the opcode and opdata
   *
   * @param input
   *   the assembly instruction string
   * @param addr
   *   the assembly instruction address
   * @param labelIndex
   *   the index containing the label addresses
   * @return
   *   a tuple containing the Instruction and opdata
   */
  def apply(
      input:      String,
      addr:       String = "0",
      labelIndex: Map[String, String] = Map[String, String](),
    ): Option[
    (Instruction, Map[String, Long])
  ] = {
    // The regex splits the input into groups (dependind on type):
    // (0) - Instruction name
    // (1) - Instruction rd
    // (2) - Instruction rs1/imm
    // (3) - Instruction rs2/rs
    val parsed = input.trim.split("[\\s,\\(\\)]+").filter(_.nonEmpty)

    // Check if it's a pseudo-instruction
    val instructionParts = PseudoInstructions(parsed).getOrElse(parsed)

    val inst = Instructions(instructionParts(0)) match {
      case Some(i) => i
      case _       => return None
    }

    inst.instType match {
      case InstType.R =>
        if (instructionParts.length != 4) return None
        Some(
          (
            inst,
            Map(
              "rd"  -> RegMap(instructionParts(1)),
              "rs1" -> RegMap(instructionParts(2)),
              "rs2" -> RegMap(instructionParts(3)),
            ),
          )
        )
      case InstType.I => {
        // First check if instruction has appropriate arguments
        if (
          instructionParts.length != 4 && !Seq("ECALL", "EBREAK", "FENCE.I", "FENCE").contains(
            instructionParts(0).toUpperCase
          )
        )
          return None
        // Treat instructions that contains offsets (Loads)
        if (inst.hasOffset) {
          val imm =
            if (instructionParts(2).startsWith("0x")) instructionParts(2).substring(2).h
            else instructionParts(2).toLong
          Some(
            (
              inst,
              Map(
                "rd"  -> RegMap(instructionParts(1)),
                "rs1" -> RegMap(instructionParts(3)),
                "imm" -> imm,
              ),
            )
          )
        } else {
          // Treat instructions with no arguments
          if (Seq("ECALL", "EBREAK", "FENCE.I").contains(instructionParts(0).toUpperCase)) {
            val imm = inst.fixed.b
            Some(
              (
                inst,
                Map(
                  "rd"  -> 0,
                  "rs1" -> 0,
                  "imm" -> imm,
                ),
              )
            )
          } else if (Seq("FENCE").contains(instructionParts(0).toUpperCase)) {
            // Treat FENCE instruction
            val imm = if (instructionParts.length == 3) {
              val pred = instructionParts(1)
                .map(_ match {
                  case bit if bit.toLower == 'i' => 8
                  case bit if bit.toLower == 'o' => 4
                  case bit if bit.toLower == 'r' => 2
                  case bit if bit.toLower == 'w' => 1
                  case _                         => 0
                })
                .sum
                .toBinaryString
              val succ = instructionParts(2)
                .map(_ match {
                  case bit if bit.toLower == 'i' => 8
                  case bit if bit.toLower == 'o' => 4
                  case bit if bit.toLower == 'r' => 2
                  case bit if bit.toLower == 'w' => 1
                  case _                         => 0
                })
                .sum
                .toBinaryString
              ("0000" + pred + succ).b
            } else {
              "000011111111".b
            }
            Some(
              (
                inst,
                Map(
                  "rd"  -> 0,
                  "rs1" -> 0,
                  "imm" -> imm,
                ),
              )
            )
          } else {
            // Treat other I instructions (Shifts)
            val shamt =
              if (instructionParts(3).startsWith("0x")) instructionParts(3).substring(2).h
              else instructionParts(3).toLong
            val imm = if (inst.fixed != "") {
              if (shamt >= 64) return None // Shamt has 5 bits
              // If instruction contains fixed imm (like SRAI, SRLI, SLLI), use the fixed imm padded right to fill 12 bits
              (inst.fixed + shamt.toBinaryString.padZero(5).takeRight(5)).b
            } else {
              if (instructionParts(3).startsWith("0x")) instructionParts(3).substring(2).h
              else instructionParts(3).toLong
            }
            Some(
              (
                inst,
                Map(
                  "rd"  -> RegMap(instructionParts(1)),
                  "rs1" -> RegMap(instructionParts(2)),
                  "imm" -> imm,
                ),
              )
            )
          }
        }
      }
      case InstType.S => {
        if (instructionParts.length != 4) return None
        val imm =
          if (instructionParts(2).startsWith("0x")) instructionParts(2).substring(2).h
          else instructionParts(2).toLong
        Some(
          (
            inst,
            Map(
              "rs2" -> RegMap(instructionParts(1)),
              "rs1" -> RegMap(instructionParts(3)),
              "imm" -> imm,
            ),
          )
        )
      }
      case InstType.B => {
        if (instructionParts.length != 4) return None
        val imm = instructionParts(3) match {
          case i if i.startsWith("0x")      => i.substring(2).h
          case i if Try(i.toLong).isFailure => labelIndex(i).h - addr.h
          case i                            => i.toLong
        }
        Some(
          (
            inst,
            Map(
              "rs1" -> RegMap(instructionParts(1)),
              "rs2" -> RegMap(instructionParts(2)),
              "imm" -> imm,
            ),
          )
        )
      }
      case InstType.U | InstType.J => {
        if (instructionParts.length != 3) return None
        val imm = instructionParts(2) match {
          case i if i.startsWith("0x")      => i.substring(2).h
          case i if Try(i.toLong).isFailure => labelIndex(i).h - addr.h
          case i                            => i.toLong
        }
        Some((inst, Map("rd" -> RegMap(instructionParts(1)), "imm" -> imm)))
      }
      case _ =>
        None
    }
  }
}

protected object FillInstruction {

  /**
   * Fills the instruction arguments based on instruction type
   *
   * @param op
   *   the instruction opcode and type
   * @param data
   *   the received instruction arguments
   * @return
   *   the filled instruction binary
   */
  def apply(op: Instruction, data: Map[String, Long]): String =
    op.instType match {
      case InstType.R => {
        val rd  = data("rd").toBinaryString.padZero(5)
        val rs1 = data("rs1").toBinaryString.padZero(5)
        val rs2 = data("rs2").toBinaryString.padZero(5)
        op.funct7 + rs2 + rs1 + op.funct3 + rd + op.opcode
      }

      case InstType.I => {
        val rd  = data("rd").toBinaryString.padZero(5)
        val rs1 = data("rs1").toBinaryString.padZero(5)
        val imm = data("imm").to32Bit.toBinaryString.padZero(12)
        imm + rs1 + op.funct3 + rd + op.opcode
      }

      case InstType.S => {
        val rs1 = data("rs1").toBinaryString.padZero(5)
        val rs2 = data("rs2").toBinaryString.padZero(5)
        val imm = data("imm").to32Bit.toBinaryString.padZero(12).reverse // reverse to have binary in little endian
        imm.slice(5, 12).reverse + rs2 + rs1 + op.funct3 + imm.slice(0, 5).reverse + op.opcode
      }

      case InstType.B => {
        val rs1 = data("rs1").toBinaryString.padZero(5)
        val rs2 = data("rs2").toBinaryString.padZero(5)
        val imm = data("imm").to32Bit.toBinaryString.padZero(13).reverse // reverse to have binary in little endian
        imm.slice(12, 13).reverse + imm.slice(5, 11).reverse + rs2 + rs1 + op.funct3 +
          imm.slice(1, 5).reverse + imm.slice(11, 12).reverse + op.opcode
      }

      case InstType.U => {
        val rd  = data("rd").toBinaryString.padZero(5)
        val imm = data("imm").to32Bit.toBinaryString.padZero(20)
        imm + rd + op.opcode
      }

      case InstType.J => {
        val rd  = data("rd").toBinaryString.padZero(5)
        val imm = data("imm").to32Bit.toBinaryString.padZero(21).reverse // reverse to have binary in little endian
        imm.slice(20, 21).reverse + imm.slice(1, 11).reverse + imm.slice(11, 12).reverse + imm
          .slice(12, 20)
          .reverse + rd + op.opcode
      }
    }
}

protected object RegMap {

  /**
   * Maps the register name or ABI name to the register number
   *
   * @param regName
   *   the register name
   * @return
   *   the register number
   */
  def apply(input: String): Long = input.toLowerCase match {
    case "x0" | "zero"      => 0
    case "x1" | "ra"        => 1
    case "x2" | "sp"        => 2
    case "x3" | "gp"        => 3
    case "x4" | "tp"        => 4
    case "x5" | "t0"        => 5
    case "x6" | "t1"        => 6
    case "x7" | "t2"        => 7
    case "x8" | "s0" | "fp" => 8
    case "x9" | "s1"        => 9
    case "x10" | "a0"       => 10
    case "x11" | "a1"       => 11
    case "x12" | "a2"       => 12
    case "x13" | "a3"       => 13
    case "x14" | "a4"       => 14
    case "x15" | "a5"       => 15
    case "x16" | "a6"       => 16
    case "x17" | "a7"       => 17
    case "x18" | "s2"       => 18
    case "x19" | "s3"       => 19
    case "x20" | "s4"       => 20
    case "x21" | "s5"       => 21
    case "x22" | "s6"       => 22
    case "x23" | "s7"       => 23
    case "x24" | "s8"       => 24
    case "x25" | "s9"       => 25
    case "x26" | "s10"      => 26
    case "x27" | "s11"      => 27
    case "x28" | "t3"       => 28
    case "x29" | "t4"       => 29
    case "x30" | "t5"       => 30
    case "x31" | "t6"       => 31
  }
}
