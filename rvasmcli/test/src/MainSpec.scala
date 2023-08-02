package com.carlosedp.rvasmcli

import org.scalatest.flatspec.*
import org.scalatest.matchers.should.*

class MainSpec extends AnyFlatSpec with Matchers:

  behavior of "rvasmcli"

  it should "help message if no parameters are passed" in {
    val res = Main.run()
    res should include("--help")
  }

  it should "generate hex output for single instruction passed as argument" in {
    val res = Main.run(assembly = "addi x0, x1, 10")
    res should include("00A08013")
  }

  it should "generate hex output for multiple instruction passed as argument" in {
    val res = Main.run(assembly = "addi x0, x1, 10\njal x0, 128")
    res should include("00A08013\n0800006F")
  }

  it should "generate hex output for single instruction with file output" in {
    val testfile = os.pwd / "testfile.hex"
    val res      = Main.run(assembly = "addi x0, x1, 10", fileOut = "testfile.hex")
    res should include("testfile.hex")
    val filecontents = os.read(testfile)
    filecontents should include("00A08013")
    var _ = os.remove(testfile)
  }

  it should "generate hex output from file input" in {
    val testfile = os.pwd / "testfile.asm"
    os.write.over(testfile, "addi x0, x1, 10")
    val res = Main.run(fileIn = testfile.toString())
    res should include("00A08013")
    var _ = os.remove(testfile)
  }

  it should "generate hex file output from file input" in {
    val testfilein  = os.pwd / "testfile.asm"
    val testfileout = os.pwd / "testfile.hex"
    os.write.over(testfilein, "addi x0, x1, 10")
    val res = Main.run(fileIn = testfilein.toString(), fileOut = "testfile.hex")
    res should include("testfile.hex")
    val filecontents = os.read(testfileout)
    filecontents should include("00A08013")
    var _ = os.remove(testfilein)
    var _ = os.remove(testfileout)
  }
