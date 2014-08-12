import sbt._
import sbt.Keys._

object Build extends sbt.Build {

  lazy val project = Project(
    id = "game",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name                  := "Game",
      organization          := "com.swingdev.game",
      version               := "0.1-SNAPSHOT",
      scalaVersion          := "2.11.2",
      scalacOptions         := Seq("-deprecation", "-feature", "-encoding", "utf8"),
      scalacOptions in Test ++= Seq("-Yrangepos"),
      libraryDependencies   ++= Dependencies()
    )
  )

  object Dependencies {

    object Versions {
      val akka = "2.3.4"
    }

    val compileDependencies = Seq(
      	"com.typesafe.akka" %% "akka-actor" % Versions.akka,
      	"com.typesafe.akka" %% "akka-stream-experimental" % "0.4"
    )

    val testDependencies = Seq(
      "com.typesafe.akka" %% "akka-testkit" % Versions.akka % "test",
      "org.specs2" %% "specs2" % "2.4" % "test"
    )

    def apply(): Seq[ModuleID] = compileDependencies ++ testDependencies

  }

}
