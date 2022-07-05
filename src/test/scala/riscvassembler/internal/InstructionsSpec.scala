package com.carlosedp.scalautils.riscvassembler

import org.scalatest.flatspec._
import org.scalatest.matchers.should._

import scala.{Tuple2 => &}

class InstructionsSpec extends AnyFlatSpec with Matchers {
  behavior of "Instructions"

  it should "fetch R-type instructions" in {
    val insts   = List("ADD", "SUB", "SLL", "SRL", "SRA", "XOR", "OR", "AND", "SLT", "SLTU")
    val funct3s = List("000", "000", "001", "101", "101", "100", "110", "111", "010", "011")
    val funct7s =
      List("0000000", "0100000", "0000000", "0000000", "0100000", "0000000", "0000000", "0000000", "0000000", "0000000")
    for (i & f3 & f7 <- insts zip funct3s zip funct7s) {
      val inst = Instructions(i)
      inst should be(
        Map(
          "inst_name" -> i,
          "funct7"    -> f7,
          "funct3"    -> f3,
          "opcode"    -> "0110011",
          "inst_type" -> "INST_R"
        )
      )
    }
  }

  it should "fetch I-type instructions" in {
    val insts   = List("ADDI", "XORI", "ORI", "ANDI", "SLTI", "SLTIU")
    val funct3s = List("000", "100", "110", "111", "010", "011")
    for (i & f3 <- insts zip funct3s) {
      val inst = Instructions(i)
      inst should be(
        Map(
          "inst_name" -> i,
          "funct3"    -> f3,
          "opcode"    -> "0010011",
          "inst_type" -> "INST_I"
        )
      )
    }
  }

  it should "fetch I-type CSR instructions" in {
    val insts   = List("CSRRW", "CSRRS", "CSRRC", "CSRRWI", "CSRRSI", "CSRRCI")
    val funct3s = List("001", "010", "011", "101", "110", "111")
    for (i & f3 <- insts zip funct3s) {
      val inst = Instructions(i)
      inst should be(
        Map(
          "inst_name" -> i,
          "funct3"    -> f3,
          "is_csr"    -> "true",
          "opcode"    -> "1110011",
          "inst_type" -> "INST_I"
        )
      )
    }
  }

  it should "fetch I-type Load instructions" in {
    val insts   = List("LB", "LH", "LBU", "LHU", "LW")
    val funct3s = List("000", "001", "100", "101", "010")
    for (i & f3 <- insts zip funct3s) {
      val inst = Instructions(i)
      inst should be(
        Map(
          "inst_name"  -> i,
          "funct3"     -> f3,
          "has_offset" -> "true",
          "opcode"     -> "0000011",
          "inst_type"  -> "INST_I"
        )
      )
    }
  }

  it should "fetch I-type Shift instructions" in {
    val insts   = List("SLLI", "SRLI", "SRAI")
    val funct3s = List("001", "101", "101")
    val fixs    = List("0000000", "0000000", "0100000")
    for (i & f3 & f <- insts zip funct3s zip fixs) {
      val inst = Instructions(i)
      inst should be(
        Map(
          "inst_name" -> i,
          "funct3"    -> f3,
          "fix"       -> f,
          "opcode"    -> "0010011",
          "inst_type" -> "INST_I"
        )
      )
    }
  }

  it should "fetch JALR instruction" in {
    val i = Instructions("JALR")
    i should be(
      Map("inst_name" -> "JALR", "opcode" -> "1100111", "inst_type" -> "INST_I", "funct3" -> "000")
    )
  }

  it should "fetch JAL instruction" in {
    val i = Instructions("JAL")
    i should be(
      Map("inst_name" -> "JAL", "opcode" -> "1101111", "inst_type" -> "INST_J")
    )
  }

  it should "fetch LUI instruction" in {
    val i = Instructions("LUI")
    i should be(
      Map("inst_name" -> "LUI", "opcode" -> "0110111", "inst_type" -> "INST_U")
    )
  }

  it should "fetch AUIPC instruction" in {
    val i = Instructions("AUIPC")
    i should be(
      Map("inst_name" -> "AUIPC", "opcode" -> "0010111", "inst_type" -> "INST_U")
    )
  }

  it should "fetch B-type Branch instructions" in {
    val insts   = List("BEQ", "BNE", "BLT", "BGE", "BLTU", "BGEU")
    val funct3s = List("000", "001", "100", "101", "110", "111")
    for ((i, f3) <- insts zip funct3s) {
      // for ((i, f3) <- insts.lazyZip(funct3s).toList) {
      val inst = Instructions(i)
      inst should be(
        Map(
          "inst_name" -> i,
          "funct3"    -> f3,
          "opcode"    -> "1100011",
          "inst_type" -> "INST_B"
        )
      )
    }
  }

  it should "fetch S-type Store instructions" in {
    val insts   = List("SB", "SH", "SW")
    val funct3s = List("000", "001", "010")
    for ((i, f3) <- insts zip funct3s) {
      val inst = Instructions(i)
      inst should be(
        Map(
          "inst_name" -> i,
          "funct3"    -> f3,
          "opcode"    -> "0100011",
          "inst_type" -> "INST_S"
        )
      )
    }
  }

  behavior of "Pseudo-Instructions"

  it should "map pseudo to real instructions" in {
    val pinsts = List("NOP", "BEQZ", "BGEZ")
    val insts  = List("ADDI", "BEQ", "BGE")
    for ((pi, i) <- pinsts zip insts) {
      val inst = Instructions(pi)

      inst("inst_name") should be(i)
      inst("pseudo_inst") should be("true")
    }
  }

  it should "map NOP Pseudo Instruction" in {
    val i = PseudoInstructions(Array("nop"))
    i should be(
      Array("addi", "x0", "x0", "0")
    )
  }

  it should "map BEQZ Pseudo Instruction" in {
    val i = PseudoInstructions(Array("beqz", "x1", "4"))
    i should be(
      Array("beq", "x1", "x0", "4")
    )
  }
  it should "map BGEZ Pseudo Instruction" in {
    val i = PseudoInstructions(Array("bgez", "x1", "4"))
    i should be(
      Array("bge", "x1", "x0", "4")
    )
  }
}
