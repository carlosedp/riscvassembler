import com.carlosedp.scalautils.riscvassembler._
import org.scalatest._

import flatspec._
import matchers.should._

class InstructionsSpec extends AnyFlatSpec with Matchers {
  behavior of "Instructions"

  it should "fetch ADD instruction" in {
    val i = Instructions("ADD")
    i should be(
      Map(
        "inst_name" -> "ADD",
        "funct7"    -> "0000000",
        "funct3"    -> "000",
        "opcode"    -> "0110011",
        "inst_type" -> "INST_R"
      )
    )
  }

  it should "fetch SLL instruction" in {
    val i = Instructions("SLL")
    i should be(
      Map(
        "inst_name" -> "SLL",
        "funct7"    -> "0000000",
        "funct3"    -> "001",
        "opcode"    -> "0110011",
        "inst_type" -> "INST_R"
      )
    )
  }

  it should "fetch ADDI instruction" in {
    val i = Instructions("ADDI")
    i should be(
      Map("inst_name" -> "ADDI", "funct3" -> "000", "opcode" -> "0010011", "inst_type" -> "INST_I")
    )
  }

  it should "fetch LUI instruction" in {
    val i = Instructions("LUI")
    i should be(
      Map("inst_name" -> "LUI", "opcode" -> "0110111", "inst_type" -> "INST_U")
    )
  }

  it should "fetch NOP instruction" in {
    val i = Instructions("NOP")
    i should be(
      Map(
        "pseudo_inst" -> "true",
        "inst_name"   -> "ADDI",
        "funct3"      -> "000",
        "opcode"      -> "0010011",
        "inst_type"   -> "INST_I"
      )
    )
  }

  it should "fetch BEQZ instruction" in {
    val i = Instructions("BEQZ")
    i should be(
      Map(
        "pseudo_inst" -> "true",
        "inst_name"   -> "BEQ",
        "funct3"      -> "000",
        "opcode"      -> "1100011",
        "inst_type"   -> "INST_B"
      )
    )
  }

  behavior of "Pseudo-Instructions"
  it should "map NOP instruction" in {
    val i = PseudoInstructions(Array("nop"))
    i should be(
      Array("addi", "x0", "x0", "0")
    )
  }
}
