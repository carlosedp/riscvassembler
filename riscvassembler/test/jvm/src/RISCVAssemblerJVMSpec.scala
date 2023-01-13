package com.carlosedp.riscvassembler

import java.io.{File, FileWriter}
import scala.util.Try
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

class RISCVAssemblerJVMSpec extends AnyFlatSpec with BeforeAndAfterEach with BeforeAndAfterAll with Matchers {

  it should "generate hex output from file source" in {
    val prog = """
      main:   lui x1, 0xfffff
              addi x2, x0, 1
      wait:   lw x3, 0(x1)
              bne x2, x3, wait
      cont:   sw x0, 0(x1)
      wait2:  lw x3, 0(x1)
              bne x2, x3, wait2
      cont2:  addi x3, x0, 2
    """.stripMargin

    val file       = File.createTempFile("hextemp", ".asm")
    val fileWriter = new FileWriter(new File(file.getAbsolutePath()))
    fileWriter.write(prog)
    fileWriter.close()
    val output = RISCVAssembler.fromFile(file.getAbsolutePath()).trim

    val correct =
      """
        |fffff0b7
        |00100113
        |0000a183
        |fe311ee3
        |0000a023
        |0000a183
        |fe311ee3
        |00200193
        |""".stripMargin.toUpperCase.trim

    output should be(correct)
    Try {
      file.delete()
    }
  }

}
