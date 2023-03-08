import org.scalajs.linker.interface.ESVersion
import org.scalajs.linker.interface.OutputPatterns
import org.scalajs.linker.interface.ModuleSplitStyle

lazy val baseSettings = Seq(
  organization := "xyz.bluepitaya",
  scalaVersion := "2.13.8",
  version := "1.0"
)

lazy val root = (project in file("."))
  .settings(baseSettings)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "laminar-drag-logic",
    scalacOptions :=
      Seq(
        // "-Xlint"
      ),
    libraryDependencies += "com.raquo" %%% "laminar" % "15.0.0-M7",
    libraryDependencies += "xyz.bluepitaya" %%% "common-utils" % "1.0",
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.15" % Test
  )

lazy val example = (project in file("example"))
  .dependsOn(root)
  .settings(baseSettings)
  .settings(
    name := "laminar-drag-logic-example",
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withOutputPatterns(OutputPatterns.fromJSFile("%s.js"))
        .withESFeatures(_.withESVersion(ESVersion.ES2021))
    },
    scalaJSUseMainModuleInitializer := true,
    Compile / fastLinkJS / scalaJSLinkerOutputDirectory :=
      baseDirectory.value / "ui/sccode/",
    Compile / fullLinkJS / scalaJSLinkerOutputDirectory :=
      baseDirectory.value / "ui/sccode/"
  )
  .enablePlugins(ScalaJSPlugin)
