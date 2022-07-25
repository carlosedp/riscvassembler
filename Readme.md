# Scala Utils

A library adding some frequently used methods in Scala and Chisel development.

For more information, check the [scaladoc](https://www.javadoc.io/doc/com.carlosedp/scalautils_2.13/latest/com/carlosedp/scalautils/index.html).


[![Scala version support](https://index.scala-lang.org/carlosedp/scalautils/scalautils/latest-by-scala-version.svg?color=blue)](https://index.scala-lang.org/carlosedp/scalautils/scalautils)
[![Scaladoc](https://www.javadoc.io/badge/com.carlosedp/scalautils_2.13.svg?color=blue&label=Scaladoc)](https://javadoc.io/doc/com.carlosedp/scalautils_2.13/latest)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.carlosedp/scalautils_2.13/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.carlosedp/scalautils_2.13)
[![Sonatype Snapshots](https://img.shields.io/nexus/s/com.carlosedp/scalautils_2.13?server=https%3A%2F%2Fs01.oss.sonatype.org)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/carlosedp/)
<br>
[![Scala CI](https://github.com/carlosedp/scalautils/actions/workflows/scala.yml/badge.svg)](https://github.com/carlosedp/scalautils/actions/workflows/scala.yml)
[![codecov](https://codecov.io/gh/carlosedp/scalautils/branch/main/graph/badge.svg?token=YNEKF3OO04)](https://codecov.io/gh/carlosedp/scalautils)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=carlosedp_scalautils&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=carlosedp_scalautils)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-green.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![Mergify Status](https://img.shields.io/endpoint.svg?url=https://api.mergify.com/v1/badges/carlosedp/scalautils&style=flat)](https://mergify.com)


## Adding to your project

### SBT

When using SBT, add the following lines to your `build.sbt` file.

```scala
// Import libraries
libraryDependencies += "com.carlosedp" %% "scalautils" % "0.10.2"
```

If you plan to use the `-SNAPSHOT` versions, add the new Sonatype repository to your `build.sbt` resolvers:

```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases"),
  "Sonatype New OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots"
)

// and change the dependency to latest SNAPSHOT as:
libraryDependencies += "com.carlosedp" %% "scalautils" % "0.11.0-SNAPSHOT"
```

Confirm the latest versions displayed on the badges at the top of this readme for both stable and snapshot (without the leading "v").

### Mill

If you use `mill` build tool, I recommend adding the following way to your `build.sc`:

```scala
import coursier.MavenRepository

// Add to your ivyDeps
def ivyDeps = Agg(
  ivy"com.carlosedp::scalautils:0.10.2"
  ...
)

// And add the snapshot resolver if using it
def repositoriesTask = T.task { super.repositoriesTask() ++ Seq(
  MavenRepository("https://s01.oss.sonatype.org/content/repositories/snapshots")
) }

def ivyDeps = Agg(
  ivy"com.carlosedp::scalautils:0.11.0-SNAPSHOT"
  ...
)
```

The library has been published to Maven Central thru Sonatype:

* <https://search.maven.org/artifact/com.carlosedp/scalautils>
* <https://mvnrepository.com/artifact/com.carlosedp/scalautils>
