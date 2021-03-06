name := "2null16-bra"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  cache,
  ws,
  "org.xerial" % "sqlite-jdbc" % "3.16.1",
  "com.brsanthu" % "migbase64" % "2.2",
  "com.markatta" %% "akron" % "1.2",
  "com.typesafe.play" %% "play-slick" % "2.0.2",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.2"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1",
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" ,
  "com.typesafe.akka" %% "akka-testkit" % "2.4.17" ,
  "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0"
).map(_ % Test)

