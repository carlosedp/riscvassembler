ThisBuild / organization := "com.carlosedp"
ThisBuild / version := "0.4.0"
ThisBuild / scalaVersion := "2.13.6"
ThisBuild / homepage := Some(url("https://carlosedp.com"))
ThisBuild / licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/carlosedp/scalautils"), "git@github.com:carlosedp/scalautils.git")
)
ThisBuild / developers := List(
  Developer("carlosedp", "carlosedp", "carlosedp@gmail.com", url("https://github.com/carlosedp"))
)
semanticdbEnabled := true
semanticdbVersion := "4.4.27" //scalafixSemanticdb.revision // Force version due to compatibility issues
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"

lazy val root = (project in file("."))
  .settings(
    name := "scalautils",
    crossScalaVersions := Seq("2.11.12", "2.12.13", "2.13.6", "3.0.2"),
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true),
    publishMavenStyle := true
  )

ThisBuild / publishTo := {
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

addCommandAlias("fix", "all Compile / scalafix Test / scalafix")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")
addCommandAlias("lint", "fmt;fix")
addCommandAlias("deps", "dependencyUpdates")
addCommandAlias("xtest", "+test")
addCommandAlias("release", "+publishSigned;sonatypeBundleRelease")

scalacOptions ++= Seq(
  "-deprecation",
  "-explaintypes",
  "-unchecked",
  "-feature",
  "-Ywarn-value-discard",
  "-Ywarn-dead-code",
  "-Ywarn-unused"
)
