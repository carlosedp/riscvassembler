package com.carlosedp.rvasmcli

import com.carlosedp.riscvassembler._
import mainargs.{main, arg, ParserForMethods}

object Main {
  println("RISC-V Assembler for Scala")
  @main(
    name = "main",
    doc = "This tool parses input strings or files in RISC-V assembly language generating hexadecimal machine code.",
  )
  def run(
    @arg(
      name = "assembly",
      short = 'a',
      doc = "Assembly instruction string in quotes(can be multiple instructions separated by `\\n`",
    )
    assembly: String = "",
    @arg(name = "file-in", short = 'f', doc = "Assembly file input")
    fileIn: String = "",
    @arg(
      name = "file-out",
      short = 'o',
      doc = "If defined, output will be redirected to this file (overwrite if exists)",
    )
    fileOut: String = "",
  ) =
    if (!assembly.isEmpty()) {
      var output = ""
      assembly.split("\\\\n").foreach { l =>
        output += RISCVAssembler.fromString(l.trim)
      }
      if (fileOut.isEmpty()) {
        println("Generated Output: \n")
        println(output)
      } else {
        os.write.over(os.pwd / fileOut, output)
        println(s"Generated $fileOut")
      }
    } else if (!fileIn.isEmpty()) {
      val output = RISCVAssembler.fromFile(fileIn)
      if (fileOut.isEmpty()) {
        println("Generated Output: \n")
        println(output)
      } else {
        os.write.over(os.pwd / fileOut, output)
        println(s"Generated $fileOut")
      }
    } else {
      println("Run tool with --help for options")
    }

  def main(args: Array[String]): Unit = {
    val res = ParserForMethods(this).runOrExit(args.toIndexedSeq)
    println(res)
  }
}
