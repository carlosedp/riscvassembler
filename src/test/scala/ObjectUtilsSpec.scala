import com.carlosedp.scalautils.ObjectUtils._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class ObjectUtilsSpec extends AnyFlatSpec with Matchers {

  behavior of "Padding"

  it should "pad binary string with zeros" in {
    val myBinary = "100"
    val output   = myBinary.padZero(12)
    output should be("000000000100")
  }

  behavior of "Truncating"

  it should "truncate long 1L to 32 bits" in {
    val myLong = 1L
    val output = myLong.to32Bit
    output should be(1L)
  }

  it should "truncate long 0xffffffff to 32 bits" in {
    val myLong = 0xffffffffL
    val output = myLong.to32Bit
    output should be(0xffffffffL)
  }

  it should "truncate long bigger than 0xffffffff to 32 bits" in {
    val myLong = 0xaaffffffffL
    val output = myLong.to32Bit
    output should be(0xffffffffL)
  }

  it should "truncate int 0xffffffff to 32 bits" in {
    val myInt  = 0xffffffff
    val output = myInt.to32Bit
    output should be(0xffffffffL)
  }

  it should "truncate BigInt bigger than 0xffffffff to 32 bits" in {
    val myBigInt = BigInt("aaffffffff", 16)
    val output   = myBigInt.to32Bit
    output should be(0xffffffffL)
  }

  behavior of "Converting"

  it should "convert a binary string to BigInt" in {
    val output = "101010".b
    output should be(42)
  }

  it should "convert an hex string to BigInt" in {
    val output = "abc".x
    output should be(2748)
  }

  it should "convert an oct string to BigInt" in {
    val output = "567".o
    output should be(375)
  }
}
