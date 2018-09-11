import sbt.Keys.testOptions
import sbt.Tests
import com.typesafe.sbt.SbtMultiJvm.multiJvmSettings
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

name := "oacp"

version := "1.0"

val akkaVersion = "2.4.12"

val aspectVersion = "1.8.10"

resolvers += "Eventuate Releases" at "https://dl.bintray.com/rbmhtechnology/maven"

lazy val egspAkka = project
  .in(file("."))
  .settings(multiJvmSettings: _*)
  .settings(commonSettings, libraryDependencies ++= egspAkkaLibs)
  .configs(MultiJvm)

lazy val ldfiakka = project
  .in(file("ldfi-akka"))
  .settings(multiJvmSettings: _*)
  .settings(
    name := "ldfi-akka",
    mainClass in Compile := Some("ldfi.akka.Main"),
    commonSettings,
    libraryDependencies ++= ldfiAkkaLibs
  )
  .configs(MultiJvm)

lazy val commonSettings = Seq(
  organization := "se.kth.csc.progsys",
  scalaVersion := "2.12.4",
  scalacOptions in Compile ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Xlog-reflective-calls",
    "-Xlint",
    "-deprecation", /*"-Ywarn-dead-code",*/ "-language:_",
    "-target:jvm-1.8",
    "-encoding",
    "UTF-8"
  ),
  javacOptions in Compile ++= Seq("-Xlint:unchecked",
    "-Xlint:deprecation",
    "-source",
    "1.8",
    "-target",
    "1.8"),
  javaOptions in run ++= Seq(
    "-Xms128m",
    "-Xmx1024m",
    "-Djava.library.path=./target/native",
    "-javaagent:" + System
      .getProperty("user.home") + "/.ivy2/cache/org.aspectj/aspectjweaver/jars/aspectjweaver-" + aspectVersion + ".jar"
  ),
  jvmOptions in MultiJvm ++= Seq(
    "-Xms128m",
    "-Xmx256M",
    "-Xmx1024m",
    "-Djava.library.path=./target/native",
    "-javaagent:" + System
      .getProperty("user.home") + "/.ivy2/cache/org.aspectj/aspectjweaver/jars/aspectjweaver-" + aspectVersion + ".jar"
  ),
  fork in run := true,
  testOptions += Tests.Argument(TestFrameworks.JUnit, "-v"),
  parallelExecution in Test := false,
  parallelExecution in MultiJvm := false
  //connectInput in run := true
)

lazy val egspAkkaLibs = Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "org.aspectj" % "aspectjweaver" % aspectVersion,
  "org.aspectj" % "aspectjrt" % aspectVersion,
  "com.rbmhtechnology" %% "eventuate-crdt" % "0.8.1",
  "com.typesafe.conductr" %% "scala-conductr-bundle-lib" % "1.9.0",
  "com.typesafe.conductr" %% "akka24-conductr-bundle-lib" % "1.9.0",
  "ch.qos.logback" % "logback-classic" % "1.1.1",
  "com.typesafe" % "config" % "1.2.0"
)

lazy val ldfiAkkaLibs = egspAkkaLibs ++ Seq(
  "org.sat4j" % "org.sat4j.core" % "2.3.1",
  "org.apache.commons" % "commons-io" % "1.3.2",
  "ch.epfl.scala" %% "scalafix-core" % "0.6.0-M5",
  "org.scalameta" %% "scalameta" % "3.7.4",
  "org.scalameta" %% "langmeta" % "3.7.4"
)

resolvers += "Eventuate Releases" at "https://dl.bintray.com/rbmhtechnology/maven"
