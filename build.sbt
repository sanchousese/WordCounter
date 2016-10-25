organization  := "com.saniasutula"

version       := "0.1"

scalaVersion  := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"            %%  "spray-can"           % sprayV,
    "io.spray"            %%  "spray-routing"       % sprayV,
    "io.spray"            %%  "spray-testkit"       % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"          % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"        % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"         % "2.3.11" % "test",
    "org.mongodb"         %%  "casbah"              % "3.1.1",
    "com.github.t3hnar"   %%  "scala-bcrypt"        % "2.6"
  )
}

Revolver.settings
