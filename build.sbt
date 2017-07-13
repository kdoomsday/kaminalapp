name := """kaminalapp"""
organization := "com.ebarrientos"

version := "1.3.1"

lazy val root = (project in file("."))
                   .enablePlugins(PlayScala)

lazy val doobieVersion = "0.4.0"

scalaVersion := "2.11.11"

libraryDependencies += filters
libraryDependencies += evolutions
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test
libraryDependencies += "org.specs2" %% "specs2-junit" % "3.8.9" % "test"

libraryDependencies ++= Seq(
  jdbc,
  "org.scalatest"  %% "scalatest"        % "2.2.6"  % "test",
  "org.scalacheck" %% "scalacheck"       % "1.12.5" % "test",

  "org.tpolecat" %% "doobie-core-cats"       % doobieVersion,
  "org.tpolecat" %% "doobie-postgres-cats"   % doobieVersion,
  "org.tpolecat" %% "doobie-specs2-cats"     % doobieVersion,
  "postgresql"    % "postgresql"             % "9.1-901.jdbc4",

  "com.github.nscala-time" %% "nscala-time"           % "2.14.0",

  "org.webjars"     %% "webjars-play"    % "2.5.0",
  "org.webjars"      % "jquery"          % "3.1.1",
  "be.objectify"    %% "deadbolt-scala"  % "2.5.0",
  "org.webjars"      % "bootstrap"       % "3.0.0" exclude("org.webjars", "jquery"),
  "org.webjars.npm"  % "alertifyjs"      % "1.4.1"
)

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-explaintypes",
  // "-Ywarn-unused-import",
  "-encoding", "UTF-8",
  "-feature",
  "-Xlog-reflective-calls",
  "-Ywarn-dead-code",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Ywarn-unused",
  "-Ywarn-value-discard",
  "-Xlint",
  "-Ywarn-nullary-override",
  "-Ywarn-nullary-unit",
  "-Xfuture"
)

import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

SbtScalariform.scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(RewriteArrowSymbols, true)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.ebarrientos.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.ebarrientos.binders._"
// testOptions in Test +=
//   Tests.Argument(TestFrameworks.Specs2, "junitxml", "console")

// testOptions in Test +=
//   Tests.Argument(TestFrameworks.JUnit, "--ignore-runners=org.specs2.runner.JUnitRunner")
