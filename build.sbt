ThisBuild / scalaVersion := "2.13.1"

lazy val hello = (project in file("."))
  .settings(
    name := "mongo-pipe",
    libraryDependencies ++= List(
      "dev.zio"     %% "zio-streams"                   % "1.0.0-RC17",
      "dev.zio"     %% "zio-kafka"                     % "0.5.0",
      "dev.zio"     %% "zio-interop-reactivestreams"   % "1.0.3.5-RC2",
      "org.mongodb" % "mongodb-driver-reactivestreams" % "1.13.0"
    )
  )
