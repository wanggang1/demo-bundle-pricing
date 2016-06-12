name := "demo-bundle-pricing"

organization := "org.gwgs"

version := "1.0"

scalaVersion := "2.11.7"

//default is true, which adds Scala version in output paths and artifacts for cross-build
crossPaths := false

libraryDependencies ++= {
	val akkaVer = "2.4.3"
	val sprayVer = "1.3.3"
    
	Seq(
	  "org.scala-stm"               %% "scala-stm"                   % "0.7"     % Compile,
	  "com.novus"                   %% "salat"                       % "1.9.9"   % Compile,
	  "com.squants"                 %% "squants"                     % "0.5.3"   % Compile,
	  "com.typesafe.akka"           %% "akka-actor"                  % akkaVer   % Compile,
	  "com.typesafe.scala-logging"  %% "scala-logging"               % "3.1.0"   % Compile,
	  "io.spray"                    %% "spray-can"                   % sprayVer  % Compile,
  	  "io.spray"                    %% "spray-routing"               % sprayVer  % Compile,
  	  "com.typesafe.play"           %% "play-json"                   % "2.3.10"  % Compile,
  	  "org.scalaz"                  %% "scalaz-core"                 % "7.1.2"   % Compile,
	  "com.typesafe.akka"           %% "akka-slf4j"                  % akkaVer   % Runtime,
	  "org.slf4j"                    % "log4j-over-slf4j"            % "1.7.13"  % Runtime,
	  "org.slf4j"                    % "slf4j-simple"           	 % "1.7.13"  % Runtime,
	  "org.scalatest"               %% "scalatest"                   % "2.2.6"   % Test,
	  "org.scalamock"               %% "scalamock-scalatest-support" % "3.2.2"   % Test,
	  "com.typesafe.akka"           %% "akka-testkit"                % akkaVer   % Test,
	  "io.spray"                    %% "spray-testkit"               % sprayVer  % Test,
	  "de.flapdoodle.embed"          % "de.flapdoodle.embed.mongo"   % "1.50.0"  % Test
	)
}

//Generate a build info object in the source for querying build info at runtime
enablePlugins(BuildInfoPlugin)
buildInfoOptions += BuildInfoOption.ToMap
buildInfoPackage := "com.bundlepricing"
buildInfoKeys ++= Seq[BuildInfoKey](BuildInfoKey.action("buildDate")(new java.util.Date),
  BuildInfoKey.action("commit")(Process("git rev-parse HEAD").lines.head))
  
resolvers += Classpaths.sbtPluginReleases

initialCommands in console := "import scala.concurrent.Future, scala.concurrent.ExecutionContext.Implicits.global"
