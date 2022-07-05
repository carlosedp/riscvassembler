import com.carlosedp.scalautils.riscvassembler.RISCVAssembler
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach}

class RISCVAssemblerProgsSpec extends AnyFlatSpec with BeforeAndAfterEach with BeforeAndAfter with Matchers {
  val tmpdir = "tmpasm"
  var memoryfile: os.Path = _

  // Create a temporary directory before executing tests and delete it after
  before {
    os.makeDir(os.pwd / tmpdir)

  }
  after {
    try {
      os.remove(os.pwd / tmpdir)
    } catch {
      case _: Exception => // not empty, ignore
    }
  }

  // Create a random temporary file for the asm source and delete it after use
  override def beforeEach(): Unit =
    memoryfile = os.pwd / tmpdir / (scala.util.Random.alphanumeric.filter(_.isLetter).take(15).mkString + ".s")
  override def afterEach(): Unit =
    os.remove.all(memoryfile)

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
        |00210113
        |""".stripMargin

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
        |00210113
        |""".stripMargin

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
        |3e820293
        |""".stripMargin.toUpperCase

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
        |00000263
        |""".stripMargin

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
    lw x4, 80(x1)
    jal x1, +2048
    """.stripMargin
    os.write(memoryfile, prog)
    val output = RISCVAssembler.fromFile(memoryfile.toString)

    val correct =
      """c0000137
        |00400093
        |00400113
        |00000013
        |001101b3
        |7e208fe3
        |40310023
        |0500a203
        |001000ef
        |""".stripMargin.toUpperCase

    output should be(correct)
  }

  it should "generate hex output from file source with directives" in {
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
    os.write(memoryfile, prog)
    val output = RISCVAssembler.fromFile(memoryfile.toString)

    val correct =
      """3e800093
        |7d008113
        |c1810193
        |83018213
        |3e820293
        |""".stripMargin.toUpperCase

    output should be(correct)
  }

  it should "generate hex output from file source with labels" in {
    val prog = """
    main:   lui x1, 0x30003000
            addi x2, x0, 1
    wait:   lw x3, 0(x1)
            bne x2, x3, -4
    cont:   sw x0, 0(x1)
    wait2:  lw x3, 0(x1)
            bne x2, x3, -4
    cont2:  addi x3, x0, 2
    """.stripMargin
    os.write(memoryfile, prog)
    val output = RISCVAssembler.fromFile(memoryfile.toString)

    val correct =
      """300030b7
        |00100113
        |0000a183
        |fe311ee3
        |0000a023
        |0000a183
        |fe311ee3
        |00200193
        |""".stripMargin.toUpperCase

    output should be(correct)
  }

}
