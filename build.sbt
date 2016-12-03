name := "play-scala"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "org.xerial" % "sqlite-jdbc" % "3.8.6",
  "com.typesafe.slick" %% "slick" % "3.1.1"
)

libraryDependencies += evolutions