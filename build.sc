import mill._
import mill.scalalib._
import mill.scalalib.publish._
import mill.scalalib.api.Util.isScala3
import mill.scalanativelib._, mill.scalanativelib.api._
import mill.scalajslib._, mill.scalajslib.api._
import scalafmt._

// Plugins
import $ivy.`com.lihaoyi::mill-contrib-scoverage:`
import mill.contrib.scoverage.ScoverageModule
import $ivy.`com.goyeau::mill-scalafix::0.2.10`
import com.goyeau.mill.scalafix.ScalafixModule
import $ivy.`io.chris-kipp::mill-ci-release::0.1.1`
import io.kipp.mill.ci.release.CiReleaseModule
import $ivy.`de.tototec::de.tobiasroeser.mill.vcs.version::0.2.0`
import de.tobiasroeser.mill.vcs.version.VcsVersion
import $ivy.`io.github.davidgregory084::mill-tpolecat::0.3.1`
import io.github.davidgregory084.TpolecatModule

val scalaVersions       = Seq("2.12.17", "2.13.8", "3.2.0")
val scalaNativeVersions = scalaVersions.map((_, "0.4.7"))

object versions {
  val scalatest       = "3.2.13"
  val oslib           = "0.8.1"
  val organizeimports = "0.6.0"
  val semanticdb      = "4.5.13"
  val mainargs        = "0.2.3"
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
}

object rvasmcli extends RiscvAssemblerModule with ScalaNativeModule {
  def sources = T.sources(super.millSourcePath / "riscvassembler" / "src", super.millSourcePath / this.toString / "src")
  def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"com.lihaoyi::mainargs::${versions.mainargs}"
  )
  def crossScalaVersion  = "2.13.8"
  def scalaNativeVersion = "0.4.7"
  def mainClass          = Some("com.carlosedp.rvasmcli.Main")
  def logLevel           = NativeLogLevel.Info
  def releaseMode        = ReleaseMode.ReleaseFast
}

// Pin coverage project to specific Scala version
object cover extends RiscvAssemblerModule with ScoverageModule {
  def millSourcePath    = super.millSourcePath / "riscvassembler"
  def crossScalaVersion = scalaVersions.find(_.contains("2.13")).get
  def scoverageVersion  = "1.4.11"
  object test extends ScoverageTests with RiscvAssemblerTest {}
}

trait RiscvAssemblerModule extends CrossScalaModule with TpolecatModule with ScalafixModule with ScalafmtModule {
  def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"com.lihaoyi::os-lib::${versions.oslib}"
  )
  def scalafixIvyDeps = Agg(ivy"com.github.liancheng::organize-imports:${versions.organizeimports}")
  def scalacPluginIvyDeps = T {
    if (!isScala3(crossScalaVersion)) {
      super.scalacPluginIvyDeps() ++ Agg(ivy"org.scalameta:::semanticdb-scalac:${versions.semanticdb}")
    } else super.scalacPluginIvyDeps()
  }
}

trait RiscvAssemblerTest extends ScalaModule with TestModule.ScalaTest {
  def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"com.lihaoyi::os-lib::${versions.oslib}",
    ivy"org.scalatest::scalatest::${versions.scalatest}"
  )
}

trait RiscvAssemblerPublish extends CrossScalaModule with CiReleaseModule {
  def artifactName = "riscvassembler"
  def publishVersion: T[String] = T {
    val state = VcsVersion.vcsState()
    if (state.commitsSinceLastTag == 0) {
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
      Developer("carlosedp", "Carlos Eduardo de Paula", "https://github.com/carlosedp")
    )
  )
  override def sonatypeUri:         String = "https://s01.oss.sonatype.org/service/local"
  override def sonatypeSnapshotUri: String = "https://s01.oss.sonatype.org/content/repositories/snapshots"
}

// Toplevel commands
def runTasks(t: Seq[String])(implicit ev: eval.Evaluator) = T.task {
  mill.main.MainModule.evaluateTasks(
    ev,
    t.flatMap(x => x +: Seq("+")).flatMap(x => x.split(" ")).dropRight(1),
    mill.define.SelectMode.Separated
  )(identity)
}
def lint(implicit ev: eval.Evaluator) = T.command {
  runTasks(Seq("__.fix", "mill.scalalib.scalafmt.ScalafmtModule/reformatAll __.sources"))
}
def deps(implicit ev: eval.Evaluator) = T.command {
  mill.scalalib.Dependency.showUpdates(ev)
}
def coverage(implicit ev: eval.Evaluator) = T.command {
  runTasks(Seq("cover.test", "cover.scoverage.htmlReport"))
}
def pub(implicit ev: eval.Evaluator) = T.command {
  runTasks(Seq("io.kipp.mill.ci.release.ReleaseModule/publishAll"))
}
def bin(implicit ev: eval.Evaluator) = T.command {
  runTasks(Seq("show rvasmcli.nativeLink"))
}
