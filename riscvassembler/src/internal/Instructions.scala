package com.carlosedp.riscvassembler

object InstructionTypes extends Enumeration {
  type InstType = Value
  val I, R, B, S, U, J = Value
}

case class Instruction(
  name:      String,
  realName:  String = "",
  instType:  InstructionTypes.InstType,
  funct3:    String = "",
  funct7:    String = "",
  opcode:    String,
  hasOffset: Boolean = false,
  isCsr:     Boolean = false,
  fixed:     String = "",
  pseudo:    Boolean = false,
)

protected object Instructions {
  def apply(instruction: String): Option[Instruction] =
    instructions.find(_.name == instruction.toUpperCase)

  private val instructions = List(
    Instruction(name = "ADD", funct7 = "0000000", funct3 = "000", opcode = "0110011", instType = InstructionTypes.R),
    Instruction(name = "SUB", funct7 = "0100000", funct3 = "000", opcode = "0110011", instType = InstructionTypes.R),
    Instruction(name = "SLL", funct7 = "0000000", funct3 = "001", opcode = "0110011", instType = InstructionTypes.R),
    Instruction(name = "SRL", funct7 = "0000000", funct3 = "101", opcode = "0110011", instType = InstructionTypes.R),
    Instruction(name = "SRA", funct7 = "0100000", funct3 = "101", opcode = "0110011", instType = InstructionTypes.R),
    Instruction(name = "XOR", funct7 = "0000000", funct3 = "100", opcode = "0110011", instType = InstructionTypes.R),
    Instruction(name = "OR", funct7 = "0000000", funct3 = "110", opcode = "0110011", instType = InstructionTypes.R),
    Instruction(name = "AND", funct7 = "0000000", funct3 = "111", opcode = "0110011", instType = InstructionTypes.R),
    Instruction(name = "SLT", funct7 = "0000000", funct3 = "010", opcode = "0110011", instType = InstructionTypes.R),
    Instruction(name = "SLTU", funct7 = "0000000", funct3 = "011", opcode = "0110011", instType = InstructionTypes.R),
    Instruction(name = "ADDI", funct3 = "000", opcode = "0010011", instType = InstructionTypes.I),
    Instruction(name = "XORI", funct3 = "100", opcode = "0010011", instType = InstructionTypes.I),
    Instruction(name = "ORI", funct3 = "110", opcode = "0010011", instType = InstructionTypes.I),
    Instruction(name = "ANDI", funct3 = "111", opcode = "0010011", instType = InstructionTypes.I),
    Instruction(name = "SLTI", funct3 = "010", opcode = "0010011", instType = InstructionTypes.I),
    Instruction(name = "SLTIU", funct3 = "011", opcode = "0010011", instType = InstructionTypes.I),
    Instruction(name = "CSRRW", funct3 = "001", opcode = "1110011", isCsr = true, instType = InstructionTypes.I),
    Instruction(name = "CSRRS", funct3 = "010", opcode = "1110011", isCsr = true, instType = InstructionTypes.I),
    Instruction(name = "CSRRC", funct3 = "011", opcode = "1110011", isCsr = true, instType = InstructionTypes.I),
    Instruction(name = "CSRRWI", funct3 = "101", opcode = "1110011", isCsr = true, instType = InstructionTypes.I),
    Instruction(name = "CSRRSI", funct3 = "110", opcode = "1110011", isCsr = true, instType = InstructionTypes.I),
    Instruction(name = "CSRRCI", funct3 = "111", opcode = "1110011", isCsr = true, instType = InstructionTypes.I),
    Instruction(name = "LB", funct3 = "000", opcode = "0000011", hasOffset = true, instType = InstructionTypes.I),
    Instruction(name = "LH", funct3 = "001", opcode = "0000011", hasOffset = true, instType = InstructionTypes.I),
    Instruction(name = "LBU", funct3 = "100", opcode = "0000011", hasOffset = true, instType = InstructionTypes.I),
    Instruction(name = "LHU", funct3 = "101", opcode = "0000011", hasOffset = true, instType = InstructionTypes.I),
    Instruction(name = "LW", funct3 = "010", opcode = "0000011", hasOffset = true, instType = InstructionTypes.I),
    Instruction(name = "JALR", funct3 = "000", opcode = "1100111", instType = InstructionTypes.I),
    Instruction(name = "SLLI", funct3 = "001", opcode = "0010011", fixed = "0000000", instType = InstructionTypes.I),
    Instruction(name = "SRLI", funct3 = "101", opcode = "0010011", fixed = "0000000", instType = InstructionTypes.I),
    Instruction(name = "SRAI", funct3 = "101", opcode = "0010011", fixed = "0100000", instType = InstructionTypes.I),
    // BitPat("b0000????????00000000000000001111")  -> List(INST_I,   FENCE
    // BitPat("b00000000000000000000001000001111")  -> List(INST_I,  FENCEI
    // BitPat("b00000000000000000000000001110011")  -> List(INST_I,   ECALL
    // BitPat("b00000000000100000000000001110011")  -> List(INST_I,  EBREAK
    Instruction(name = "LUI", opcode = "0110111", instType = InstructionTypes.U),
    Instruction(name = "AUIPC", funct3 = "001", opcode = "0010111", instType = InstructionTypes.U),
    Instruction(name = "BEQ", funct3 = "000", opcode = "1100011", instType = InstructionTypes.B),
    Instruction(name = "BNE", funct3 = "001", opcode = "1100011", instType = InstructionTypes.B),
    Instruction(name = "BLT", funct3 = "100", opcode = "1100011", instType = InstructionTypes.B),
    Instruction(name = "BGE", funct3 = "101", opcode = "1100011", instType = InstructionTypes.B),
    Instruction(name = "BLTU", funct3 = "110", opcode = "1100011", instType = InstructionTypes.B),
    Instruction(name = "BGEU", funct3 = "111", opcode = "1100011", instType = InstructionTypes.B),
    Instruction(name = "JAL", opcode = "1101111", instType = InstructionTypes.J),
    Instruction(name = "SB", funct3 = "000", opcode = "0100011", instType = InstructionTypes.S),
    Instruction(name = "SH", funct3 = "001", opcode = "0100011", instType = InstructionTypes.S),
    Instruction(name = "SW", funct3 = "010", opcode = "0100011", instType = InstructionTypes.S),
    // Pseudo-instructions mapping to the corresponding RISC-V instructions
    Instruction(
      name = "NOP",
      realName = "ADDI",
      funct3 = "000",
      opcode = "0010011",
      instType = InstructionTypes.I,
      pseudo = true,
    ),
    Instruction(
      name = "MV",
      realName = "ADDI",
      funct3 = "000",
      opcode = "0010011",
      instType = InstructionTypes.I,
      pseudo = true,
    ),
    Instruction(
      name = "BNEZ",
      realName = "BNE",
      funct3 = "001",
      opcode = "1100011",
      instType = InstructionTypes.B,
      pseudo = true,
    ),
    Instruction(
      name = "BEQZ",
      realName = "BEQ",
      funct3 = "000",
      opcode = "1100011",
      instType = InstructionTypes.B,
      pseudo = true,
    ),
    Instruction(
      name = "BGEZ",
      realName = "BGE",
      funct3 = "101",
      opcode = "1100011",
      instType = InstructionTypes.B,
      pseudo = true,
    ),
    Instruction(
      name = "BLEZ",
      realName = "BGE",
      funct3 = "101",
      opcode = "1100011",
      instType = InstructionTypes.B,
      pseudo = true,
    ),
    Instruction(
      name = "BLTZ",
      realName = "BLT",
      funct3 = "100",
      opcode = "1100011",
      instType = InstructionTypes.B,
      pseudo = true,
    ),
    Instruction(
      name = "BGTZ",
      realName = "BLT",
      funct3 = "100",
      opcode = "1100011",
      instType = InstructionTypes.B,
      pseudo = true,
    ),
    Instruction(
      name = "BGT",
      realName = "BLT",
      funct3 = "100",
      opcode = "1100011",
      instType = InstructionTypes.B,
      pseudo = true,
    ),
    Instruction(
      name = "BLE",
      realName = "BGE",
      funct3 = "101",
      opcode = "1100011",
      instType = InstructionTypes.B,
      pseudo = true,
    ),
    Instruction(
      name = "BGTU",
      realName = "BLTU",
      funct3 = "110",
      opcode = "1100011",
      instType = InstructionTypes.B,
      pseudo = true,
    ),
    Instruction(
      name = "BLEU",
      realName = "BGEU",
      funct3 = "111",
      opcode = "1100011",
      instType = InstructionTypes.B,
      pseudo = true,
    ),
    Instruction(name = "J", realName = "JAL", opcode = "1101111", instType = InstructionTypes.J, pseudo = true),
    Instruction(
      name = "RET",
      realName = "JALR",
      funct3 = "000",
      opcode = "1100111",
      instType = InstructionTypes.I,
      pseudo = true,
    ),
  )
}

protected object PseudoInstructions {
  def apply(instructionData: Array[String]): Option[Array[String]] =
    instructionData(0).toUpperCase match {
      // Map received params to the corresponding RISC-V instruction
      case "NOP"  => { Some(Array("addi", "x0", "x0", "0")) }
      case "MV"   => { Some(Array("addi", instructionData(1), instructionData(2), "0")) }
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
      case "RET"  => { Some(Array("jalr", "x0", "x1", "0")) }
      case _      => None

    }
}
