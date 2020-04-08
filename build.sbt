name := "future-vs-monix-task"

version := "0.1.0"

scalaVersion := "2.13.1"

scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",        // source files are in UTF-8
  "-deprecation", // warn about use of deprecated APIs
  "-unchecked",   // warn about unchecked type parameters
  "-feature"      // warn about misused language features
  // "-Ypartial-unification" // (only for scalaVersion < 2.13.0) allow the compiler to unify type constructors of different arities
  //"-Xlint",                 // enable handy linter warnings
  //"-Xfatal-warnings",        // turn compiler warnings into errors
)

libraryDependencies ++= Seq(
  "io.monix" %% "monix" % "3.1.0" withSources () withJavadoc ()
)

addCompilerPlugin(
  "org.typelevel" % "kind-projector" % "0.11.0" cross CrossVersion.full
)
