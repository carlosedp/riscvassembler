package com.carlosedp.riscvassembler

object InstType extends Enumeration {
  type Type = Value
  val I, R, B, S, U, J = Value
}

case class Instruction(
  name:      String,
  realName:  String = "",
  instType:  InstType.Type,
  funct3:    String = "",
  funct7:    String = "",
  opcode:    String,
  hasOffset: Boolean = false,
  isCsr:     Boolean = false,
  hasImm:    Boolean = false,
  isFence:   Boolean = false,
  fixed:     String = "",
)

protected object Instructions {
  def apply(instruction: String): Option[Instruction] =
    instructions.find(_.name == instruction.toUpperCase)

  // scalafmt: { maxColumn = 130}
  private val instructions = List(
    Instruction(name = "LUI", instType   = InstType.U, opcode = "0110111"),
    Instruction(name = "AUIPC", instType = InstType.U, opcode = "0010111", funct3 = "001"),
    Instruction(name = "JAL", instType   = InstType.J, opcode = "1101111"),
    Instruction(name = "JALR", instType  = InstType.I, opcode = "1100111", funct3 = "000"),
    Instruction(name = "BEQ", instType   = InstType.B, opcode = "1100011", funct3 = "000"),
    Instruction(name = "BNE", instType   = InstType.B, opcode = "1100011", funct3 = "001"),
    Instruction(name = "BLT", instType   = InstType.B, opcode = "1100011", funct3 = "100"),
    Instruction(name = "BGE", instType   = InstType.B, opcode = "1100011", funct3 = "101"),
    Instruction(name = "BLTU", instType  = InstType.B, opcode = "1100011", funct3 = "110"),
    Instruction(name = "BGEU", instType  = InstType.B, opcode = "1100011", funct3 = "111"),
    Instruction(name = "LB", instType    = InstType.I, opcode = "0000011", funct3 = "000", hasOffset  = true),
    Instruction(name = "LH", instType    = InstType.I, opcode = "0000011", funct3 = "001", hasOffset  = true),
    Instruction(name = "LW", instType    = InstType.I, opcode = "0000011", funct3 = "010", hasOffset  = true),
    Instruction(name = "LBU", instType   = InstType.I, opcode = "0000011", funct3 = "100", hasOffset  = true),
    Instruction(name = "LHU", instType   = InstType.I, opcode = "0000011", funct3 = "101", hasOffset  = true),
    Instruction(name = "SB", instType    = InstType.S, opcode = "0100011", funct3 = "000"),
    Instruction(name = "SH", instType    = InstType.S, opcode = "0100011", funct3 = "001"),
    Instruction(name = "SW", instType    = InstType.S, opcode = "0100011", funct3 = "010"),
    Instruction(name = "ADDI", instType  = InstType.I, opcode = "0010011", funct3 = "000"),
    Instruction(name = "SLTI", instType  = InstType.I, opcode = "0010011", funct3 = "010"),
    Instruction(name = "SLTIU", instType = InstType.I, opcode = "0010011", funct3 = "011"),
    Instruction(name = "XORI", instType  = InstType.I, opcode = "0010011", funct3 = "100"),
    Instruction(name = "ORI", instType   = InstType.I, opcode = "0010011", funct3 = "110"),
    Instruction(name = "ANDI", instType  = InstType.I, opcode = "0010011", funct3 = "111"),
    Instruction(name = "SLLI", instType  = InstType.I, opcode = "0010011", funct3 = "001", fixed      = "0000000"),
    Instruction(name = "SRLI", instType  = InstType.I, opcode = "0010011", funct3 = "101", fixed      = "0000000"),
    Instruction(name = "SRAI", instType  = InstType.I, opcode = "0010011", funct3 = "101", fixed      = "0100000"),
    Instruction(name = "ADD", instType   = InstType.R, opcode = "0110011", funct7 = "0000000", funct3 = "000"),
    Instruction(name = "SUB", instType   = InstType.R, opcode = "0110011", funct7 = "0100000", funct3 = "000"),
    Instruction(name = "SLL", instType   = InstType.R, opcode = "0110011", funct7 = "0000000", funct3 = "001"),
    Instruction(name = "SLT", instType   = InstType.R, opcode = "0110011", funct7 = "0000000", funct3 = "010"),
    Instruction(name = "SLTU", instType  = InstType.R, opcode = "0110011", funct7 = "0000000", funct3 = "011"),
    Instruction(name = "XOR", instType   = InstType.R, opcode = "0110011", funct7 = "0000000", funct3 = "100"),
    Instruction(name = "SRL", instType   = InstType.R, opcode = "0110011", funct7 = "0000000", funct3 = "101"),
    Instruction(name = "SRA", instType   = InstType.R, opcode = "0110011", funct7 = "0100000", funct3 = "101"),
    Instruction(name = "OR", instType    = InstType.R, opcode = "0110011", funct7 = "0000000", funct3 = "110"),
    Instruction(name = "AND", instType   = InstType.R, opcode = "0110011", funct7 = "0000000", funct3 = "111"),
    // Instructions below are still not implemented
    Instruction(name = "FENCE", instType   = InstType.I, opcode = "0001111", funct3 = "000", isFence = true),
    Instruction(name = "FENCE.I", instType = InstType.I, opcode = "0001111", funct3 = "001", isFence = true),
    Instruction(name = "ECALL", instType   = InstType.I, opcode = "1110011", funct3 = "000", fixed   = "000000000000"),
    Instruction(name = "EBREAK", instType  = InstType.I, opcode = "1110011", funct3 = "000", fixed   = "000000000001"),
    Instruction(name = "CSRRW", instType   = InstType.I, opcode = "1110011", funct3 = "001", isCsr   = true, hasImm = false),
    Instruction(name = "CSRRS", instType   = InstType.I, opcode = "1110011", funct3 = "010", isCsr   = true, hasImm = false),
    Instruction(name = "CSRRC", instType   = InstType.I, opcode = "1110011", funct3 = "011", isCsr   = true, hasImm = false),
    Instruction(name = "CSRRWI", instType  = InstType.I, opcode = "1110011", funct3 = "101", isCsr   = true, hasImm = true),
    Instruction(name = "CSRRSI", instType  = InstType.I, opcode = "1110011", funct3 = "110", isCsr   = true, hasImm = true),
    Instruction(name = "CSRRCI", instType  = InstType.I, opcode = "1110011", funct3 = "111", isCsr   = true, hasImm = true),
  )
}

/**
 * This function transforms a pseudo instruction to it's real conterpart
 */
protected object PseudoInstructions {
  def apply(instructionData: Array[String]): Option[Array[String]] =
    instructionData(0).toUpperCase match {
      // Map received params to the corresponding RISC-V instruction
      case "NOP"  => { Some(Array("addi", "x0", "x0", "0")) }
      case "MV"   => { Some(Array("addi", instructionData(1), instructionData(2), "0")) }
      case "NOT"  => { Some(Array("xori", instructionData(1), instructionData(2), "-1")) }
      case "NEG"  => { Some(Array("sub", instructionData(1), "x0", instructionData(2))) }
      case "SEQZ" => { Some(Array("sltiu", instructionData(1), instructionData(2), "1")) }
      case "SNEZ" => { Some(Array("sltu", instructionData(1), "x0", instructionData(2))) }
      case "SLTZ" => { Some(Array("slt", instructionData(1), instructionData(2), "x0")) }
      case "SGTZ" => { Some(Array("slt", instructionData(1), "x0", instructionData(2))) }
      case "BEQZ" => { Some(Array("beq", instructionData(1), "x0", instructionData(2))) }
      case "BNEZ" => { Some(Array("bne", instructionData(1), "x0", instructionData(2))) }
      case "BLEZ" => { Some(Array("bge", "x0", instructionData(1), instructionData(2))) }
      case "BGEZ" => { Some(Array("bge", instructionData(1), "x0", instructionData(2))) }
      case "BLTZ" => { Some(Array("blt", instructionData(1), "x0", instructionData(2))) }
      case "BGTZ" => { Some(Array("blt", "x0", instructionData(1), instructionData(2))) }
      case "BGT"  => { Some(Array("blt", instructionData(2), instructionData(1), instructionData(3))) }
      case "BLE"  => { Some(Array("bge", instructionData(2), instructionData(1), instructionData(3))) }
      case "BGTU" => { Some(Array("bltu", instructionData(2), instructionData(1), instructionData(3))) }
      case "BLEU" => { Some(Array("bgeu", instructionData(2), instructionData(1), instructionData(3))) }
      case "J"    => { Some(Array("jal", "x0", instructionData(1))) }
      case "JR"   => { Some(Array("jalr", "x0", instructionData(1), "0")) }
      case "RET"  => { Some(Array("jalr", "x0", "x1", "0")) }
      case _      => None
    }
}
