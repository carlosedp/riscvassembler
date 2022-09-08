# RISCVAssembler

A RISC-V assembler library for Scala/Chisel HDL projects.

For more information, check the [scaladoc](https://www.javadoc.io/doc/com.carlosedp/riscvassembler_2.13/latest/com/carlosedp/riscvassembler/index.html).


[![Scala version support](https://index.scala-lang.org/carlosedp/riscvassembler/riscvassembler/latest-by-scala-version.svg?color=blue)](https://index.scala-lang.org/carlosedp/riscvassembler/riscvassembler)
[![Scaladoc](https://www.javadoc.io/badge/com.carlosedp/riscvassembler_2.13.svg?color=blue&label=Scaladoc)](https://javadoc.io/doc/com.carlosedp/riscvassembler_2.13/latest)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.carlosedp/riscvassembler_2.13/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.carlosedp/riscvassembler_2.13)
[![Sonatype Snapshots](https://img.shields.io/nexus/s/com.carlosedp/riscvassembler_2.13?server=https%3A%2F%2Fs01.oss.sonatype.org)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/carlosedp/)
<br>
[![Scala CI](https://github.com/carlosedp/riscvassembler/actions/workflows/scala.yml/badge.svg)](https://github.com/carlosedp/riscvassembler/actions/workflows/scala.yml)
[![codecov](https://codecov.io/gh/carlosedp/riscvassembler/branch/main/graph/badge.svg?token=YNEKF3OO04)](https://codecov.io/gh/carlosedp/riscvassembler)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=carlosedp_riscvassembler&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=carlosedp_riscvassembler)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-green.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![Mergify Status](https://img.shields.io/endpoint.svg?url=https://api.mergify.com/v1/badges/carlosedp/riscvassembler&style=flat)](https://mergify.com)

## Using in your project

### SBT

When using SBT, add the following lines to your `build.sbt` file.

```scala
// Import libraries
libraryDependencies += "com.carlosedp" %% "riscvassembler" % ""
```

### Mill

If you use `mill` build tool, I recommend adding the following way to your `build.sc`:

```scala
// Add to your ivyDeps
def ivyDeps = Agg(
  ivy"com.carlosedp::riscvassembler:"
  ...
)
```

## Sample Code

The library provides methods to generate hexadecimal machine code (memory files to be consumed by `readmemh` statements) from assembly input. Works similarly to `gcc + ld + objcopy + hexdump` as used on this [`Makefile`](https://github.com/carlosedp/chiselv/gcc/test/Makefile) or <https://riscvasm.lucasteske.dev> web app.

The library can be seen in use in [ChiselV](https://github.com/carlosedp/chiselv), my RV32I core written in Chisel. The core tests use the library to generate [test data](https://github.com/carlosedp/chiselv/blob/e014da49ace5d5dd917eac3e3bf8ca6bbeadc244/chiselv/test/src/CPUSingleCycleInstructionSpec.scala#L71).

Currently the lib does not support labels and jumping to defined labels as it doesn't calculate the addresses.

The program can be a single line or multi-line statements(supports inline or line comments) and can be generated from a simple string, multi-line string or loaded from a file.

### Examples

**Reading from file:**

```asm
# Sample file "input.asm":
addi x0, x0, 0
addi x1, x1, 1
addi x2, x2, 2
```

```scala
// Using the lib
val outputHex = RISCVAssembler.fromFile("input.asm")

// outputHex will be:
//    00000013
//    00108093
//    00210113
```

**From a multiline string:**

```scala
// Sample input:
val input =
      """addi x0, x0, 0
         addi x1, x1, 1
         addi x2, x2, 2
        """.stripMargin
val output = RISCVAssembler.fromString(input)

// outputHex will be:
//    00000013
//    00108093
//    00210113
 ```

Which can be used as the input to tests in a RISC-V Core.

### Using Snapshot versions

Snapshot versions are released on every commit to main branch and might be broken (check CI). If you want to use it, configure as follows.

#### SBT

Add the new Sonatype repository to your `build.sbt` resolvers and change the library import name:

```scala
// and change the dependency to latest SNAPSHOT as:
libraryDependencies += "com.carlosedp" %% "riscvassembler" % "0.10-SNAPSHOT"


resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases"),
  "Sonatype New OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots"
)
```

Confirm the latest versions displayed on the badges at the top of this readme for both stable and snapshot (without the leading "v").

#### Mill

If you use `mill` build tool, I recommend adding the following way to your `build.sc`:

```scala
import coursier.MavenRepository

...

// Inside your project `object`:
// And add the snapshot resolver if using it
def repositoriesTask = T.task { super.repositoriesTask() ++ Seq(
  MavenRepository("https://s01.oss.sonatype.org/content/repositories/snapshots")
) }

def ivyDeps = Agg(
  ivy"com.carlosedp::riscvassembler:0.10-SNAPSHOT"
  ...
)
```

The library has been published to Maven Central thru Sonatype:

* <https://search.maven.org/artifact/com.carlosedp/riscvassembler>
* <https://mvnrepository.com/artifact/com.carlosedp/riscvassembler>
