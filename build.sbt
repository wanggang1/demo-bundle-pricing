name := "demo-bundle-pricing"

organization := "org.gwgs"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scala-stm"   %% "scala-stm"   % "0.7"     % Compile,
  "org.scalatest"   %% "scalatest"   % "2.2.6"   % Test
)

resolvers += Classpaths.sbtPluginReleases

