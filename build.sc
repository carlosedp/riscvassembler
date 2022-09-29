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
import $ivy.`com.goyeau::mill-scalafix::0.2.10`
import com.goyeau.mill.scalafix.ScalafixModule
import $ivy.`io.chris-kipp::mill-ci-release::0.1.1`
import io.kipp.mill.ci.release.CiReleaseModule
import $ivy.`de.tototec::de.tobiasroeser.mill.vcs.version::0.3.0`
import de.tobiasroeser.mill.vcs.version.VcsVersion
import $ivy.`io.github.davidgregory084::mill-tpolecat::0.3.1`
import io.github.davidgregory084.TpolecatModule
import $ivy.`com.lihaoyi::mill-contrib-buildinfo:`
import mill.contrib.buildinfo.BuildInfo

val scala212            = "2.12.17"
val scala213            = "2.13.9"
val scala3              = "3.2.0"
val scalaVersions       = Seq(scala212, scala213, scala3)
val scalaNativeVersions = scalaVersions.map((_, "0.4.7"))
val scalaJsVersions     = scalaVersions.map((_, "1.11.0"))

object versions {
  val scalatest       = "3.2.14"
  val oslib           = "0.8.1"
  val organizeimports = "0.6.0"
  val semanticdb      = "4.5.13"
  val mainargs        = "0.3.0"
  val scoverage       = "2.0.5"
  val scalajsdom      = "2.3.0"
}

object riscvassembler extends Module {
  object jvm extends Cross[RiscvAssemblerJVMModule](scalaVersions: _*)
  class RiscvAssemblerJVMModule(val crossScalaVersion: String)
    extends RiscvAssemblerModule
    with RiscvAssemblerPublish
    with ScoverageModule {
    def millSourcePath   = super.millSourcePath / os.up
    def scoverageVersion = versions.scoverage
    object test extends ScoverageTests with RiscvAssemblerTest {}
  }

  object native extends Cross[RiscvAssemblerNativeModule](scalaNativeVersions: _*)
  class RiscvAssemblerNativeModule(val crossScalaVersion: String, crossScalaNativeVersion: String)
    extends RiscvAssemblerModule
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
    extends RiscvAssemblerModule
    with RiscvAssemblerPublish
    with ScalaJSModule {
    def millSourcePath = super.millSourcePath / os.up / os.up
    def scalaJSVersion = crossScalaJsVersion
    def ivyDeps        = Agg(ivy"org.scala-js::scalajs-dom::${versions.scalajsdom}")

    def scalaJSUseMainModuleInitializer = true
    def moduleKind                      = T(ModuleKind.CommonJSModule)
    object test extends Tests with RiscvAssemblerTest {
      def jsEnvConfig = T(JsEnvConfig.JsDom())
    }
  }
}

object rvasmcli extends RiscvAssemblerModule with ScalaNativeModule {
  def millSourcePath     = super.millSourcePath / os.up / this.toString
  def crossScalaVersion  = scala3
  def scalaNativeVersion = scalaNativeVersions.head._2
  def moduleDeps         = Seq(riscvassembler.native(scala3, scalaNativeVersions.head._2))
  def mainClass          = Some("com.carlosedp.rvasmcli.Main")
  def logLevel           = NativeLogLevel.Info
  def releaseMode        = ReleaseMode.Debug
  object test extends Tests with RiscvAssemblerTest {}
}

// Aggregate reports for all projects
object scoverage extends ScoverageReport {
  override def scalaVersion     = scala3
  override def scoverageVersion = versions.scoverage
}

trait RiscvAssemblerModule extends CrossScalaModule with TpolecatModule with BuildInfo with ScalafixModule with ScalafmtModule {
  def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"com.lihaoyi::os-lib::${versions.oslib}",
    ivy"com.lihaoyi::mainargs::${versions.mainargs}",
  )
  def scalafixIvyDeps = Agg(ivy"com.github.liancheng::organize-imports:${versions.organizeimports}")
  def scalacPluginIvyDeps = super.scalacPluginIvyDeps() ++ (if (!isScala3(crossScalaVersion))
                                                              Agg(ivy"org.scalameta:::semanticdb-scalac:${versions.semanticdb}")
                                                            else Agg.empty)
  def artifactName = "riscvassembler"
  def publishVer: T[String] = T {
    val isTag = T.ctx().env.get("GITHUB_REF").exists(_.startsWith("refs/tags"))
    val state = VcsVersion.vcsState()
    if (state.commitsSinceLastTag == 0 && isTag) {
      state.lastTag.get.replace("v", "")
    } else {
      val v = state.lastTag.get.split('.')
      s"${v(0)}.${(v(1).toInt) + 1}".replace("v", "") + "-SNAPSHOT"
    }
  }
  def buildInfoMembers: T[Map[String, String]] = T {
    Map(
      "appName"     -> artifactName.toString,
      "appVersion"  -> publishVer(),
      "revision"    -> VcsVersion.vcsState().format(),
      "buildCommit" -> VcsVersion.vcsState().currentRevision,
      "commitDate"  -> os.proc("git", "log", "-1", "--date=format:\"%a %b %d %T %z %Y\"", "--format=\"%ad\"").call().out.trim,
      "buildDate"   -> new Date().toString,
    )
  }
  def buildInfoObjectName  = "BuildInfo"
  def buildInfoPackageName = Some("com.carlosedp.riscvassembler")
}

trait RiscvAssemblerPublish extends RiscvAssemblerModule with CiReleaseModule {
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
  runTasks(
    Seq(s"riscvassembler.jvm[$scala3].fix", "rvasmcli.fix", "mill.scalalib.scalafmt.ScalafmtModule/reformatAll __.sources"),
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
