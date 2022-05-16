import com.carlosedp.scalautils.riscvassembler.internal._
import org.scalatest._

import flatspec._
import matchers.should._

class RISCVAssemblerInternalSpec extends AnyFlatSpec with Matchers {
  behavior of "InstructionFiller"

  it should "fill R-type instruction" in {
    val op     = Map("funct7" -> "0000000", "funct3" -> "000", "opcode" -> "0110011", "inst_type" -> "INST_R")
    val opdata = Map("rd" -> 1.toLong, "rs1" -> 2.toLong, "rs2" -> 3.toLong)
    val output = FillInstruction(op("inst_type"), opdata, op)
    output should be("00000000001100010000000010110011")
  }

  it should "fill I-type instruction" in {
    val op     = Map("funct3" -> "000", "opcode" -> "0010011", "inst_type" -> "INST_I")
    val opdata = Map("rd" -> 1.toLong, "rs1" -> 2.toLong, "imm" -> 4095.toLong)
    val output = FillInstruction(op("inst_type"), opdata, op)
    output should be("11111111111100010000000010010011")
  }

  it should "fill B-type instruction" in {
    val op     = Map("funct3" -> "000", "opcode" -> "1100011", "inst_type" -> "INST_B")
    val opdata = Map("rs1" -> 1L, "rs2" -> 2L, "imm" -> 4094L)
    val output = FillInstruction(op("inst_type"), opdata, op)
    output should be("01111110001000001000111111100011")
  }

  it should "fill S-type instruction" in {
    val op     = Map("funct3" -> "000", "opcode" -> "0100011", "inst_type" -> "INST_S")
    val opdata = Map("rs1" -> 2L, "rs2" -> 3L, "imm" -> 1024L)
    val output = FillInstruction(op("inst_type"), opdata, op)
    output should be("01000000001100010000000000100011")
  }

  it should "fill U-type instruction" in {
    val op     = Map("opcode" -> "0110111", "inst_type" -> "INST_U")
    val opdata = Map("rd" -> 2L, "imm" -> 0xc0000000L)
    val output = FillInstruction(op("inst_type"), opdata, op)
    output should be("11000000000000000000000100110111")
  }

  it should "fill J-type instruction" in {
    val op     = Map("opcode" -> "1101111", "inst_type" -> "INST_J")
    val opdata = Map("rd" -> 1L, "imm" -> 2048L)
    val output = FillInstruction(op("inst_type"), opdata, op)
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
    val output = InstructionParser("add x1, x2, x3")
    val d = Map(
      "inst_name" -> "ADD",
      "funct7"    -> "0000000",
      "funct3"    -> "000",
      "opcode"    -> "0110011",
      "inst_type" -> "INST_R"
    )
    output should be((d, Map("rd" -> 1, "rs1" -> 2, "rs2" -> 3)))
  }

  it should "parse I-type instruction" in {
    val output = InstructionParser("addi x1, x2, 1024")
    val d      = Map("inst_name" -> "ADDI", "funct3" -> "000", "opcode" -> "0010011", "inst_type" -> "INST_I")
    output should be((d, Map("rd" -> 1, "rs1" -> 2, "imm" -> 1024)))
  }

  it should "parse S-type instruction" in {
    val output = InstructionParser("sb x3, 1024(x2)")
    val d      = Map("inst_name" -> "SB", "funct3" -> "000", "opcode" -> "0100011", "inst_type" -> "INST_S")
    output should be((d, Map("rs1" -> 2, "rs2" -> 3, "imm" -> 1024)))
  }

  it should "parse B-type instruction" in {
    val output = InstructionParser("beq x3, x0, +16")
    val d      = Map("inst_name" -> "BEQ", "funct3" -> "000", "opcode" -> "1100011", "inst_type" -> "INST_B")
    output should be((d, Map("rs1" -> 3, "rs2" -> 0, "imm" -> 16)))
  }

  it should "parse U-type instruction with hex input" in {
    val output = InstructionParser("lui x2, 0xc0000000")
    val d      = Map("inst_name" -> "LUI", "opcode" -> "0110111", "inst_type" -> "INST_U")
    output should be((d, Map("rd" -> 2, "imm" -> 0xc0000000L)))
  }

  it should "parse U-type instruction with dec input" in {
    val output = InstructionParser("lui x2, 32")
    val d      = Map("inst_name" -> "LUI", "opcode" -> "0110111", "inst_type" -> "INST_U")
    output should be((d, Map("rd" -> 2, "imm" -> 32)))
  }

  it should "parse J-type instruction" in {
    val output = InstructionParser("jal x0, -16")
    val d      = Map("inst_name" -> "JAL", "opcode" -> "1101111", "inst_type" -> "INST_J")
    output should be((d, Map("rd" -> 0, "imm" -> -16)))
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
