package com.carlosedp
package rvasmcli

import com.carlosedp.riscvassembler.RISCVAssembler
import mainargs.{ParserForMethods, arg, main}

object Main:
  @main(
    name = "main",
    doc  = "This tool parses input strings or files in RISC-V assembly language generating hexadecimal machine code.",
  )
  def run(
      @arg(
        name  = "assembly",
        short = 'a',
        doc   = "Assembly instruction string in quotes(can be multiple instructions separated by `\\n`",
      )
      assembly: String = "",
      @arg(name = "file-in", short = 'f', doc = "Assembly file input")
      fileIn: String = "",
      @arg(
        name  = "file-out",
        short = 'o',
        doc   = "If defined, output will be redirected to this file (overwrite if exists)",
      )
      fileOut: String = "",
    ): String =
    var output = ""
    var hex    = ""
    if !assembly.isEmpty() then
      assembly.split("\\\\n").foreach { l =>
        hex += RISCVAssembler.fromString(l.trim)
      }
      if fileOut.isEmpty() then
        output += "Generated Output: \n"
        output += hex
      else
        os.write.over(os.pwd / fileOut, hex)
        output = s"Generated $fileOut"
    else if !fileIn.isEmpty() then
      hex = RISCVAssembler.fromFile(fileIn)
      if fileOut.isEmpty() then
        output += "Generated Output: \n"
        output += hex
      else
        os.write.over(os.pwd / fileOut, hex)
        output = s"Generated $fileOut"
    else
      output = s"""
                  |RISCVAssembler version ${RISCVAssembler.AppInfo.appVersion}
                  |Revision ${RISCVAssembler.AppInfo.revision}
                  |Commit: ${RISCVAssembler.AppInfo.buildCommit}
                  |Commit Date: ${RISCVAssembler.AppInfo.commitDate}
                  |Build Date: ${RISCVAssembler.AppInfo.buildDate}
                  |Run tool with --help for options
                  |""".stripMargin
    output

  def main(args: Array[String]): Unit =
    println("RISC-V Assembler for Scala")
    val out = ParserForMethods(this).runOrExit(args.toIndexedSeq)
    println(out)
