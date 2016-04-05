name := "demo-bundle-pricing"

organization := "org.gwgs"

version := "1.0"

scalaVersion := "2.11.7"

//default is true, which adds Scala version in output paths and artifacts for cross-build
crossPaths := false

libraryDependencies ++= Seq(
  "org.scala-stm"   %% "scala-stm"   % "0.7"     % Compile,
  "org.scalatest"   %% "scalatest"   % "2.2.6"   % Test
)

resolvers += Classpaths.sbtPluginReleases

