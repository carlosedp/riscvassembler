# Scala Utils

A generic library adding some frequently used methods in Scala and Chisel development.

For details, check the  [scaladoc](https://www.javadoc.io/doc/com.carlosedp/scalautils_2.13/latest/com/carlosedp/scalautils/index.html).

[![codecov](https://codecov.io/gh/carlosedp/scalautils/branch/main/graph/badge.svg?token=YNEKF3OO04)](https://codecov.io/gh/carlosedp/scalautils)

## Adding to your project

### SBT

When using SBT, add the following lines to your `build.sbt` file.

```scala
// Import libraries
libraryDependencies += "com.carlosedp" %% "scalautils" % "0.5.0"
```

Replace `0.5.0` with latest version.

### Mill

If you use `mill` build tool, I recommend adding the following way to your `build.sc`:

```scala
// Add to your ivyDeps

def ivyDeps = Agg(
  ivy"com.carlosedp::scalautils:0.5.0"
  ...
)
```

The library has been published to Maven Central thru Sonatype:

* <https://search.maven.org/artifact/com.carlosedp/scalautils>
* <https://mvnrepository.com/artifact/com.carlosedp/scalautils>