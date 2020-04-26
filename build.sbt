name := "custom-bingo-builder"
organization := "dev.marksman"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    publishArtifact in(Compile, packageDoc) := false,
    publishArtifact in packageDoc := false,
    sources in(Compile, doc) := Seq.empty
  )
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)

scalaVersion := "2.13.2"

libraryDependencies += guice
libraryDependencies += "org.jsoup" % "jsoup" % "1.13.1"
libraryDependencies += "com.googlecode.owasp-java-html-sanitizer" % "owasp-java-html-sanitizer" % "20191001.1"
libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"
libraryDependencies += "dev.zio" %% "zio" % "1.0.0-RC18-2"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "dev.marksman.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "dev.marksman.binders._"
