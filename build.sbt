name := "demo-bundle-pricing"

organization := "org.gwgs"

version := "1.0"

scalaVersion := "2.11.7"

//default is true, which adds Scala version in output paths and artifacts for cross-build
crossPaths := false

libraryDependencies ++= Seq(
  "org.scala-stm"       %% "scala-stm"                  % "0.7"     % Compile,
  "com.novus"           %% "salat"                      % "1.9.9"   % Compile,
  "com.squants"         %% "squants"                    % "0.5.3"   % Compile,
  "org.scalatest"       %% "scalatest"                  % "2.2.6"   % Test,
  "de.flapdoodle.embed"  % "de.flapdoodle.embed.mongo"  % "1.50.0"  % Test
)

resolvers += Classpaths.sbtPluginReleases

initialCommands in console := "import scala.concurrent.Future, scala.concurrent.ExecutionContext.Implicits.global"
