name := """sample1"""
organization := "com.chemix"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(
    "org.reactivemongo" %% "play2-reactivemongo" % "0.11.11-play24",
    "org.mindrot" % "jbcrypt" % "0.3m"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.chemix.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.chemix.binders._"
