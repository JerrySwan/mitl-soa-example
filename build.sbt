lazy val root = project.
  settings(
    name := "MitLSOAExample",
    version := "0.1.0",
    scalaVersion := "2.12.3"
)

// for naive http:
resolvers += "Tim Tennant's repo" at "https://dl.bintray.com/timt/repo/"

libraryDependencies ++= Seq(
  "io.github.shogowada" %% "scala-json-rpc" % "0.9.3",
  "io.github.shogowada" %% "scala-json-rpc-upickle-json-serializer" % "0.9.3",
  "org.http4s" % "http4s-core_2.12" % "1.0.0-M4",
  "io.shaka" %% "naive-http" % "110",
  "org.scala-lang.modules" %% "scala-xml" % "1.3.0"
)

// End ///////////////////////////////////////////////////////////////


