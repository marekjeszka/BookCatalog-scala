name := "BookCatalog-scala"
organization := "com.marekjeszka"
version := "1.0.0"
scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaV = "2.4.4"
  val circeV = "0.4.1"
  Seq(
    "org.scala-lang" % "scala-reflect" % "2.11.8",
    "org.scala-lang.modules" %% "scala-xml" % "1.0.4",

    "com.typesafe.akka" %% "akka-http-core" % akkaV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaV,
    "de.heikoseeberger" %% "akka-http-circe" % "1.9.0",

    "io.circe" %% "circe-core" % circeV,
    "io.circe" %% "circe-generic" % circeV,

    "com.typesafe.slick" %% "slick" % "3.1.1",
    "com.h2database" % "h2" % "1.4.192",

    "org.scalatest" %% "scalatest" % "2.2.6" % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaV,

  "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test"
  )
}

