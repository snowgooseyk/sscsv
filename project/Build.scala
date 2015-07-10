import sbt._
import Keys._
import sbtrelease._
import ReleaseStateTransformations._
import xerial.sbt.Sonatype._
import scoverage._

object BuildSettings {
    val buildOrganization = "com.github.snowgooseyk"
    val buildVersion      = "0.1.0-SNAPSHOT"
    val buildScalaVersion = "2.11.7"
    val clossBuildScalaVersion = Seq("2.10.5","2.11.7")

    val buildSettings = Defaults.defaultSettings ++ ReleasePlugin.releaseSettings ++ sonatypeSettings ++ ScoverageSbtPlugin.projectSettings  ++ Seq (
      organization := buildOrganization,
      version      := buildVersion,
      scalaVersion := buildScalaVersion,
      crossScalaVersions := clossBuildScalaVersion,
      licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
      description := "Simple CSV library for Scala.",
      publishMavenStyle := true,
      publishTo <<= version { (v: String) =>
          val nexus = "https://oss.sonatype.org/"
          if (v.trim.endsWith("SNAPSHOT"))
              Some("snapshots" at nexus + "content/repositories/snapshots")
          else
              Some("releases"  at nexus + "service/local/staging/deploy/maven2")
      },
      publishArtifact in Test := false,
      scmInfo := Some(ScmInfo(
              url("https://github.com/snowgooseyk/sscsv"),
                  "scm:git:git@github.com:snowgooseyk/sscsv.git"
      )),
      pomExtra := (
            <url>https://github.com/snowgooseyk/sscsv</url>
            <developers>
                <developer>
                    <id>snowgooseyk</id>
                    <name>snowgooseyk</name>
                    <url>https://github.com/snowgooseyk</url>
                </developer>
            </developers>
      ),
      isSnapshot := true,
      scalacOptions ++= Seq("-feature","-deprecation")
    )
}

object Dependencies {
  
  val specsVersion = "3.6.2"

  val commonsLang = "org.apache.commons" % "commons-lang3" % "3.4"
  val specsCore =  "org.specs2" % "specs2-core" % specsVersion % "test" cross CrossVersion.fullMapped {
      case "2.10.5" => "2.10"
      case "2.11.7" => "2.11"
  }
  val specsJunit =  "org.specs2" % "specs2-junit" % specsVersion % "test" cross CrossVersion.fullMapped {
      case "2.10.5" => "2.10"
      case "2.11.7" => "2.11"
  }
  val specsMock =  "org.specs2" % "specs2-mock" % specsVersion % "test" cross CrossVersion.fullMapped {
      case "2.10.5" => "2.10"
      case "2.11.7" => "2.11"
  }

  val all = Seq (
    commonsLang,
    specsCore,
    specsJunit,
    specsMock
  )
}

object Resolvers {
  val m2local = Resolver.mavenLocal 
  val sonatype = Resolver.sonatypeRepo("snapshots")
  val all = Seq (
    m2local,
    sonatype
  )
}

object SSCSV extends Build {
  import BuildSettings._
  import Dependencies._

  lazy val root = Project (
    id = "sscsv",
    base = file("."),
    settings = buildSettings ++ Seq (
      resolvers ++= Resolvers.all,
      libraryDependencies ++= Dependencies.all 
    )
  )
}
