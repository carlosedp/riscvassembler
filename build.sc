import mill._
import mill.scalalib._
import mill.scalalib.publish._
import mill.scalalib.api.Util.isScala3
import mill.scalanativelib._, mill.scalanativelib.api._
import mill.scalajslib._,     mill.scalajslib.api._
import scalafmt._

// Plugins
import $ivy.`com.lihaoyi::mill-contrib-scoverage:`
import mill.contrib.scoverage.{ScoverageModule, ScoverageReport}
import $ivy.`com.goyeau::mill-scalafix::0.2.10`
import com.goyeau.mill.scalafix.ScalafixModule
import $ivy.`io.chris-kipp::mill-ci-release::0.1.1`
import io.kipp.mill.ci.release.CiReleaseModule
import $ivy.`de.tototec::de.tobiasroeser.mill.vcs.version::0.2.0`
import de.tobiasroeser.mill.vcs.version.VcsVersion
import $ivy.`io.github.davidgregory084::mill-tpolecat::0.3.1`
import io.github.davidgregory084.TpolecatModule

val scalaVersions       = Seq("2.12.17", "2.13.9", "3.1.3")
val scalaNativeVersions = scalaVersions.map((_, "0.4.7"))
val scalaJsVersions     = scalaVersions.map((_, "1.11.0"))

object versions {
  val scalatest       = "3.2.13"
  val oslib           = "0.8.1"
  val organizeimports = "0.6.0"
  val semanticdb      = "4.5.13"
  val mainargs        = "0.3.0"
  val scoverage       = "2.0.5"
}

object riscvassembler extends Module {
  object jvm extends Cross[RiscvAssemblerJVMModule](scalaVersions: _*)
  class RiscvAssemblerJVMModule(val crossScalaVersion: String) extends RiscvAssemblerModule with RiscvAssemblerPublish {
    def millSourcePath = super.millSourcePath / _root_.os.up
    object test extends Tests with RiscvAssemblerTest {
      def scalaVersion = crossScalaVersion
    }
  }

  object native extends Cross[RiscvAssemblerNativeModule](scalaNativeVersions: _*)
  class RiscvAssemblerNativeModule(val crossScalaVersion: String, crossScalaNativeVersion: String)
    extends RiscvAssemblerModule
    with RiscvAssemblerPublish
    with ScalaNativeModule {
    def millSourcePath     = super.millSourcePath / _root_.os.up / os.up
    def scalaNativeVersion = crossScalaNativeVersion
    object test extends Tests with RiscvAssemblerTest {
      def nativeLinkStubs = true
    }
  }

  object scalajs extends Cross[RiscvAssemblerScalajsModule](scalaJsVersions: _*)
  class RiscvAssemblerScalajsModule(val crossScalaVersion: String, crossScalaJsVersion: String)
    extends RiscvAssemblerModule
    with RiscvAssemblerPublish
    with ScalaJSModule {
    def millSourcePath = super.millSourcePath / _root_.os.up / os.up
    def scalaJSVersion = crossScalaJsVersion
    def ivyDeps        = Agg(ivy"org.scala-js::scalajs-dom::2.2.0")

    def scalaJSUseMainModuleInitializer = true
    // def moduleKind       = T(ModuleKind.ESModule)
    def moduleKind = T(ModuleKind.CommonJSModule)
    object test extends Tests with RiscvAssemblerTest {
      def jsEnvConfig = T(JsEnvConfig.JsDom())
    }
  }
}

object rvasmcli extends RiscvAssemblerModule with ScalaNativeModule {
  def millSourcePath = super.millSourcePath / this.toString()
  def sources = T.sources(
    super.millSourcePath / "riscvassembler" / "src",
    super.millSourcePath / "rvasmcli" / "src",
  )
  def crossScalaVersion  = scalaVersions.find(_.startsWith("3.")).get
  def scalaNativeVersion = scalaNativeVersions(0)._2
  def mainClass          = Some("com.carlosedp.rvasmcli.Main")
  def logLevel           = NativeLogLevel.Info
  def releaseMode        = ReleaseMode.Debug
  object test extends Tests with RiscvAssemblerTest {}
}

// Create a project on pinned Scala version for coverage, fmt and fix
object linter extends ScoverageReport with ScalafixModule with ScalafmtModule {
  val scala            = "2.13"
  def scalaVersion     = scalaVersions.find(_.startsWith(scala)).get
  def scoverageVersion = versions.scoverage
  def scalafixIvyDeps  = Agg(ivy"com.github.liancheng::organize-imports:${versions.organizeimports}")
  def scalacPluginIvyDeps = T {
    super.scalacPluginIvyDeps() ++ Agg(ivy"org.scalameta:::semanticdb-scalac:${versions.semanticdb}")
  }

  object riscvassembler extends RiscvAssemblerModule with ScoverageModule {
    def millSourcePath    = super.millSourcePath / os.up / "riscvassembler"
    def scoverageVersion  = versions.scoverage
    def crossScalaVersion = scalaVersions.find(_.startsWith(scala)).get
    object test extends ScoverageTests with RiscvAssemblerTest {}
  }
  object rvasmcli extends RiscvAssemblerModule with ScoverageModule {
    def millSourcePath    = super.millSourcePath / os.up / "rvasmcli"
    def scoverageVersion  = versions.scoverage
    def crossScalaVersion = scalaVersions.find(_.startsWith(scala)).get
    def sources = T.sources(
      millSourcePath / os.up / "riscvassembler" / "src",
      millSourcePath / os.up / "rvasmcli" / "src",
    )
    object test extends ScoverageTests with RiscvAssemblerTest {}
  }
}

trait RiscvAssemblerModule extends CrossScalaModule with TpolecatModule {
  def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"com.lihaoyi::os-lib::${versions.oslib}",
    ivy"com.lihaoyi::mainargs::${versions.mainargs}",
  )
}

trait RiscvAssemblerPublish extends CrossScalaModule with CiReleaseModule {
  def artifactName = "riscvassembler"
  def publishVersion: T[String] = T {
    val isTag = T.ctx().env.get("GITHUB_REF").exists(_.startsWith("refs/tags"))
    println("Release is tag:", isTag)
    val state = VcsVersion.vcsState()
    if (state.commitsSinceLastTag == 0 && isTag) {
      state.lastTag.get.replace("v", "")
    } else {
      val v = state.lastTag.get.split('.')
      s"${v(0)}.${(v(1).toInt) + 1}".replace("v", "") + "-SNAPSHOT"
    }
  }
  def pomSettings = PomSettings(
    description = "RiscvAssembler is a RISC-V assembler library to be used on Scala and Chisel HDL projects.",
    organization = "com.carlosedp",
    url = "https://github.com/carlosedp/riscvassembler",
    licenses = Seq(License.MIT),
    versionControl = VersionControl.github("carlosedp", "RiscvAssembler"),
    developers = Seq(
      Developer("carlosedp", "Carlos Eduardo de Paula", "https://github.com/carlosedp"),
    ),
  )
  override def sonatypeUri:         String = "https://s01.oss.sonatype.org/service/local"
  override def sonatypeSnapshotUri: String = "https://s01.oss.sonatype.org/content/repositories/snapshots"
}

trait RiscvAssemblerTest extends ScalaModule with TestModule.ScalaTest {
  def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"org.scalatest::scalatest::${versions.scalatest}",
  )
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
  runTasks(Seq("__.fix", "mill.scalalib.scalafmt.ScalafmtModule/reformatAll __.sources"))
}
def deps(implicit ev: eval.Evaluator) = T.command {
  mill.scalalib.Dependency.showUpdates(ev)
}
def coverage(implicit ev: eval.Evaluator) = T.command {
  runTasks(Seq("linter.__.test", "linter.htmlReportAll", "linter.xmlReportAll", "linter.consoleReportAll"))
}
def pub(implicit ev: eval.Evaluator) = T.command {
  runTasks(Seq("io.kipp.mill.ci.release.ReleaseModule/publishAll"))
}
def bin(implicit ev: eval.Evaluator) = T.command {
  runTasks(Seq("show rvasmcli.nativeLink"))
}
def testall(implicit ev: eval.Evaluator) = T.command {
  runTasks(Seq("riscvassembler.__.test", "rvasmcli.test"))
}
def test(implicit ev: eval.Evaluator) = T.command {
  val scalaver = scalaVersions.find(_.startsWith("2.13")).get
  runTasks(Seq("riscvassembler.jvm[" + scalaver + "].test", "rvasmcli.test"))
}
