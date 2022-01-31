package com.carlosedp.scalautils.riscvassembler.internal

object Instructions {
  def apply(instruction: String): Map[String, String] =
    instruction.toUpperCase match {
      case "ADD" =>
        Map(
          "inst_name" -> "ADD",
          "funct7"    -> "0000000",
          "funct3"    -> "000",
          "opcode"    -> "0110011",
          "inst_type" -> "INST_R"
        )
      case "SUB" =>
        Map(
          "inst_name" -> "SUB",
          "funct7"    -> "0100000",
          "funct3"    -> "000",
          "opcode"    -> "0110011",
          "inst_type" -> "INST_R"
        )
      case "SLL" =>
        Map(
          "inst_name" -> "SLL",
          "funct7"    -> "0000000",
          "funct3"    -> "001",
          "opcode"    -> "0110011",
          "inst_type" -> "INST_R"
        )
      case "SRL" =>
        Map(
          "inst_name" -> "SRL",
          "funct7"    -> "0100000",
          "funct3"    -> "101",
          "opcode"    -> "0110011",
          "inst_type" -> "INST_R"
        )
      case "SRA" =>
        Map(
          "inst_name" -> "SRA",
          "funct7"    -> "0100000",
          "funct3"    -> "101",
          "opcode"    -> "0110011",
          "inst_type" -> "INST_R"
        )
      case "XOR" =>
        Map(
          "inst_name" -> "XOR",
          "funct7"    -> "0000000",
          "funct3"    -> "100",
          "opcode"    -> "0110011",
          "inst_type" -> "INST_R"
        )
      case "OR" =>
        Map(
          "inst_name" -> "OR",
          "funct7"    -> "0000000",
          "funct3"    -> "110",
          "opcode"    -> "0110011",
          "inst_type" -> "INST_R"
        )
      case "AND" =>
        Map(
          "inst_name" -> "AND",
          "funct7"    -> "0000000",
          "funct3"    -> "111",
          "opcode"    -> "0110011",
          "inst_type" -> "INST_R"
        )
      case "SLT" =>
        Map(
          "inst_name" -> "SLT",
          "funct7"    -> "0000000",
          "funct3"    -> "010",
          "opcode"    -> "0110011",
          "inst_type" -> "INST_R"
        )
      case "SLTU" =>
        Map(
          "inst_name" -> "SLTU",
          "funct7"    -> "0000000",
          "funct3"    -> "011",
          "opcode"    -> "0110011",
          "inst_type" -> "INST_R"
        )
      case "ADDI"  => Map("inst_name" -> "ADDI", "funct3" -> "000", "opcode" -> "0010011", "inst_type" -> "INST_I")
      case "XORI"  => Map("inst_name" -> "XORI", "funct3" -> "100", "opcode" -> "0010011", "inst_type" -> "INST_I")
      case "ORI"   => Map("inst_name" -> "ORI", "funct3" -> "110", "opcode" -> "0010011", "inst_type" -> "INST_I")
      case "ANDI"  => Map("inst_name" -> "ANDI", "funct3" -> "111", "opcode" -> "0010011", "inst_type" -> "INST_I")
      case "SLTI"  => Map("inst_name" -> "SLTI", "funct3" -> "010", "opcode" -> "0010011", "inst_type" -> "INST_I")
      case "SLTIU" => Map("inst_name" -> "SLTIU", "funct3" -> "011", "opcode" -> "0010011", "inst_type" -> "INST_I")
      case "CSRRW" =>
        Map(
          "inst_name" -> "CSRRW",
          "is_csr"    -> "true",
          "funct3"    -> "001",
          "opcode"    -> "1110011",
          "inst_type" -> "INST_I"
        )
      case "CSRRS" =>
        Map(
          "inst_name" -> "CSRRS",
          "is_csr"    -> "true",
          "funct3"    -> "010",
          "opcode"    -> "1110011",
          "inst_type" -> "INST_I"
        )
      case "CSRRC" =>
        Map(
          "inst_name" -> "CSRRC",
          "is_csr"    -> "true",
          "funct3"    -> "011",
          "opcode"    -> "1110011",
          "inst_type" -> "INST_I"
        )
      case "CSRRWI" =>
        Map(
          "inst_name" -> "CSRRWI",
          "is_csr"    -> "true",
          "funct3"    -> "101",
          "opcode"    -> "1110011",
          "inst_type" -> "INST_I"
        )
      case "CSRRSI" =>
        Map(
          "inst_name" -> "CSRRSI",
          "is_csr"    -> "true",
          "funct3"    -> "110",
          "opcode"    -> "1110011",
          "inst_type" -> "INST_I"
        )
      case "CSRRCI" =>
        Map(
          "inst_name" -> "CSRRCI",
          "is_csr"    -> "true",
          "funct3"    -> "111",
          "opcode"    -> "1110011",
          "inst_type" -> "INST_I"
        )
      case "LB" =>
        Map(
          "inst_name"  -> "LB",
          "has_offset" -> "true",
          "funct3"     -> "000",
          "opcode"     -> "0000011",
          "inst_type"  -> "INST_I"
        )
      case "LH" =>
        Map(
          "inst_name"  -> "LH",
          "has_offset" -> "true",
          "funct3"     -> "001",
          "opcode"     -> "0000011",
          "inst_type"  -> "INST_I"
        )
      case "LBU" =>
        Map(
          "inst_name"  -> "LBU",
          "has_offset" -> "true",
          "funct3"     -> "100",
          "opcode"     -> "0000011",
          "inst_type"  -> "INST_I"
        )
      case "LHU" =>
        Map(
          "inst_name"  -> "LHU",
          "has_offset" -> "true",
          "funct3"     -> "101",
          "opcode"     -> "0000011",
          "inst_type"  -> "INST_I"
        )
      case "LW" =>
        Map(
          "inst_name"  -> "LW",
          "has_offset" -> "true",
          "funct3"     -> "010",
          "opcode"     -> "0000011",
          "inst_type"  -> "INST_I"
        )
      case "JALR" =>
        Map(
          "inst_name" -> "JALR",
          "funct3"    -> "000",
          "opcode"    -> "1100111",
          "inst_type" -> "INST_I"
        )
      case "SLLI" =>
        Map(
          "inst_name" -> "SLLI",
          "fix"       -> "0000000",
          "funct3"    -> "001",
          "opcode"    -> "0010011",
          "inst_type" -> "INST_I"
        )
      case "SRLI" =>
        Map(
          "inst_name" -> "SRLI",
          "fix"       -> "0000000",
          "funct3"    -> "101",
          "opcode"    -> "0010011",
          "inst_type" -> "INST_I"
        )
      case "SRAI" =>
        Map(
          "inst_name" -> "SRAI",
          "fix"       -> "0100000",
          "funct3"    -> "101",
          "opcode"    -> "0010011",
          "inst_type" -> "INST_I"
        )
      // BitPat("b0000????????00000000000000001111")  -> List(INST_I,   FENCE
      // BitPat("b00000000000000000000001000001111")  -> List(INST_I,  FENCEI
      // BitPat("b00000000000000000000000001110011")  -> List(INST_I,   ECALL
      // BitPat("b00000000000100000000000001110011")  -> List(INST_I,  EBREAK
      case "LUI"   => Map("inst_name" -> "LUI", "opcode" -> "0110111", "inst_type" -> "INST_U")
      case "AUIPC" => Map("inst_name" -> "AUIPC", "opcode" -> "0010111", "inst_type" -> "INST_U")
      case "BEQ"   => Map("inst_name" -> "BEQ", "funct3" -> "000", "opcode" -> "1100011", "inst_type" -> "INST_B")
      case "BNE"   => Map("inst_name" -> "BNE", "funct3" -> "001", "opcode" -> "1100011", "inst_type" -> "INST_B")
      case "BLT"   => Map("inst_name" -> "BLT", "funct3" -> "100", "opcode" -> "1100011", "inst_type" -> "INST_B")
      case "BGE"   => Map("inst_name" -> "BGE", "funct3" -> "101", "opcode" -> "1100011", "inst_type" -> "INST_B")
      case "BLTU"  => Map("inst_name" -> "BLTU", "funct3" -> "110", "opcode" -> "1100011", "inst_type" -> "INST_B")
      case "BGEU"  => Map("inst_name" -> "BGEU", "funct3" -> "111", "opcode" -> "1100011", "inst_type" -> "INST_B")
      case "JAL"   => Map("inst_name" -> "JAL", "opcode" -> "1101111", "inst_type" -> "INST_J")
      case "SB"    => Map("inst_name" -> "SB", "funct3" -> "000", "opcode" -> "0100011", "inst_type" -> "INST_S")
      case "SH"    => Map("inst_name" -> "SH", "funct3" -> "001", "opcode" -> "0100011", "inst_type" -> "INST_S")
      case "SW"    => Map("inst_name" -> "SW", "funct3" -> "010", "opcode" -> "0100011", "inst_type" -> "INST_S")
      // Pseudo-instructions mapping to the corresponding RISC-V instructions
      case "NOP"  => Instructions("ADDI") ++ Map("pseudo_inst" -> "true")
      case "BEQZ" => Instructions("BEQ") ++ Map("pseudo_inst" -> "true")
      case "BGEZ" => Instructions("BGE") ++ Map("pseudo_inst" -> "true")
    }
}

object PseudoInstructions {
  def apply(instructionData: Array[String]): Array[String] =
    instructionData(0).toUpperCase match {
      // Map received params to the corresponding RISC-V instruction
      case "NOP"  => { Array("addi", "x0", "x0", "0") }
      case "BEQZ" => { Array("beq", instructionData(1), "x0", instructionData(2)) }
      case "BGEZ" => { Array("bge", instructionData(1), "x0", instructionData(2)) }
    }
}
