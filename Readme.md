# Scala Utils

A generic library adding some frequently used methods in Scala and Chisel development.

## Adding to your project

### SBT

When using SBT, add the following lines to your `build.sbt` file.

```scala
// Import libraries
externalResolvers += "Scalautils" at "https://maven.pkg.github.com/carlosedp/scalautils"
libraryDependencies += "com.carlosedp" %% "scalautils" % "0.1.0"
```

Replace `0.1.0` with latest version.

### Mill

If you use `mill` build tool, I recommend adding the following way to your `build.sc`:

```scala
// Create a trait
trait HasGithubLibs extends CrossSbtModule {
  override def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"com.carlosedp::scalautils:0.1.0"
  )
  def repositories = super.repositories ++ Seq(
    MavenRepository("https://maven.pkg.github.com/carlosedp/scalautils")
  )
}
...
// And add it to your object
object myobject
    extends CrossSbtModule
    with HasGithubLibs
    {
        ...
    }
```

Or override directly in your object `repositories` and `ivyDeps` functions.
