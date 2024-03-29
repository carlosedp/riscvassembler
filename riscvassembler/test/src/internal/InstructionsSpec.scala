package com.carlosedp.riscvassembler

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
      val inst = Instructions(i).get
      inst.name should be(i)
      inst.funct7 should be(f7)
      inst.funct3 should be(f3)
      inst.opcode should be("0110011")
      inst.instType should be(InstType.R)
    }
  }

  it should "fetch I-type instructions" in {
    val insts   = List("ADDI", "XORI", "ORI", "ANDI", "SLTI", "SLTIU")
    val funct3s = List("000", "100", "110", "111", "010", "011")
    for (i & f3 <- insts zip funct3s) {
      val inst = Instructions(i).get
      inst.name should be(i)
      inst.funct3 should be(f3)
      inst.opcode should be("0010011")
      inst.instType should be(InstType.I)
    }
  }

  it should "fetch I-type CSR instructions" in {
    val insts   = List("CSRRW", "CSRRS", "CSRRC", "CSRRWI", "CSRRSI", "CSRRCI")
    val funct3s = List("001", "010", "011", "101", "110", "111")
    for (i & f3 <- insts zip funct3s) {
      val inst = Instructions(i).get
      inst.name should be(i)
      inst.funct3 should be(f3)
      inst.opcode should be("1110011")
      inst.isCsr should be(true)
      inst.instType should be(InstType.I)
    }
  }

  it should "fetch I-type Load instructions" in {
    val insts   = List("LB", "LH", "LBU", "LHU", "LW")
    val funct3s = List("000", "001", "100", "101", "010")
    for (i & f3 <- insts zip funct3s) {
      val inst = Instructions(i).get
      inst.name should be(i)
      inst.funct3 should be(f3)
      inst.opcode should be("0000011")
      inst.instType should be(InstType.I)
    }
  }

  it should "fetch I-type Shift instructions" in {
    val insts   = List("SLLI", "SRLI", "SRAI")
    val funct3s = List("001", "101", "101")
    val fixs    = List("0000000", "0000000", "0100000")
    for (i & f3 & f <- insts zip funct3s zip fixs) {
      val inst = Instructions(i).get
      inst.name should be(i)
      inst.funct3 should be(f3)
      inst.fixed should be(f)
      inst.opcode should be("0010011")
      inst.instType should be(InstType.I)
    }
  }

  it should "fetch JALR instruction" in {
    val inst = Instructions("JALR").get
    inst.name should be("JALR")
    inst.funct3 should be("000")
    inst.opcode should be("1100111")
    inst.instType should be(InstType.I)
  }

  it should "fetch JAL instruction" in {
    val inst = Instructions("JAL").get
    inst.name should be("JAL")
    inst.opcode should be("1101111")
    inst.instType should be(InstType.J)
  }

  it should "fetch LUI instruction" in {
    val inst = Instructions("LUI").get
    inst.name should be("LUI")
    inst.opcode should be("0110111")
    inst.instType should be(InstType.U)
  }

  it should "fetch AUIPC instruction" in {
    val inst = Instructions("AUIPC").get
    inst.name should be("AUIPC")
    inst.opcode should be("0010111")
    inst.instType should be(InstType.U)
  }

  it should "fetch B-type Branch instructions" in {
    val insts   = List("BEQ", "BNE", "BLT", "BGE", "BLTU", "BGEU")
    val funct3s = List("000", "001", "100", "101", "110", "111")
    for ((i, f3) <- insts zip funct3s) {
      // for ((i, f3) <- insts.lazyZip(funct3s).toList) {
      val inst = Instructions(i).get
      inst.name should be(i)
      inst.funct3 should be(f3)
      inst.opcode should be("1100011")
      inst.instType should be(InstType.B)
    }
  }

  it should "fetch S-type Store instructions" in {
    val insts   = List("SB", "SH", "SW")
    val funct3s = List("000", "001", "010")
    for ((i, f3) <- insts zip funct3s) {
      val inst = Instructions(i).get
      inst.name should be(i)
      inst.funct3 should be(f3)
      inst.opcode should be("0100011")
      inst.instType should be(InstType.S)
    }
  }

  it should "fetch a invalid instruction" in {
    val inst = Instructions("invalid")
    inst should be(None)
  }

  behavior of "Instructions"

  it should "assemble SRAI Instruction" in {
    val i = RISCVAssembler.binOutput("srai x0, x0, 0")
    i should be(
      "01000000000000000101000000010011"
    )
  }

  it should "assemble SRAI Instruction with max shamt" in {
    val i = RISCVAssembler.binOutput("srai x0, x0, 63")
    i should be(
      "01000001111100000101000000010011"
    )
  }

  it should "assemble SRAI Instruction with invalid shamt" in {
    val i = RISCVAssembler.binOutput("srai x0, x0, 64")
    i should be(
      "00000000000000000000000000000000"
    )
  }

  it should "assemble FENCE Instruction" in {
    val i = RISCVAssembler.binOutput("fence iorw, iorw")
    i should be(
      "00001111111100000000000000001111"
    )
  }

  it should "assemble FENCE Instruction with input read" in {
    val i = RISCVAssembler.binOutput("fence ir, ir")
    i should be(
      "00001010101000000000000000001111"
    )
  }

  it should "assemble FENCE Instruction with no arguments" in {
    val i = RISCVAssembler.binOutput("fence")
    i should be(
      "00001111111100000000000000001111"
    )
  }

  it should "assemble FENCE.i Instruction" in {
    val i = RISCVAssembler.binOutput("fence.i")
    i should be(
      "00000000000000000001000000001111"
    )
  }

  it should "assemble ECALL Instruction" in {
    val i = RISCVAssembler.binOutput("ecall")
    i should be(
      "00000000000000000000000001110011"
    )
  }

  it should "assemble EBREAK Instruction" in {
    val i = RISCVAssembler.binOutput("ebreak")
    i should be(
      "00000000000100000000000001110011"
    )
  }

  behavior of "Pseudo-Instructions"

  it should "map NOP Pseudo Instruction" in {
    val i = PseudoInstructions(Array("nop")).get
    i should be(
      Array("addi", "x0", "x0", "0")
    )
  }

  it should "map MV Pseudo Instruction" in {
    val i = PseudoInstructions(Array("mv", "x1", "x2")).get
    i should be(
      Array("addi", "x1", "x2", "0")
    )
  }

  it should "map NOT Pseudo Instruction" in {
    val i = PseudoInstructions(Array("not", "x1", "x2")).get
    i should be(
      Array("xori", "x1", "x2", "-1")
    )
  }
  it should "map NEG Pseudo Instruction" in {
    val i = PseudoInstructions(Array("neg", "x1", "x2")).get
    i should be(
      Array("sub", "x1", "x0", "x2")
    )
  }
  it should "map SEQZ Pseudo Instruction" in {
    val i = PseudoInstructions(Array("seqz", "x1", "x2")).get
    i should be(
      Array("sltiu", "x1", "x2", "1")
    )
  }
  it should "map SNEZ Pseudo Instruction" in {
    val i = PseudoInstructions(Array("snez", "x1", "x2")).get
    i should be(
      Array("sltu", "x1", "x0", "x2")
    )
  }
  it should "map SLTZ Pseudo Instruction" in {
    val i = PseudoInstructions(Array("sltz", "x1", "x2")).get
    i should be(
      Array("slt", "x1", "x2", "x0")
    )
  }
  it should "map SGTZ Pseudo Instruction" in {
    val i = PseudoInstructions(Array("sgtz", "x1", "x2")).get
    i should be(
      Array("slt", "x1", "x0", "x2")
    )
  }
  it should "map BEQZ Pseudo Instruction" in {
    val i = PseudoInstructions(Array("beqz", "x1", "4")).get
    i should be(
      Array("beq", "x1", "x0", "4")
    )
  }
  it should "map BNEZ Pseudo Instruction" in {
    val i = PseudoInstructions(Array("bnez", "x1", "4")).get
    i should be(
      Array("bne", "x1", "x0", "4")
    )
  }
  it should "map BLEZ Pseudo Instruction" in {
    val i = PseudoInstructions(Array("blez", "x1", "4")).get
    i should be(
      Array("bge", "x0", "x1", "4")
    )
  }
  it should "map BGEZ Pseudo Instruction" in {
    val i = PseudoInstructions(Array("bgez", "x1", "4")).get
    i should be(
      Array("bge", "x1", "x0", "4")
    )
  }
  it should "map BLTZ Pseudo Instruction" in {
    val i = PseudoInstructions(Array("bltz", "x1", "4")).get
    i should be(
      Array("blt", "x1", "x0", "4")
    )
  }
  it should "map BGTZ Pseudo Instruction" in {
    val i = PseudoInstructions(Array("bgtz", "x1", "4")).get
    i should be(
      Array("blt", "x0", "x1", "4")
    )
  }
  it should "map BGT Pseudo Instruction" in {
    val i = PseudoInstructions(Array("bgt", "x1", "x2", "4")).get
    i should be(
      Array("blt", "x2", "x1", "4")
    )
  }
  it should "map BLE Pseudo Instruction" in {
    val i = PseudoInstructions(Array("ble", "x1", "x2", "4")).get
    i should be(
      Array("bge", "x2", "x1", "4")
    )
  }
  it should "map BGTU Pseudo Instruction" in {
    val i = PseudoInstructions(Array("bgtu", "x1", "x2", "4")).get
    i should be(
      Array("bltu", "x2", "x1", "4")
    )
  }
  it should "map BLEU Pseudo Instruction" in {
    val i = PseudoInstructions(Array("bleu", "x1", "x2", "4")).get
    i should be(
      Array("bgeu", "x2", "x1", "4")
    )
  }
  it should "map J Pseudo Instruction" in {
    val i = PseudoInstructions(Array("j", "4")).get
    i should be(
      Array("jal", "x0", "4")
    )
  }
  it should "map JR Pseudo Instruction" in {
    val i = PseudoInstructions(Array("jr", "x2")).get
    i should be(
      Array("jalr", "x0", "x2", "0")
    )
  }
  it should "map RET Pseudo Instruction" in {
    val i = PseudoInstructions(Array("ret")).get
    i should be(
      Array("jalr", "x0", "x1", "0")
    )
  }
  it should "map an invalid Pseudo Instruction to None" in {
    val i = PseudoInstructions(Array("bogus"))
    i should be(None)
  }
}
