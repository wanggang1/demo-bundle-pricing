name := "demo-bundle-pricing"

organization := "org.gwgs"

version := "1.0"

scalaVersion := "2.11.7"

//default is true, which adds Scala version in output paths and artifacts for cross-build
crossPaths := false

libraryDependencies ++= {
	val akkaVer = "2.4.3"
    
	Seq(
	  "org.scala-stm"               %% "scala-stm"                  % "0.7"     % Compile,
	  "com.novus"                   %% "salat"                      % "1.9.9"   % Compile,
	  "com.squants"                 %% "squants"                    % "0.5.3"   % Compile,
	  "com.typesafe.akka"           %% "akka-actor"                 % akkaVer   % Compile,
	  "com.typesafe.scala-logging"  %% "scala-logging"              % "3.1.0"   % Compile,
	  "com.typesafe.akka"           %% "akka-slf4j"                 % akkaVer   % Runtime,
	  "org.slf4j"                    % "log4j-over-slf4j"           % "1.7.13"  % Runtime,
	  "org.scalatest"               %% "scalatest"                  % "2.2.6"   % Test,
	  "com.typesafe.akka"           %% "akka-testkit"               % akkaVer   % Test,
	  "de.flapdoodle.embed"          % "de.flapdoodle.embed.mongo"  % "1.50.0"  % Test
	)
}

resolvers += Classpaths.sbtPluginReleases

initialCommands in console := "import scala.concurrent.Future, scala.concurrent.ExecutionContext.Implicits.global"
