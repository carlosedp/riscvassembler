# RISCVAssembler

A RISC-V assembler library for Scala/Chisel HDL projects. For details, check the [scaladoc](https://www.javadoc.io/doc/com.carlosedp/riscvassembler_2.13/latest/com/carlosedp/riscvassembler/index.html).

[![riscvassembler Scala version support](https://index.scala-lang.org/carlosedp/riscvassembler/riscvassembler/latest-by-scala-version.svg?platform=jvm)](https://index.scala-lang.org/carlosedp/riscvassembler/riscvassembler)
[![riscvassembler Scala version support](https://index.scala-lang.org/carlosedp/riscvassembler/riscvassembler/latest-by-scala-version.svg?platform=native0.4)](https://index.scala-lang.org/carlosedp/riscvassembler/riscvassembler)
[![riscvassembler Scala version support](https://index.scala-lang.org/carlosedp/riscvassembler/riscvassembler/latest-by-scala-version.svg?platform=sjs1)](https://index.scala-lang.org/carlosedp/riscvassembler/riscvassembler)
[![Sonatype Snapshots](https://img.shields.io/nexus/s/com.carlosedp/riscvassembler_2.13?server=https%3A%2F%2Fs01.oss.sonatype.org)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/carlosedp/)


[![Scala CI](https://github.com/carlosedp/riscvassembler/actions/workflows/scala.yml/badge.svg)](https://github.com/carlosedp/riscvassembler/actions/workflows/scala.yml)
[![codecov](https://codecov.io/gh/carlosedp/riscvassembler/branch/main/graph/badge.svg?token=YNEKF3OO04)](https://codecov.io/gh/carlosedp/riscvassembler)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-green.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![Mergify Status](https://img.shields.io/endpoint.svg?url=https://api.mergify.com/v1/badges/carlosedp/riscvassembler&style=flat)](https://mergify.com)
[![Scaladoc](https://www.javadoc.io/badge/com.carlosedp/riscvassembler_2.13.svg?color=blue&label=Scaladoc)](https://javadoc.io/doc/com.carlosedp/riscvassembler_2.13/latest)


## Using in your project

### SBT

When using SBT, add the following lines to your `build.sbt` file.

```scala
// Import libraries
libraryDependencies += "com.carlosedp" %% "riscvassembler" % "1.4.0"  //ReleaseVerSBT
```

### Mill

If you use `mill` build tool, add the following dep to your `build.sc`:

```scala
// Add to your ivyDeps
def ivyDeps = Agg(
  ivy"com.carlosedp::riscvassembler:1.4.0"  //ReleaseVerMill
  ...
)
```

## Library Description and Sample Code

The library is pure Scala and provides methods to generate hexadecimal machine code (like memory files to be consumed by `readmemh` statements) from assembly input. It does not depend on Chisel or other libs and intent to work similarly to a simpler `gcc + ld + objcopy + hexdump` flow as used on this [`Makefile`](https://github.com/carlosedp/chiselv/gcc/test/Makefile) or <https://riscvasm.lucasteske.dev> web app.

The library can be seen in use in [ChiselV](https://github.com/carlosedp/chiselv), my RV32I core written in Chisel. The core tests use the library to generate [test data](https://github.com/carlosedp/chiselv/blob/e014da49ace5d5dd917eac3e3bf8ca6bbeadc244/chiselv/test/src/CPUSingleCycleInstructionSpec.scala#L71).

What the library **can and can not do**:

- The library **can** generate hex(machine code) for most RV32 instructions;
- It **can** accept either offsets or [labels](https://github.com/riscv-non-isa/riscv-asm-manual/blob/master/riscv-asm.md#labels) (in the same or previous line) for jump/branch instructions;
- It **can** implement [some](./riscvassembler/src/internal/Instructions.scala#73) pseudo-instructions (more to come soon);
- It **can** generate one machine code for each input asm instruction;
- It **can not** decompose one pseudo-instruction to multiple instructions. Eg. `li x1, 0x80000000` to `addiw	ra,zero,1` + `ra,ra,0x1f` as gcc;
- It **can not** validate your input asm code. Imm values might get truncated if not proper used;
- It **can not** support [assembler relocation functions](https://github.com/riscv-non-isa/riscv-asm-manual/blob/master/riscv-asm.md#assembler-relocation-functions);
- The library ignores all [asm directives](https://github.com/riscv-non-isa/riscv-asm-manual/blob/master/riscv-asm.md#pseudo-ops).

The program can be a single line or multi-line statements(supports inline or full-line comments) and can be generated from a simple string, multi-line string or loaded from a file.

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

## Command line tool

The library also contains a command line tool that generates the machine code from strings or input files.

```sh
❯ rvasmcli --help
RISC-V Assembler for Scala
main
This tool parses input strings or files in RISC-V assembly language generating hexadecimal machine
code.
  -a --assembly <str>  Assembly instruction string in quotes(can be multiple instructions separated
                       by `\n`
  -f --file-in <str>   Assembly file input
  -o --file-out <str>  If defined, output will be redirected to this file (overwrite if exists)

❯ rvasmcli -a "addi x1, x2, 32\njal x0, 128"
RISC-V Assembler for Scala
Generated Output:

02010093
0800006F
```

To generate the tool binary yourself, use `./mill bin` and the native executable will be generated and it's name printed on screen.

Native binaries for major OS/Arch combinations will be published soon.

### Using Snapshot versions

Snapshot versions are released on every commit to main branch and might be broken (check CI). If you want to use it, configure as follows.

#### SBT

Add the new Sonatype repository to your `build.sbt` resolvers and change the library import name:

```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases"),
  "Sonatype New OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots"
)

// and change the dependency to latest SNAPSHOT as:
libraryDependencies += "com.carlosedp" %% "riscvassembler" % "1.5-SNAPSHOT"  //SnapshotVerSBT
```

Confirm the latest versions displayed on the badges at the top of this readme for both stable and snapshot (without the leading "v").

#### Mill

If you use `mill` build tool, add the following dep to your `build.sc`:

```scala
import coursier.MavenRepository

...

// Inside your project `object`:
// And add the snapshot resolver if using it
def repositoriesTask = T.task { super.repositoriesTask() ++ Seq(
  MavenRepository("https://s01.oss.sonatype.org/content/repositories/snapshots")
) }

def ivyDeps = Agg(
  ivy"com.carlosedp::riscvassembler:1.5-SNAPSHOT"  //SnapshotVerMill
  ...
)
```

### Development and Testing

All build processes are integrated into mill `build.sc`. There are tasks for linting, code coverage, publishing and binary generation.

To locally test and build the library for Scala.js, it's required to have [nodejs](nodejs.org/). After install, run `npm install` so dependencies are installed as well.

To test and generate the Scala Native binaries, the [LLVM toolchain](https://scala-native.org/en/stable/user/setup.html#installing-clang-and-runtime-dependencies) is required.

Publishing flow:

1. Commit and push latest changes to `main` (generates SNAPSHOT)
2. Git tag new version
3. Push tag to origin (generates a new release)
4. Check if readme was updated

The library has been published to Maven Central thru Sonatype:

- <https://search.maven.org/artifact/com.carlosedp/riscvassembler>
- <https://mvnrepository.com/artifact/com.carlosedp/riscvassembler>
