package com.carlosedp.riscvassembler

object InstructionTypes extends Enumeration {
  type InstType = Value
  val I, R, B, S, U, J = Value
}

case class Instruction(
  name:      String,
  instType:  InstructionTypes.InstType,
  funct3:    String = "",
  funct7:    String = "",
  opcode:    String,
  hasOffset: Boolean = false,
  isCsr:     Boolean = false,
  fixed:     String = "",
  pseudo:    Boolean = false
)

protected object Instructions {
  def apply(instruction: String): Instruction =
    instruction.toUpperCase match {
      case "ADD" =>
        Instruction(
          name = "ADD",
          funct7 = "0000000",
          funct3 = "000",
          opcode = "0110011",
          instType = InstructionTypes.R
        )
      case "SUB" =>
        Instruction(
          name = "SUB",
          funct7 = "0100000",
          funct3 = "000",
          opcode = "0110011",
          instType = InstructionTypes.R
        )
      case "SLL" =>
        Instruction(
          name = "SLL",
          funct7 = "0000000",
          funct3 = "001",
          opcode = "0110011",
          instType = InstructionTypes.R
        )
      case "SRL" =>
        Instruction(
          name = "SRL",
          funct7 = "0000000",
          funct3 = "101",
          opcode = "0110011",
          instType = InstructionTypes.R
        )
      case "SRA" =>
        Instruction(
          name = "SRA",
          funct7 = "0100000",
          funct3 = "101",
          opcode = "0110011",
          instType = InstructionTypes.R
        )
      case "XOR" =>
        Instruction(
          name = "XOR",
          funct7 = "0000000",
          funct3 = "100",
          opcode = "0110011",
          instType = InstructionTypes.R
        )
      case "OR" =>
        Instruction(
          name = "OR",
          funct7 = "0000000",
          funct3 = "110",
          opcode = "0110011",
          instType = InstructionTypes.R
        )
      case "AND" =>
        Instruction(
          name = "AND",
          funct7 = "0000000",
          funct3 = "111",
          opcode = "0110011",
          instType = InstructionTypes.R
        )
      case "SLT" =>
        Instruction(
          name = "SLT",
          funct7 = "0000000",
          funct3 = "010",
          opcode = "0110011",
          instType = InstructionTypes.R
        )
      case "SLTU" =>
        Instruction(
          name = "SLTU",
          funct7 = "0000000",
          funct3 = "011",
          opcode = "0110011",
          instType = InstructionTypes.R
        )
      case "ADDI" =>
        Instruction(
          name = "ADDI",
          funct3 = "000",
          opcode = "0010011",
          instType = InstructionTypes.I
        )
      case "XORI" =>
        Instruction(
          name = "XORI",
          funct3 = "100",
          opcode = "0010011",
          instType = InstructionTypes.I
        )
      case "ORI" =>
        Instruction(
          name = "ORI",
          funct3 = "110",
          opcode = "0010011",
          instType = InstructionTypes.I
        )

      case "ANDI" =>
        Instruction(
          name = "ANDI",
          funct3 = "111",
          opcode = "0010011",
          instType = InstructionTypes.I
        )
      case "SLTI" =>
        Instruction(
          name = "SLTI",
          funct3 = "010",
          opcode = "0010011",
          instType = InstructionTypes.I
        )
      case "SLTIU" =>
        Instruction(
          name = "SLTIU",
          funct3 = "011",
          opcode = "0010011",
          instType = InstructionTypes.I
        )
      case "CSRRW" =>
        Instruction(
          name = "CSRRW",
          funct3 = "001",
          opcode = "1110011",
          isCsr = true,
          instType = InstructionTypes.I
        )
      case "CSRRS" =>
        Instruction(
          name = "CSRRS",
          funct3 = "010",
          opcode = "1110011",
          isCsr = true,
          instType = InstructionTypes.I
        )
      case "CSRRC" =>
        Instruction(
          name = "CSRRC",
          funct3 = "011",
          opcode = "1110011",
          isCsr = true,
          instType = InstructionTypes.I
        )
      case "CSRRWI" =>
        Instruction(
          name = "CSRRWI",
          funct3 = "101",
          opcode = "1110011",
          isCsr = true,
          instType = InstructionTypes.I
        )

      case "CSRRSI" =>
        Instruction(
          name = "CSRRSI",
          funct3 = "110",
          opcode = "1110011",
          isCsr = true,
          instType = InstructionTypes.I
        )

      case "CSRRCI" =>
        Instruction(
          name = "CSRRCI",
          funct3 = "111",
          opcode = "1110011",
          isCsr = true,
          instType = InstructionTypes.I
        )

      case "LB" =>
        Instruction(
          name = "LB",
          funct3 = "000",
          opcode = "0000011",
          hasOffset = true,
          instType = InstructionTypes.I
        )
      case "LH" =>
        Instruction(
          name = "LH",
          funct3 = "001",
          opcode = "0000011",
          hasOffset = true,
          instType = InstructionTypes.I
        )
      case "LBU" =>
        Instruction(
          name = "LBU",
          funct3 = "100",
          opcode = "0000011",
          hasOffset = true,
          instType = InstructionTypes.I
        )
      case "LHU" =>
        Instruction(
          name = "LHU",
          funct3 = "101",
          opcode = "0000011",
          hasOffset = true,
          instType = InstructionTypes.I
        )
      case "LW" =>
        Instruction(
          name = "LW",
          funct3 = "010",
          opcode = "0000011",
          hasOffset = true,
          instType = InstructionTypes.I
        )
      case "JALR" =>
        Instruction(
          name = "JALR",
          funct3 = "000",
          opcode = "1100111",
          instType = InstructionTypes.I
        )

      case "SLLI" =>
        Instruction(
          name = "SLLI",
          funct3 = "001",
          opcode = "0010011",
          fixed = "0000000",
          instType = InstructionTypes.I
        )
      case "SRLI" =>
        Instruction(
          name = "SRLI",
          funct3 = "101",
          opcode = "0010011",
          fixed = "0000000",
          instType = InstructionTypes.I
        )
      case "SRAI" =>
        Instruction(
          name = "SRAI",
          funct3 = "101",
          opcode = "0010011",
          fixed = "0100000",
          instType = InstructionTypes.I
        )
      // BitPat("b0000????????00000000000000001111")  -> List(INST_I,   FENCE
      // BitPat("b00000000000000000000001000001111")  -> List(INST_I,  FENCEI
      // BitPat("b00000000000000000000000001110011")  -> List(INST_I,   ECALL
      // BitPat("b00000000000100000000000001110011")  -> List(INST_I,  EBREAK
      case "LUI" =>
        Instruction(
          name = "LUI",
          opcode = "0110111",
          instType = InstructionTypes.U
        )
      case "AUIPC" =>
        Instruction(
          name = "AUIPC",
          funct3 = "001",
          opcode = "0010111",
          instType = InstructionTypes.U
        )
      case "BEQ" =>
        Instruction(
          name = "BEQ",
          funct3 = "000",
          opcode = "1100011",
          instType = InstructionTypes.B
        )
      case "BNE" =>
        Instruction(
          name = "BNE",
          funct3 = "001",
          opcode = "1100011",
          instType = InstructionTypes.B
        )
      case "BLT" =>
        Instruction(
          name = "BLT",
          funct3 = "100",
          opcode = "1100011",
          instType = InstructionTypes.B
        )
      case "BGE" =>
        Instruction(
          name = "BGE",
          funct3 = "101",
          opcode = "1100011",
          instType = InstructionTypes.B
        )
      case "BLTU" =>
        Instruction(
          name = "BLTU",
          funct3 = "110",
          opcode = "1100011",
          instType = InstructionTypes.B
        )
      case "BGEU" =>
        Instruction(
          name = "BGEU",
          funct3 = "111",
          opcode = "1100011",
          instType = InstructionTypes.B
        )
      case "JAL" =>
        Instruction(
          name = "JAL",
          opcode = "1101111",
          instType = InstructionTypes.J
        )
      case "SB" =>
        Instruction(
          name = "SB",
          funct3 = "000",
          opcode = "0100011",
          instType = InstructionTypes.S
        )
      case "SH" =>
        Instruction(
          name = "SH",
          funct3 = "001",
          opcode = "0100011",
          instType = InstructionTypes.S
        )
      case "SW" =>
        Instruction(
          name = "SW",
          funct3 = "010",
          opcode = "0100011",
          instType = InstructionTypes.S
        )
      // Pseudo-instructions mapping to the corresponding RISC-V instructions
      case "NOP" =>
        Instruction(
          name = "ADDI",
          funct3 = "000",
          opcode = "0010011",
          instType = InstructionTypes.I,
          pseudo = true
        )
      case "BEQZ" =>
        Instruction(
          name = "BEQ",
          funct3 = "000",
          opcode = "1100011",
          instType = InstructionTypes.B,
          pseudo = true
        )
      case "BGEZ" =>
        Instruction(
          name = "BGE",
          funct3 = "101",
          opcode = "1100011",
          instType = InstructionTypes.B,
          pseudo = true
        )
    }
}

protected object PseudoInstructions {
  def apply(instructionData: Array[String]): Array[String] =
    instructionData(0).toUpperCase match {
      // Map received params to the corresponding RISC-V instruction
      case "NOP"  => { Array("addi", "x0", "x0", "0") }
      case "BEQZ" => { Array("beq", instructionData(1), "x0", instructionData(2)) }
      case "BGEZ" => { Array("bge", instructionData(1), "x0", instructionData(2)) }
    }
}
