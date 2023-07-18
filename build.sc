import mill._, mill.scalalib._
import scalafmt._, publish._
import scalajslib._, mill.scalajslib.api._
import scalanativelib._, mill.scalanativelib.api._

// Plugins
import $ivy.`com.lihaoyi::mill-contrib-scoverage:$MILL_VERSION`
import mill.contrib.scoverage.{ScoverageModule, ScoverageReport}
import $ivy.`com.lihaoyi::mill-contrib-buildinfo:$MILL_VERSION`
import mill.contrib.buildinfo.BuildInfo
import $ivy.`com.goyeau::mill-scalafix::0.3.1`
import com.goyeau.mill.scalafix.ScalafixModule
import $ivy.`io.chris-kipp::mill-ci-release::0.1.9`
import io.kipp.mill.ci.release.{CiReleaseModule, SonatypeHost}
import de.tobiasroeser.mill.vcs.version.VcsVersion
import $ivy.`io.github.davidgregory084::mill-tpolecat::0.3.5`
import io.github.davidgregory084.TpolecatModule
import $ivy.`com.carlosedp::mill-aliases::0.3.0`
import com.carlosedp.aliases._

val scala212            = "2.12.17"
val scala213            = "2.13.11"
val scala3              = "3.3.0"
val scalaVersions       = Seq(scala212, scala213, scala3)
val scalaNativeVersions = scalaVersions.map((_, "0.4.14"))
val scalaJsVersions     = scalaVersions.map((_, "1.13.2"))

object versions {
  val scalatest  = "3.2.16"
  val oslib      = "0.9.1"
  val mainargs   = "0.5.0"
  val scoverage  = "2.0.10"
  val scalajsdom = "2.6.0"
}

object riscvassembler extends Module {
  object jvm extends Cross[RiscvAssemblerJVMModule](scalaVersions)
  trait RiscvAssemblerJVMModule
    extends Cross.Module[String]
    with RiscvAssemblerLib
    with RiscvAssemblerPublish
    with ScoverageModule {
    def millSourcePath   = super.millSourcePath / os.up
    def scoverageVersion = versions.scoverage
    object test extends ScoverageTests with RiscvAssemblerTest {
      // Add JVM specific tests to the source path
      def testSourcesJVM   = T.sources(super.millSourcePath / "jvm")
      override def sources = T.sources(super.sources() ++ testSourcesJVM())
    }
  }

  object native extends Cross[RiscvAssemblerNativeModule](scalaNativeVersions)
  trait RiscvAssemblerNativeModule
    extends Cross.Module2[String, String]
    with RiscvAssemblerLib
    with RiscvAssemblerPublish
    with ScalaNativeModule {
    def millSourcePath     = super.millSourcePath / os.up
    def scalaNativeVersion = crossValue2
    object test extends ScalaTests with RiscvAssemblerTest {
      def nativeLinkStubs = true
    }
  }

  object scalajs extends Cross[RiscvAssemblerScalajsModule](scalaJsVersions)
  trait RiscvAssemblerScalajsModule
    extends Cross.Module2[String, String]
    with RiscvAssemblerLib
    with RiscvAssemblerPublish
    with ScalaJSModule {
    def millSourcePath = super.millSourcePath / os.up
    def scalaJSVersion = crossValue2
    def ivyDeps        = Agg(ivy"org.scala-js::scalajs-dom::${versions.scalajsdom}")

    def scalaJSUseMainModuleInitializer = true
    def moduleKind                      = T(ModuleKind.CommonJSModule)
    def jsEnvConfig                     = T(JsEnvConfig.JsDom())
    object test extends ScalaTests with RiscvAssemblerTest
  }
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

  def buildInfoPackageName = "com.carlosedp.riscvassembler"
  def buildInfoMembers = Seq(
    BuildInfo.Value("appName", artifactName.toString),
    BuildInfo.Value("appVersion", publishVer()),
    BuildInfo.Value("revision", VcsVersion.vcsState().format()),
    BuildInfo.Value("buildCommit", VcsVersion.vcsState().currentRevision),
    BuildInfo.Value("commitDate", os.proc("git", "log", "-1", "--format=%ai").call().out.trim()),
    BuildInfo.Value("buildDate", new java.util.Date().toString),
  )
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

// Build rvasmcli Scala Native binary for current platform
// Scala Native: `./mill rvasmcli.nativeLink`
object rvasmcli extends RVASMcliBase {
  object test extends ScalaTests with RiscvAssemblerTest { // TODO: Fix tests
    def releaseMode = ReleaseMode.Debug
  }
}

// Build rvasmcli Scala Native binary for cross-architecture depending on LLVM Triple setting below.
// Cross build for all available architectures on current OS with: `./mill rvasmclicross.__.nativeLink`
// On Mac, install LLVM using Homebrew which contains libs for amd64 and arm64
// On Linux, install "build-essential clang build-essential clang crossbuild-essential-arm64 crossbuild-essential-riscv64 crossbuild-essential-amd64 crossbuild-essential-ppc64el"
def LLVMTriples = System.getProperty("os.name").toLowerCase match {
  case os if os.contains("linux") =>
    Seq("x86_64-linux-gnu", "arm64-linux-gnu", "powerpc64le-linux-gnu", "riscv64-linux-gnu")
  case os if os.contains("mac") =>
    Seq("x86_64-apple-darwin20.3.0", "arm64-apple-darwin20.3.0")
}
object rvasmclicross extends Cross[RVASMCLI](LLVMTriples)
trait RVASMCLI extends Cross.Module[String] with RVASMcliBase {
  def nativeTarget = Some(crossValue)
}

// This trait allows building Scala Native for current platform and cross-building for other platforms
trait RVASMcliBase extends ScalaNativeModule with TpolecatModule with ScalafixModule with ScalafmtModule {
  def scalaVersion       = scala3
  def scalaNativeVersion = scalaNativeVersions.head._2
  def moduleDeps         = Seq(riscvassembler.native(scala3, scalaNativeVersions.head._2))
  def millSourcePath     = build.millSourcePath / "rvasmcli"
  def ivyDeps = Agg(
    ivy"com.lihaoyi::os-lib::${versions.oslib}",
    ivy"com.lihaoyi::mainargs::${versions.mainargs}",
  )
  def logLevel    = NativeLogLevel.Info
  def releaseMode = ReleaseMode.ReleaseFast
  if (System.getProperty("os.name").toLowerCase == "linux") {
    def nativeLTO            = LTO.Thin
    def nativeLinkingOptions = Array("-static", "-fuse-ld=lld")
  }
}

// Aggregate reports for all projects
object scoverage extends ScoverageReport {
  override def scalaVersion     = scala3
  override def scoverageVersion = versions.scoverage
}

// -----------------------------------------------------------------------------
// Command Aliases
// -----------------------------------------------------------------------------
object MyAliases extends Aliases {
  def lint = alias(
    s"riscvassembler.jvm[$scala3].fix",
    "rvasmcli.fix",
    "mill.scalalib.scalafmt.ScalafmtModule/reformatAll __.sources",
  )
  def deps     = alias("mill.scalalib.Dependency/showUpdates")
  def checkfmt = alias("mill.scalalib.scalafmt.ScalafmtModule/checkFormatAll __.sources")
  def fmt      = alias("mill.scalalib.scalafmt.ScalafmtModule/reformatAll __.sources")
  def coverage = alias(
    s"riscvassembler.jvm[$scala3].test",
    "rvasmcli.test",
    "scoverage.htmlReportAll",
    "scoverage.xmlReportAll",
    "scoverage.consoleReportAll",
  )
  def pub      = alias("io.kipp.mill.ci.release.ReleaseModule/publishAll")
  def publocal = alias("riscvassembler.__.publishLocal")
  def cli      = alias("show rvasmcli.nativeLink")
  def testall  = alias("riscvassembler.__.test") // TODO: Add "rvasmcli.test" once fixed
  def test     = alias("riscvassembler.jvm[" + scala3 + "].test", "rvasmcli.test")
}
