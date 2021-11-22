import ReleaseTransformations._

ThisBuild / organization := "com.carlosedp"
ThisBuild / description := "scalautils contains misc utility functions to be used on Scala and Chisel projects"
ThisBuild / homepage := Some(url("https://carlosedp.com"))
ThisBuild / licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/carlosedp/scalautils"), "git@github.com:carlosedp/scalautils.git")
)
ThisBuild / developers := List(
  Developer("carlosedp", "Carlos Eduardo de Paula", "carlosedp@gmail.com", url("https://github.com/carlosedp"))
)
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"
Global / semanticdbEnabled := true
Global / semanticdbVersion := "4.4.28" //scalafixSemanticdb.revision // Force version due to compatibility issues
Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / versionScheme := Some("early-semver")

Test / logBuffered := false

lazy val root = (project in file("."))
  .settings(
    name := "scalautils",
    scalaVersion := "2.13.6",
    crossScalaVersions := Seq("2.11.12", "2.12.13", "2.13.6", "3.1.0"),
    // Libraries
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test",
    // Sonatype publishing repository
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true),
    publishMavenStyle := true
  )

releaseCrossBuild := true
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)

ThisBuild / publishTo := {
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

addCommandAlias("com", "all compile test:compile")
addCommandAlias("rel", "reload")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll;all Compile / scalafix; Test / scalafix")
addCommandAlias("fix", "all Compile / scalafixAll; Test / scalafixAll")
addCommandAlias("lint", "fmt;fix")
addCommandAlias("deps", "dependencyUpdates")
addCommandAlias("xtest", "+test")
addCommandAlias("pub", "+publishSigned;sonatypeBundleRelease")
// To release new versions, use `sbt release`
