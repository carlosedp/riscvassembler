import com.carlosedp.scalautils.ObjectUtils._
import org.scalatest._

import flatspec._
import matchers.should._

class ObjectUtilsSpec extends AnyFlatSpec with Matchers {

  behavior of "ObjectUtils"

  it should "pad binary string with zeros" in {
    val myBinary = "100"
    val output   = myBinary.padZero(12)
    output should be("000000000100")
  }

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

}
