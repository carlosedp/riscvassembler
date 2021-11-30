# Scala Utils

A library adding some frequently used methods in Scala and Chisel development.

For more information, check the [scaladoc](https://www.javadoc.io/doc/com.carlosedp/scalautils_2.13/latest/com/carlosedp/scalautils/index.html).

[![codecov](https://codecov.io/gh/carlosedp/scalautils/branch/main/graph/badge.svg?token=YNEKF3OO04)](https://codecov.io/gh/carlosedp/scalautils)
[![Scala CI](https://github.com/carlosedp/scalautils/actions/workflows/scala.yml/badge.svg)](https://github.com/carlosedp/scalautils/actions/workflows/scala.yml)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![Mergify Status][mergify-status]][mergify]

[mergify]: https://mergify.com
[mergify-status]: https://img.shields.io/endpoint.svg?url=https://api.mergify.com/v1/badges/carlosedp/scalautils&style=flat


## Adding to your project

### SBT

When using SBT, add the following lines to your `build.sbt` file.

```scala
// Import libraries
libraryDependencies += "com.carlosedp" %% "scalautils" % "0.7.2"
```

Replace `0.7.2` with latest version.

If you plan to use the `-SNAPSHOT` versions, add the new Sonatype repository to your `build.sbt` resolvers:

```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases"),
  "Sonatype New OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots"
)
```

### Mill

If you use `mill` build tool, I recommend adding the following way to your `build.sc`:

```scala
import coursier.MavenRepository

// Add to your ivyDeps
def ivyDeps = Agg(
  ivy"com.carlosedp::scalautils:0.7.2"
  ...
)

// And add the snapshot resolver
def repositoriesTask = T.task { super.repositoriesTask() ++ Seq(
  MavenRepository("https://s01.oss.sonatype.org/content/repositories/snapshots")
) }
```

The library has been published to Maven Central thru Sonatype:

* <https://search.maven.org/artifact/com.carlosedp/scalautils>
* <https://mvnrepository.com/artifact/com.carlosedp/scalautils>
