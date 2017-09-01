name := "kafka-demo"

version := "1.0"

scalaVersion := "2.11.11"

unmanagedBase := baseDirectory.value / "libs"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.2.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.1",
  "org.xerial" % "sqlite-jdbc" % "3.20.0"
)
