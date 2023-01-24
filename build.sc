import mill._
import mill.scalalib._
import mill.scalalib.publish._
import mill.scalalib.api.Util.isScala3
import mill.scalanativelib._, mill.scalanativelib.api._
import mill.scalajslib._, mill.scalajslib.api._
import scalafmt._
import java.util.Date

// Plugins
import $ivy.`com.lihaoyi::mill-contrib-scoverage:`
import mill.contrib.scoverage.{ScoverageModule, ScoverageReport}
import $ivy.`com.goyeau::mill-scalafix::0.2.11`
import com.goyeau.mill.scalafix.ScalafixModule
import $ivy.`io.chris-kipp::mill-ci-release::0.1.5`
import io.kipp.mill.ci.release.{CiReleaseModule, SonatypeHost}
import $ivy.`de.tototec::de.tobiasroeser.mill.vcs.version::0.3.0`
import de.tobiasroeser.mill.vcs.version.VcsVersion
import $ivy.`io.github.davidgregory084::mill-tpolecat::0.3.2`
import io.github.davidgregory084.TpolecatModule
import $ivy.`com.lihaoyi::mill-contrib-buildinfo:`
import mill.contrib.buildinfo.BuildInfo
import $ivy.`io.github.alexarchambault.mill::mill-native-image::0.1.23`
import io.github.alexarchambault.millnativeimage.NativeImage

val scala212            = "2.12.17"
val scala213            = "2.13.10"
val scala3              = "3.2.1"
val scalaVersions       = Seq(scala212, scala213, scala3)
val scalaNativeVersions = scalaVersions.map((_, "0.4.9"))
val scalaJsVersions     = scalaVersions.map((_, "1.12.0"))

object versions {
  val scalatest       = "3.2.15"
  val oslib           = "0.9.0"
  val organizeimports = "0.6.0"
  val semanticdb      = "4.5.13"
  val mainargs        = "0.3.0"
  val scoverage       = "2.0.7"
  val scalajsdom      = "2.3.0"
}

object riscvassembler extends Module {
  object jvm extends Cross[RiscvAssemblerJVMModule](scalaVersions: _*)
  class RiscvAssemblerJVMModule(val crossScalaVersion: String)
    extends RiscvAssemblerLib
    with RiscvAssemblerPublish
    with ScoverageModule {
    def millSourcePath   = super.millSourcePath / os.up
    def scoverageVersion = versions.scoverage
    object test extends ScoverageTests with RiscvAssemblerTest {
      // Add JVM specific tests to the source path
      def testSourcesJVM   = T.sources(super.millSourcePath / "jvm" / "src")
      override def sources = T.sources(super.sources() ++ testSourcesJVM())
    }
  }

  object native extends Cross[RiscvAssemblerNativeModule](scalaNativeVersions: _*)
  class RiscvAssemblerNativeModule(val crossScalaVersion: String, crossScalaNativeVersion: String)
    extends RiscvAssemblerLib
    with RiscvAssemblerPublish
    with ScalaNativeModule {
    def millSourcePath     = super.millSourcePath / os.up / os.up
    def scalaNativeVersion = crossScalaNativeVersion
    object test extends Tests with RiscvAssemblerTest {
      def nativeLinkStubs = true
    }
  }

  object scalajs extends Cross[RiscvAssemblerScalajsModule](scalaJsVersions: _*)
  class RiscvAssemblerScalajsModule(val crossScalaVersion: String, crossScalaJsVersion: String)
    extends RiscvAssemblerLib
    with RiscvAssemblerPublish
    with ScalaJSModule {
    def millSourcePath = super.millSourcePath / os.up / os.up
    def scalaJSVersion = crossScalaJsVersion
    def ivyDeps        = Agg(ivy"org.scala-js::scalajs-dom::${versions.scalajsdom}")

    def scalaJSUseMainModuleInitializer = true
    def moduleKind                      = T(ModuleKind.CommonJSModule)
    object test extends Tests with RiscvAssemblerTest {
      def moduleKind  = T(ModuleKind.NoModule)
      def jsEnvConfig = T(JsEnvConfig.JsDom())
    }
  }
}

// Build ScalaNative or Native Image for current platform
// Scala Native: `./mill rvasmcli.nativeLink`
// Native Image: `./mill rvasmcli.nativeImage`
object rvasmcli extends RVasmcliBase

def LLVMTriples = System.getProperty("os.name").toLowerCase match {
  case os if os.contains("linux") =>
    Seq("x86_64-linux-gnu", "arm64-linux-gnu", "powerpc64le-linux-gnu", "riscv64-linux-gnu")
  case os if os.contains("mac") =>
    Seq("x86_64-apple-darwin20.3.0", "arm64-apple-darwin20.3.0")
}

// Build ScalaNative or Native Image for cross-architecture depending on LLVM Triple setting above.
// Cross build for available architectures on current OS with: `./mill rvasmclicross.__.nativeLink`
// On Mac, install LLVM using Homebrew which contains libs for amd64 and arm64
// On Linux, install "build-essential clang build-essential clang crossbuild-essential-arm64 crossbuild-essential-riscv64 crossbuild-essential-amd64 crossbuild-essential-ppc64el"
object rvasmclicross extends Cross[RVASMCLI](LLVMTriples: _*)
class RVASMCLI(val LLVMtriple: String) extends RVasmcliBase {
  def nativeTarget = Some(LLVMtriple)
}

// This trait allows building both Scala Native and Native Image (using GraalVM)
// The trait is also used for cross-building to different architectures
trait RVasmcliBase
  extends ScalaNativeModule
  with NativeImage
  with TpolecatModule
  with ScalafixModule
  with ScalafmtModule {
  def scalaVersion   = scala3
  def millSourcePath = build.millSourcePath / "rvasmcli"
  def ivyDeps = Agg(
    ivy"com.lihaoyi::os-lib::${versions.oslib}",
    ivy"com.lihaoyi::mainargs::${versions.mainargs}",
  )
  def scalafixIvyDeps = Agg(ivy"com.github.liancheng::organize-imports:${versions.organizeimports}")
  // Scala Native settings
  def scalaNativeVersion      = scalaNativeVersions.head._2
  def moduleDeps              = Seq(riscvassembler.native(scala3, scalaNativeVersions.head._2))
  private def actualMainClass = "com.carlosedp.rvasmcli.Main"
  def mainClass               = Some(actualMainClass)
  def logLevel                = NativeLogLevel.Info
  def releaseMode             = ReleaseMode.Debug
  if (System.getProperty("os.name").toLowerCase == "linux") {
    def nativeLTO            = LTO.Thin
    def nativeLinkingOptions = Array("-static", "-fuse-ld=lld")
  }
  // Native Image (GraalVM) settings
  def nativeImageName = "rvasmcli"
  def nativeImageGraalVmJvmId = T {
    sys.env.getOrElse("GRAALVM_ID", "graalvm-java17:22.2.0")
  }
  def nativeImageClassPath = runClasspath()
  def nativeImageMainClass = actualMainClass
  def nativeImageOptions = super.nativeImageOptions() ++ Seq(
    "--no-fallback",
    "--enable-url-protocols=http,https",
    "-Djdk.http.auth.tunneling.disabledSchemes=",
  )

  object test extends Tests with RiscvAssemblerTest
}

// Aggregate reports for all projects
object scoverage extends ScoverageReport {
  override def scalaVersion     = scala3
  override def scoverageVersion = versions.scoverage
}

trait RiscvAssemblerLib
  extends CrossScalaModule
  with TpolecatModule
  with BuildInfo
  with ScalafixModule
  with ScalafmtModule {
  def ivyDeps = Agg(
    ivy"com.lihaoyi::os-lib::${versions.oslib}",
  )
  def scalafixIvyDeps = Agg(ivy"com.github.liancheng::organize-imports:${versions.organizeimports}")
  def scalacPluginIvyDeps =
    super.scalacPluginIvyDeps() ++ (if (!isScala3(crossScalaVersion))
                                      Agg(ivy"org.scalameta:::semanticdb-scalac:${versions.semanticdb}")
                                    else Agg.empty)
  def artifactName = "riscvassembler"
  def publishVer: T[String] = T {
    val isTag = T.ctx().env.get("GITHUB_REF").exists(_.startsWith("refs/tags"))
    val state = VcsVersion.vcsState()
    if (state.commitsSinceLastTag == 0 && isTag) {
      state.stripV(state.lastTag.get)
    } else {
      val v = state.stripV(state.lastTag.get).split('.')
      s"${v(0)}.${(v(1).toInt) + 1}-SNAPSHOT"
    }
  }
  def buildInfoMembers: T[Map[String, String]] = T {
    Map(
      "appName"     -> artifactName.toString,
      "appVersion"  -> publishVer(),
      "revision"    -> VcsVersion.vcsState().format(),
      "buildCommit" -> VcsVersion.vcsState().currentRevision,
      "commitDate" -> os
        .proc("git", "log", "-1", "--date=format:\"%a %b %d %T %z %Y\"", "--format=\"%ad\"")
        .call()
        .out
        .trim,
      "buildDate" -> new Date().toString,
    )
  }
  def buildInfoObjectName  = "BuildInfo"
  def buildInfoPackageName = Some("com.carlosedp.riscvassembler")
}

trait RiscvAssemblerTest extends ScalaModule with TestModule.ScalaTest {
  def ivyDeps = Agg(ivy"org.scalatest::scalatest::${versions.scalatest}")
}

trait RiscvAssemblerPublish extends RiscvAssemblerLib with CiReleaseModule {
  def publishVersion = super.publishVer
  def pomSettings = PomSettings(
    description    = "RiscvAssembler is a RISC-V assembler library to be used on Scala and Chisel HDL projects.",
    organization   = "com.carlosedp",
    url            = "https://github.com/carlosedp/riscvassembler",
    licenses       = Seq(License.MIT),
    versionControl = VersionControl.github("carlosedp", "RiscvAssembler"),
    developers = Seq(
      Developer("carlosedp", "Carlos Eduardo de Paula", "https://github.com/carlosedp"),
    ),
  )
  override def sonatypeHost = Some(SonatypeHost.s01)
}

// Toplevel commands and aliases
def runTasks(t: Seq[String])(implicit ev: eval.Evaluator) = T.task {
  mill.main.MainModule.evaluateTasks(
    ev,
    t.flatMap(x => x +: Seq("+")).flatMap(x => x.split(" ")).dropRight(1),
    mill.define.SelectMode.Separated,
  )(identity)
}
def lint(implicit ev: eval.Evaluator) = T.command {
  runTasks(
    Seq(
      s"riscvassembler.jvm[$scala3].fix",
      "rvasmcli.fix",
      "mill.scalalib.scalafmt.ScalafmtModule/reformatAll __.sources",
    ),
  )
}
def deps(implicit ev: eval.Evaluator) = T.command {
  mill.scalalib.Dependency.showUpdates(ev)
}
def coverage(implicit ev: eval.Evaluator) = T.command {
  runTasks(
    Seq(
      s"riscvassembler.jvm[$scala3].test",
      "rvasmcli.test",
      "scoverage.htmlReportAll",
      "scoverage.xmlReportAll",
      "scoverage.consoleReportAll",
    ),
  )
}
def pub(implicit ev: eval.Evaluator) = T.command {
  runTasks(Seq("io.kipp.mill.ci.release.ReleaseModule/publishAll"))
}
def publocal(implicit ev: eval.Evaluator) = T.command {
  runTasks(Seq("riscvassembler.__.publishLocal"))
}
def bin(implicit ev: eval.Evaluator) = T.command {
  runTasks(Seq("show rvasmcli.nativeLink"))
}
def testall(implicit ev: eval.Evaluator) = T.command {
  runTasks(Seq("riscvassembler.__.test", "rvasmcli.test"))
}
def test(implicit ev: eval.Evaluator) = T.command {
  runTasks(Seq("riscvassembler.jvm[" + scala3 + "].test", "rvasmcli.test"))
}
