name := "fetcher"
organization := "me.carjump"
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.14" % "test",
  "org.specs2" %% "specs2" % "2.4.17" % "test"
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions")