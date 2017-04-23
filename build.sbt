lazy val scalametaVersion = "1.7.0"
lazy val allSettings = Seq(
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.2",
  organization := "io.delmore",
  description := "implicitly make your project more clippity with imclipitly",
  pomIncludeRepository := { _ =>
    false
  },
  licenses += ("Apache-2.0", url(
    "https://www.apache.org/licenses/LICENSE-2.0.html")),
  homepage := Some(url("http://github.com/shanedelmore/imclipitly/")),
  scmInfo := Some(
    ScmInfo(url("https://github.com/shanedelmore/imclipitly"),
            "scm:git@github.com:shanedelmore/imclipitly.git")),
  publishArtifact in Test := false,
  bintrayReleaseOnPublish := false,
  bintrayOrganization := None,
  bintrayRepository := "sbt-plugins",
  bintrayPackage := "sbt-imclipitly"
)

lazy val `imclipitly-core` = project
  .settings(
    allSettings,
    scalaVersion := "2.12.2",
    libraryDependencies += "com.github.alexarchambault" %% "case-app" % "1.2.0-M3",
    libraryDependencies += "org.scalameta" %% "scalameta" % scalametaVersion,
    // soon unnecessary dependency after https://github.com/scalameta/scalameta/pull/808
    libraryDependencies += "org.scalameta" %% "scalahost-nsc" % scalametaVersion cross CrossVersion.full,
    libraryDependencies += "org.json4s" %% "json4s-native" % "3.5.0",
    libraryDependencies += "com.softwaremill.clippy" %% "plugin" % "0.5.2",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.3" % Test
  )

lazy val `imclipitly-sbt` = project
  .configs(IntegrationTest)
  .settings(
    moduleName := "sbt-imclipitly", // sbt plugin convention to use sbt-*
    allSettings,
    scriptedSettings,
    buildInfoSettings,
    Defaults.itSettings,
    addSbtPlugin("org.scalameta" %% "sbt-scalahost" % scalametaVersion),
    // to run: imclipitly/it:test
    test.in(IntegrationTest) := {
      scripted
        .toTask("")
        .dependsOn(
          publishLocal.in(`imclipitly-core`)
        )
        .value
    },
    sbtPlugin := true,
    scalaVersion := "2.10.6"
  )
  .enablePlugins(BuildInfoPlugin) // used to inject version number

// boilerplate
lazy val buildInfoSettings: Seq[Def.Setting[_]] = Seq(
  buildInfoKeys := Seq[BuildInfoKey](
    name,
    version,
    "coreScalaVersion" -> scalaVersion.in(`imclipitly-core`).value
  ),
  buildInfoObject := "ImclipitlyBuildInfo",
  buildInfoPackage := "imclipitly"
)

lazy val scriptedSettings =
  ScriptedPlugin.scriptedSettings ++ Seq(
    scriptedLaunchOpts := Seq(
      "-Dplugin.version=" + version.value,
      "-Dscalafmt.scripted=true",
      "-XX:MaxPermSize=256m",
      "-Xmx2g",
      "-Xss2m"
    ),
    scriptedBufferLog := false
  )
lazy val noPublish = Seq(
  publish := {},
  publishArtifact := false,
  publishLocal := {}
)
noPublish
