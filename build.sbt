organization := "com.carlosedp"
name := "scalautils"
version := "0.1.0"
scalaVersion := "2.12.10"
semanticdbEnabled := true
semanticdbVersion := scalafixSemanticdb.revision

crossScalaVersions := Seq("2.11.12", "2.12.10", "2.13.4")

githubOwner := "carlosedp"
githubRepository := "scalautils"

libraryDependencies += "org.scalatest"                     %% "scalatest"        % "3.2.2"
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"

addCommandAlias("fix", "all compile:scalafix test:scalafix")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")

scalacOptions ++= Seq(
  "-Ywarn-value-discard",
  "-Ywarn-dead-code",
  "-Ywarn-unused"
)
