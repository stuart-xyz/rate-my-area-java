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
  "com.h2database" % "h2" % "1.4.196" % Test
)

javaOptions in Test ++= Seq("-Dconfig.file=conf/application.test.conf")
