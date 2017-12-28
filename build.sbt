name := """rate-my-area"""
organization := "io.stuartp"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  guice,
  evolutions,
  javaJdbc,
  ehcache,
  "org.postgresql" % "postgresql" % "9.4.1207.jre7",
  "de.svenkubiak" % "jBCrypt" % "0.4.1",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.186",
  "com.h2database" % "h2" % "1.4.196" % Test,
  "org.mockito" % "mockito-core" % "2.13.0" % Test
)

javaOptions in Test ++= Seq("-Dconfig.file=conf/application.test.conf")
