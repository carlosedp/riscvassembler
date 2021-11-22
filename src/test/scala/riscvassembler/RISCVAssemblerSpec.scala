import com.carlosedp.scalautils.riscvassembler._
import org.scalatest._

import java.io.{File, PrintWriter}
import java.nio.file.{Files, Paths}

import flatspec._
import matchers.should._

class RISCVAssemblerProgsSpec extends AnyFlatSpec with BeforeAndAfterEach with BeforeAndAfter with Matchers {
  var memoryfile = ""

  // Create a temporary directory before executing tests and delete it after
  before {
    Files.createDirectories(Paths.get("tmpasm"));
  }
  after {
    Files.deleteIfExists(Paths.get("tmpasm"));
  }

  //Create a random temporary file for the asm source and delete it after use
  override def beforeEach(): Unit =
    memoryfile =
      Paths.get("tmpasm/", scala.util.Random.alphanumeric.filter(_.isLetter).take(15).mkString + ".s").toString()
  override def afterEach(): Unit = {
    val _ = new File(memoryfile).delete()
  }

  behavior of "RISCVAssembler"
  it should "generate hex output for multiple I-type instructions" in {
    val input =
      """addi x0, x0, 0
         addi x1, x1, 1
         addi x2, x2, 2
        """.stripMargin
    val output = RISCVAssembler.fromString(input)

    val correct =
      """00000013
        |00108093
        |00210113""".stripMargin

    output should be(correct)
  }

  it should "generate hex output for multiple instructions" in {
    val input =
      """
        addi x0, x0, 0
        addi x1, x1, 1
        addi x2, x2, 2
        """.stripMargin
    val output = RISCVAssembler.fromString(input)

    val correct =
      """00000013
        |00108093
        |00210113""".stripMargin

    output should be(correct)
  }

  it should "generate hex output for multiple instructions with comments" in {
    val input =
      """
      addi x1 , x0,   1000  /* x1  = 1000 0x3E8 */
      addi x2 , x1,   2000  /* x2  = 3000 0xBB8 */
      addi x3 , x2,  -1000  /* x3  = 2000 0x7D0 */
      addi x4 , x3,  -2000  /* x4  = 0    0x000 */
      addi x5 , x4,   1000  /* x5  = 1000 0x3E8 */
        """.stripMargin
    val output = RISCVAssembler.fromString(input)

    val correct =
      """3e800093
        |7d008113
        |c1810193
        |83018213
        |3e820293""".stripMargin.toUpperCase

    output should be(correct)
  }

  it should "generate hex output for pseudo-instructions" in {
    val input =
      """
        addi x0, x0, 0
        nop
        beqz x0, +4
        """.stripMargin
    val output = RISCVAssembler.fromString(input)
    val correct =
      """00000013
        |00000013
        |00000263""".stripMargin

    output should be(correct)
  }

  it should "generate hex output from file source" in {
    val prog = """
    lui x2, 0xc0000000
    addi x1, x0, 4
    addi x2, x0, 4
    nop
    add  x3, x2, x1
    beq x1, x2, +4094
    sb x3, 1024(x2)
    jal x1, +2048
    """.stripMargin
    new PrintWriter(new File(memoryfile)) { write(prog); close }
    val output = RISCVAssembler.fromFile(memoryfile)

    val correct =
      """c0000137
        |00400093
        |00400113
        |00000013
        |001101b3
        |7e208fe3
        |40310023
        |001000ef""".stripMargin.toUpperCase

    output should be(correct)
  }

}
