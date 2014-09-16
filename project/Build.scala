import sbt._
import Keys._
import Tests._

object myBuild extends Build {
  lazy val mainProject = Project(
    id="main",
    base=file("."),
    settings = Project.defaultSettings ++ Seq(
      scalaVersion := "2.11.2",
      libraryDependencies ++= List(
        "org.cvogt" %% "slick-action" % "0.2.1"
      )
    )
  )
}
