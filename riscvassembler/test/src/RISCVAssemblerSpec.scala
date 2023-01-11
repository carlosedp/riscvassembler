package com.carlosedp.riscvassembler

import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

class RISCVAssemblerSpec extends AnyFlatSpec with BeforeAndAfterEach with BeforeAndAfterAll with Matchers {
//   val tmpdir = os.pwd / "tmphex"
//   var memoryfile: os.Path = _

//   override def beforeAll(): Unit = os.makeDir.all(tmpdir)
//   override def afterAll():  Unit = { val _ = scala.util.Try(os.remove(tmpdir)) }
//   override def beforeEach(): Unit =
//     memoryfile = tmpdir / (scala.util.Random.alphanumeric.filter(_.isLetter).take(15).mkString + ".s")
//   override def afterEach(): Unit = os.remove.all(memoryfile)

  behavior of "RISCVAssembler"

  it should "generate binary output for I-type instructions" in {
    val input  = "addi x1, x2, 10"
    val output = RISCVAssembler.binOutput(input)

    val correct = "00000000101000010000000010010011"
    output should be(correct)
  }

  it should "generate binary output for I-type instructions with max IMM" in {
    val input  = "addi x1, x2, -1"
    val output = RISCVAssembler.binOutput(input)

    val correct = "11111111111100010000000010010011"
    output should be(correct)
  }

  it should "convert binary instruction to hex" in {
    val output = RISCVAssembler.hexOutput("11111111111111111111000001101111")
    output should be("FFFFF06F")
  }

  it should "generate hex output for single I-type instruction" in {
    val input =
      """
        addi x0, x0, 0
        """.stripMargin
    val output = RISCVAssembler.fromString(input).trim

    val correct =
      """
        |00000013
        |
      """.stripMargin.trim

    output should be(correct)
  }

  it should "generate hex output for multiple I-type instructions" in {
    val input =
      """addi x0, x0, 0
         addi x1, x1, 1
         addi x2, x2, 2
        """.stripMargin
    val output = RISCVAssembler.fromString(input).trim

    val correct =
      """
        |00000013
        |00108093
        |00210113
        """.stripMargin.trim

    output should be(correct)
  }

  it should "generate hex output for multiple instructions" in {
    val input =
      """
        addi x0, x0, 0
        addi x1, x1, 1
        addi x2, x2, 2
        """.stripMargin
    val output = RISCVAssembler.fromString(input).trim

    val correct =
      """
        |00000013
        |00108093
        |00210113
        """.stripMargin.trim
    output should be(correct)
  }

  it should "generate blanks for invalid instructions" in {
    val input =
      """
      addi x0, x0, 0
      addi x1, x1
      addi x2, x2, 2
      """.stripMargin
    val output = RISCVAssembler.fromString(input).trim

    val correct =
      """
        |00000013
        |00000000
        |00210113
        """.stripMargin.trim
    output should be(correct)
  }

  it should "generate blanks for bogus data" in {
    val input =
      """
      blabla
      addi x1, x1
      wrong info
      """.stripMargin
    val output = RISCVAssembler.fromString(input).trim
    val correct =
      """
        |00000000
        |00000000
        |00000000
        """.stripMargin.trim
    output should be(correct)
  }

  it should "generate hex output for multiple instructions with /* */ comments" in {
    val input =
      """
      addi x1 , x0,   1000  /* x1  = 1000 0x3E8 */
      addi x2 , x1,   2000  /* x2  = 3000 0xBB8 */
      addi x3 , x2,  -1000  /* x3  = 2000 0x7D0 */
      addi x4 , x3,  -2000  /* x4  = 0    0x000 */
      addi x5 , x4,   1000  /* x5  = 1000 0x3E8 */
        """.stripMargin
    val output = RISCVAssembler.fromString(input).trim

    val correct =
      """
        |3e800093
        |7d008113
        |c1810193
        |83018213
        |3e820293
        |""".stripMargin.toUpperCase.trim

    output should be(correct)
  }

  it should "generate hex output for multiple instructions with // comments" in {
    val input =
      """
      addi x1, x0, 1000
      sw x3, 48(x1)      // With single line comment
      addi x1, x0, 1000
        """.stripMargin
    val output = RISCVAssembler.fromString(input).trim

    val correct =
      """
        |3e800093
        |0230A823
        |3e800093
        |""".stripMargin.toUpperCase.trim

    output should be(correct)
  }

  it should "generate hex output for pseudo-instructions" in {
    val input =
      """
        addi x0, x0, 0
        nop
        beqz x0, +4
        """.stripMargin
    val output = RISCVAssembler.fromString(input).trim
    val correct =
      """
        |00000013
        |00000013
        |00000263
        |""".stripMargin.toUpperCase.trim

    output should be(correct)
  }

  it should "generate hex output with directives" in {
    val prog = """
    .global _boot
    .text

    _boot:                  /* x0  = 0    0x000 */
      /* Test ADDI */
      addi x1 , x0,   1000  /* x1  = 1000 0x3E8 */
      addi x2 , x1,   2000  /* x2  = 3000 0xBB8 */
      addi x3 , x2,  -1000  /* x3  = 2000 0x7D0 */
      addi x4 , x3,  -2000  /* x4  = 0    0x000 */
      addi x5 , x4,   1000  /* x5  = 1000 0x3E8 */
    """.stripMargin
    val output = RISCVAssembler.fromString(prog).trim

    val correct =
      """
        |3e800093
        |7d008113
        |c1810193
        |83018213
        |3e820293
        |""".stripMargin.toUpperCase.trim

    output should be(correct)
  }

  it should "generate hex output with labels" in {
    val prog = """
              main:   addi x1, x0, 1000
              wait:   lw x3, 0(x1)
                      jal x0, -4
    """.stripMargin
    val output = RISCVAssembler.fromString(prog).trim

    val correct =
      """
        |3e800093
        |0000a183
        |ffdff06f
        |""".stripMargin.toUpperCase.trim

    output should be(correct)
  }

  it should "generate hex output using labels inline" in {
    val prog = """
              main:   addi x1, x0, 1000
              wait:   lw x3, 0(x1)
                      jal x0, wait
    """.stripMargin
    val output = RISCVAssembler.fromString(prog).trim

    val correct =
      """
        |3e800093
        |0000a183
        |ffdff06f
        |""".stripMargin.toUpperCase.trim

    output should be(correct)
  }

  it should "generate hex output using labels on previous line" in {
    val prog = """
              main:   addi x1, x0, 1000
              wait:
                      lw x3, 0(x1)
                      jal x0, wait
    """.stripMargin
    val output = RISCVAssembler.fromString(prog).trim

    val correct =
      """
        |3e800093
        |0000a183
        |ffdff06f
        |""".stripMargin.toUpperCase.trim

    output should be(correct)
  }

  it should "generate hex output using labels in same line" in {
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
    val output = RISCVAssembler.fromString(prog).trim

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
  }

  it should "generate hex output with jumps forward and backward" in {
    val prog = """
                 tgt1: addi x1, x2, 10
                 nop
                 jal x4, tgt1
                 jal x4, tgt2
                 nop
                 tgt2: addi x1, x2, 10
    """.stripMargin
    val output = RISCVAssembler.fromString(prog).trim

    val correct =
      """
        |00a10093
        |00000013
        |ff9ff26f
        |0080026f
        |00000013
        |00a10093
        |""".stripMargin.toUpperCase.trim

    output should be(correct)
  }
}
