name := "kafka-demo"

version := "1.0"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.2.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.1",
  "org.apache.kafka" % "kafka_2.11" % "0.11.0.0",
  "org.apache.zookeeper" % "zookeeper" % "3.4.5"
    exclude("com.sun.jdmk", "jmxtools")
    exclude("com.sun.jmx", "jmxri")
    exclude("javax.jms", "jms"),
  "org.xerial" % "sqlite-jdbc" % "3.20.0"
)

trapExit := false
