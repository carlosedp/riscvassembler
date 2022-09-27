package com.carlosedp.riscvassembler

import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class RISCVAssemblerInternalSpec extends AnyFlatSpec with Matchers {
  behavior of "InstructionFiller"

  it should "fill R-type instruction" in {
    val i = Instruction(
      name = "ADD",
      funct7 = "0000000",
      funct3 = "000",
      opcode = "0110011",
      instType = InstructionTypes.R,
    )
    val opdata = Map("rd" -> 1.toLong, "rs1" -> 2.toLong, "rs2" -> 3.toLong)
    val output = FillInstruction(i, opdata)
    output should be("00000000001100010000000010110011")
  }

  it should "fill I-type instruction" in {
    val i = Instruction(
      name = "ADDI",
      funct3 = "000",
      opcode = "0010011",
      instType = InstructionTypes.I,
    )
    val opdata = Map("rd" -> 1.toLong, "rs1" -> 2.toLong, "imm" -> 4095.toLong)
    val output = FillInstruction(i, opdata)
    output should be("11111111111100010000000010010011")
  }

  it should "fill B-type instruction" in {
    val i = Instruction(
      name = "BEQ",
      funct3 = "000",
      opcode = "1100011",
      instType = InstructionTypes.B,
    )
    val opdata = Map("rs1" -> 1L, "rs2" -> 2L, "imm" -> 4094L)
    val output = FillInstruction(i, opdata)
    output should be("01111110001000001000111111100011")
  }

  it should "fill S-type instruction" in {
    val i = Instruction(
      name = "SB",
      funct3 = "000",
      opcode = "0100011",
      instType = InstructionTypes.S,
    )
    val opdata = Map("rs1" -> 2L, "rs2" -> 3L, "imm" -> 1024L)
    val output = FillInstruction(i, opdata)
    output should be("01000000001100010000000000100011")
  }

  it should "fill U-type instruction" in {
    val i = Instruction(
      name = "LUI",
      opcode = "0110111",
      instType = InstructionTypes.U,
    )
    val opdata = Map("rd" -> 2L, "imm" -> 0xc0000000L)
    val output = FillInstruction(i, opdata)
    output should be("11000000000000000000000100110111")
  }

  it should "fill J-type instruction" in {
    val i = Instruction(
      name = "JAL",
      opcode = "1101111",
      instType = InstructionTypes.J,
    )
    val opdata = Map("rd" -> 1L, "imm" -> 2048L)
    val output = FillInstruction(i, opdata)
    output should be("00000000000100000000000011101111")
  }

  // ------------------------------------------------------------
  behavior of "GenHex"
  it should "convert binary instruction to hex" in {
    val output = GenHex("11111111111111111111000001101111")
    output should be("FFFFF06F")
  }

  // ------------------------------------------------------------

  behavior of "InstructionParser"

  it should "parse R-type instruction" in {
    val (inst, instData) = InstructionParser("add x1, x2, x3").get
    val d = Instruction(
      name = "ADD",
      funct7 = "0000000",
      funct3 = "000",
      opcode = "0110011",
      instType = InstructionTypes.R,
    )
    inst should be(d)
    instData should be(Map("rd" -> 1, "rs1" -> 2, "rs2" -> 3))
  }

  it should "parse I-type instruction" in {
    val (inst, instData) = InstructionParser("addi x1, x2, 1024").get
    val d = Instruction(
      name = "ADDI",
      funct3 = "000",
      opcode = "0010011",
      instType = InstructionTypes.I,
    )
    inst should be(d)
    instData should be(Map("rd" -> 1, "rs1" -> 2, "imm" -> 1024))
  }

  it should "parse I-type instruction with imm in hex" in {
    val (inst, instData) = InstructionParser("addi x1, x2, 0x400").get
    val d = Instruction(
      name = "ADDI",
      funct3 = "000",
      opcode = "0010011",
      instType = InstructionTypes.I,
    )
    inst should be(d)
    instData should be(Map("rd" -> 1, "rs1" -> 2, "imm" -> 1024))
  }

  it should "parse I-type instruction with offset in hex" in {
    val (inst, instData) = InstructionParser("lb x1, 0x400(x2)").get
    val d = Instruction(
      name = "LB",
      funct3 = "000",
      opcode = "0000011",
      hasOffset = true,
      instType = InstructionTypes.I,
    )
    inst should be(d)
    instData should be(Map("rd" -> 1, "rs1" -> 2, "imm" -> 1024))
  }

  it should "parse S-type instruction" in {
    val (inst, instData) = InstructionParser("sb x3, 1024(x2)").get
    val d = Instruction(
      name = "SB",
      funct3 = "000",
      opcode = "0100011",
      instType = InstructionTypes.S,
    )
    inst should be(d)
    instData should be(Map("rs1" -> 2, "rs2" -> 3, "imm" -> 1024))
  }

  it should "parse S-type instruction with offset in hex" in {
    val (inst, instData) = InstructionParser("sb x3, 0x400(x2)").get
    val d = Instruction(
      name = "SB",
      funct3 = "000",
      opcode = "0100011",
      instType = InstructionTypes.S,
    )
    inst should be(d)
    instData should be(Map("rs1" -> 2, "rs2" -> 3, "imm" -> 1024))
  }

  it should "parse B-type instruction" in {
    val (inst, instData) = InstructionParser("beq x3, x0, +16").get
    val d = Instruction(
      name = "BEQ",
      funct3 = "000",
      opcode = "1100011",
      instType = InstructionTypes.B,
    )
    inst should be(d)
    instData should be(Map("rs1" -> 3, "rs2" -> 0, "imm" -> 16))
  }

  it should "parse B-type instruction with offset in hex" in {
    val (inst, instData) = InstructionParser("beq x3, x0, 0x10").get
    val d = Instruction(
      name = "BEQ",
      funct3 = "000",
      opcode = "1100011",
      instType = InstructionTypes.B,
    )
    inst should be(d)
    instData should be(Map("rs1" -> 3, "rs2" -> 0, "imm" -> 16))
  }

  it should "parse U-type instruction with hex input" in {
    val (inst, instData) = InstructionParser("lui x2, 0xc0000000").get
    val d = Instruction(
      name = "LUI",
      opcode = "0110111",
      instType = InstructionTypes.U,
    )
    inst should be(d)
    instData should be(Map("rd" -> 2, "imm" -> 0xc0000000L))
  }

  it should "parse U-type instruction with dec input" in {
    val (inst, instData) = InstructionParser("lui x2, 32").get
    val d = Instruction(
      name = "LUI",
      opcode = "0110111",
      instType = InstructionTypes.U,
    )
    inst should be(d)
    instData should be(Map("rd" -> 2, "imm" -> 32))
  }

  it should "parse J-type instruction" in {
    val (inst, instData) = InstructionParser("jal x0, -16").get
    val d = Instruction(
      name = "JAL",
      opcode = "1101111",
      instType = InstructionTypes.J,
    )
    inst should be(d)
    instData should be(Map("rd" -> 0, "imm" -> -16))
  }

  // ------------------------------------------------------------

  behavior of "RegMap"
  it should "map registers using name" in {
    for (i <- 0 to 31) {
      val output = RegMap("x" + i.toString)
      output should be(i)
    }
  }

  it should "map registers using ABI name" in {
    val output = RegMap("a0")
    output should be(10)
  }

  it should "map registers using secondary ABI name" in {
    val output = RegMap("fp")
    output should be(8)
  }
}
