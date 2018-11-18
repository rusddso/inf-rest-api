import com.typesafe.sbt.packager.docker._

lazy val GatlingTest = config("gatling") extend Test

name := "inf-rest-api"
version := "0.1"
organization := "com.infinera"
scalaVersion := "2.11.11"

val dependencies: Seq[ModuleID] = Seq(
  "com.netaporter" %% "scala-uri" % "0.4.14",
  "net.codingwell" %% "scala-guice" % "4.1.0",
  "com.typesafe.play" %% "play-slick" % "2.1.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.1.0",
  ws,
  "org.mariadb.jdbc" % "mariadb-java-client" % "2.3.0",
  "org.postgresql" % "postgresql" % "42.1.4",
  "com.byteslounge" %% "slick-repo" % "1.4.3",
  "net.kaliber" %% "play-s3" % "8.0.0",
//  "com.sendgrid" % "sendgrid-java" % "4.0.1",
//  "com.amazonaws" % "aws-java-sdk" % "1.11.202",
//  "com.amazonaws" % "aws-java-sdk-athena" % "1.11.202",
//  "com.google.cloud" % "google-cloud-bigquery" % "0.30.0-beta",

  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test,
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.2" % Test,
  "io.gatling" % "gatling-test-framework" % "2.2.2" % Test,
  "org.eu.acolyte" % "jdbc-scala_2.11" % "1.0.44-j7p" % "test",
  "org.mockito" % "mockito-all" % "1.10.19" % "test",
  "com.typesafe.akka" % "akka-testkit_2.11" % "2.4.18" % "test"
)

libraryDependencies ++= dependencies

dependencyOverrides ++= Set(
  "org.asynchttpclient" % "async-http-client" % "2.0.25",
  "org.apache.httpcomponents" % "httpclient" % "4.5.3",
  "com.google.guava" % "guava" % "24.1.1-android",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.9.7",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.9.7",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.7",
  "org.glassfish.jersey.core" % "jersey-common" % "2.27",
  "org.glassfish.jersey.core" % "jersey-server" % "2.27",
  "org.glassfish.jersey.core" % "jersey-client" % "2.27",
  "javax.ws.rs" % "javax.ws.rs-api" % "2.0",
  "xerces" % "xercesImpl" % "2.12.0",
  "io.spray" %% "spray-json" % "1.3.5"
)

//updateOptions := updateOptions.value.withCachedResolution(true)
routesGenerator := InjectedRoutesGenerator

// play project
lazy val root = (project in file("."))
  .enablePlugins(Common, PlayScala, GatlingPlugin, NewRelic)
  .configs(GatlingTest)
  .settings(inConfig(GatlingTest)(Defaults.testSettings): _*)
  .settings(
    name := """inf-rest-api""",
    scalaSource in GatlingTest := baseDirectory.value / "/gatling/simulation"
  )

lazy val docs = (project in file("docs")).enablePlugins(ParadoxPlugin).
  settings(
    paradoxProperties += ("download_url" -> "https://example.lightbend.com/v1/download/play-rest-api")
  )

// docker settings - http://www.scala-sbt.org/sbt-native-packager/formats/docker.html
dockerBaseImage := "openjdk:8-slim"
dockerRepository := Some("infinera")
version in Docker := version.value

dockerCommands := dockerCommands.value.filterNot {
  case ExecCmd("ENTRYPOINT", args @ _*) => true //remove the entrypoint so we can customize it
  case cmd                              => false
}

dockerCommands ++= Seq(
  ExecCmd("ENTRYPOINT","bin/inf-rest-api","-J-Xmx8000M","-J-Xms8000M")
)

javaOptions in Universal ++= Seq(
  "-Dpidfile.path=/dev/null",
  "-J-XX:+UseG1GC",
  "-J-XX:MaxGCPauseMillis=200",
  "-J-XX:ParallelGCThreads=20",
  "-J-XX:ConcGCThreads=5",
  "-J-XX:InitiatingHeapOccupancyPercent=70",
  "-J-XX:PermSize=512m"
)
