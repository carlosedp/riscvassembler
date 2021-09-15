name := "scalautils"
organization := "com.carlosedp"
version := "0.4.0"
scalaVersion := "2.13.6"

homepage := Some(url("https://carlosedp.com"))
ThisBuild / licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
scmInfo := Some(ScmInfo(url("https://github.com/carlosedp/scalautils"), "git@github.com:carlosedp/scalautils.git"))
developers := List(Developer("carlosedp", "carlosedp", "carlosedp@gmail.com", url("https://github.com/carlosedp")))
semanticdbEnabled := true
semanticdbVersion := "4.4.27"

crossScalaVersions := Seq("2.11.12", "2.12.13", "2.13.6")
publishConfiguration := publishConfiguration.value.withOverwrite(true)
publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)
publishMavenStyle := true

libraryDependencies += "org.scalatest"                     %% "scalatest"        % "3.2.9"
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"

addCommandAlias("fix", "all compile:scalafix test:scalafix")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll;all compile:scalafix test:scalafix")
addCommandAlias("deps", "dependencyUpdates")
addCommandAlias("xtest", "+test")
addCommandAlias("release", "+publishSigned;sonatypeBundleRelease")

scalacOptions ++= Seq(
  "-Ywarn-value-discard",
  "-Ywarn-dead-code",
  "-Ywarn-unused"
)

sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / publishTo := {
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
