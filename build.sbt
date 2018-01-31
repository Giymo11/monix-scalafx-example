name := "scalafx-eventbus-example"

version := "0.1"

scalaVersion := "2.12.4"

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.144-R12"
libraryDependencies += "io.monix" %% "monix" % "3.0.0-M3"

libraryDependencies += "org.tpolecat" %% "doobie-core" % "0.5.0-M14"
libraryDependencies += "org.tpolecat" %% "doobie-h2" % "0.5.0-M14" // H2 driver 1.4.196 + type mappings.

libraryDependencies += "com.github.scala212-forks" %% "sorm" % "0.3.22-SNAPSHOT"

libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.25"

// javaOptions += "-Dorg.slf4j.simpleLogger.log.sorm=debug"
fork in run := true
