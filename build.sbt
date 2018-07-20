/** SBT multi-project config
  *
  * For more info on sbt multi-projects see:
  * http://www.scala-sbt.org/1.0/docs/Multi-Project.html
  * and https://github.com/sbt/sbt-assembly multi project section
  *
  * The sbt assembly plugin is used to deploy jars with dependencies (fat jars).
  * For more info on the assembly plugin see:
  * https://github.com/sbt/sbt-assembly
  *
  */

/* ************************************************************************* *\
|                      Shared Dependencies and Settings                       |
\* ************************************************************************* */

//common settings such as name, version, scala version etc.
lazy val commonSettings = Seq(
  organization := "de.htwberlin.f4.wikiplag",
  version := "0.1.0",
  scalaVersion := "2.11.8",
  parallelExecution in test := false
)

//resolvers
resolvers += Classpaths.typesafeReleases

//testing dependencies
lazy val testDependencies = Seq(
  //not sure if this is required
  "org.slf4j" % "slf4j-simple" % "1.7.21" % "test",
  "junit" % "junit" % "4.11" % "test",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
  //"org.scalatest" %% "scalatest" % "2.2.6" % Test
)

lazy val sparkDependencies = Seq(
  //spark context, spark config ...
  "org.apache.spark" %% "spark-core" % "2.1.1" % "provided",
  //dataframe ...
  "org.apache.spark" %% "spark-sql" % "2.1.1" % "provided"
)

lazy val sparkDependencies_compile = Seq(
  //spark context, spark config ...
  "org.apache.spark" %% "spark-core" % "2.1.1" % "compile",
  //dataframe ...
  "org.apache.spark" %% "spark-sql" % "2.1.1" % "compile"
)

//allows communication with a cassandra database from spark
//https://github.com/datastax/spark-cassandra-connector
lazy val cassandraDependencies = Seq(
  "com.datastax.spark" %% "spark-cassandra-connector" % "2.0.1"
)

/* ************************************************************************* *\
|                             UTILS Project                                   |
\* ************************************************************************* */
lazy val utils = (project in file("utils"))
  .settings(
    commonSettings,
    name := "WikiPlagUtils",
    // other settings
    libraryDependencies ++= testDependencies,
    libraryDependencies ++= sparkDependencies,
    libraryDependencies ++= cassandraDependencies,
    libraryDependencies ++= Seq(
      //used for parsing xml by the WikidumpParser
      "org.unbescape" % "unbescape" % "1.1.4.RELEASE",
      "com.databricks" % "spark-xml_2.11" % "0.4.1",
      //read config files easily
      "com.typesafe" % "config" % "1.3.1"
    )
  )

/* ************************************************************************* *\
|                             ANALYSER Project                                |
\* ************************************************************************* */
lazy val plagiarismFinder = (project in file("plagiarismFinder"))
  .settings(
    commonSettings,
    name := "WikiPlagAnalyser",
    libraryDependencies ++= testDependencies,
    libraryDependencies ++= sparkDependencies,
    libraryDependencies ++= Seq(
      //add other dependencies for the analyser project here
    )
  ).dependsOn(utils)


/* ************************************************************************* *\
|                         Word2Vec ANALYSER Project                           |
\* ************************************************************************* */
lazy val plagFinderWord2Vec = (project in file("plagFinderWord2Vec"))
  .settings(
    commonSettings,
    name := "plagFinderWord2Vec",
    libraryDependencies ++= testDependencies,
    libraryDependencies ++= sparkDependencies,
    libraryDependencies ++= Seq(
      //add other dependencies for the analyser project here
    )
  ).dependsOn(utils)


/* ************************************************************************* *\
|                  WIKIPEDIA-IMPORTER (word2vec) Project                      |
\* ************************************************************************* */
lazy val wikiImporterForWord2Vec = (project in file("wikiImporterForWord2Vec"))
  .settings(
    commonSettings,
    name := "WikiPlagImporterForWord2Vec",
    libraryDependencies ++= testDependencies,
    libraryDependencies ++= sparkDependencies_compile,
    libraryDependencies ++= cassandraDependencies,
    libraryDependencies ++= Seq(
      //command line arguments parsing helper
      "commons-cli" % "commons-cli" % "1.2",
      //used for parsing xml by the WikidumpParser
      "org.unbescape" % "unbescape" % "1.1.4.RELEASE",
      "com.databricks" % "spark-xml_2.11" % "0.4.1",
      //read config files easily
      "com.typesafe" % "config" % "1.3.1"
    ),
    //configuration for the sbt-assembly plugin
    assemblyJarName in assembly := "wiki_importer_word2Vec.jar",
    mainClass in assembly := Some("de.htwberlin.f4.wikiImporterWord2Vec.SparkWord2Vec")
  ).dependsOn(utils)


/* ************************************************************************* *\
|                        WIKIPEDIA-IMPORTER Project                           |
\* ************************************************************************* */
lazy val wikipediaImporter = (project in file("wikipediaImporter"))
  .settings(
    commonSettings,
    name := "WikiPlagImporter",
    libraryDependencies ++= testDependencies,
    libraryDependencies ++= sparkDependencies,
    libraryDependencies ++= cassandraDependencies,
    libraryDependencies ++= Seq(
      //command line arguments parsing helper
      "commons-cli" % "commons-cli" % "1.2"
      //add other dependencies for the importer project here
    ),
    //configuration for the sbt-assembly plugin
    assemblyJarName in assembly := "wiki_importer.jar",
    mainClass in assembly := Some("de.htwberlin.f4.wikiplag.SparkApp")
  ).dependsOn(utils)


/* ************************************************************************* *\
|                                REST-API Project                             |
\* ************************************************************************* */

lazy val ScalatraVersion = "2.6.2"

lazy val restApi = (project in file("restApi"))
  .settings(
    commonSettings,
    name := "WikiPlagRestAPI",
    libraryDependencies ++= testDependencies,
    libraryDependencies ++= sparkDependencies_compile,
    libraryDependencies ++= cassandraDependencies,
    libraryDependencies ++= Seq(
      "org.scalatra" %% "scalatra" % ScalatraVersion,
      "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
      "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
      //add compile scope so we can use jetty in standalone mode
      "org.eclipse.jetty" % "jetty-webapp" % "9.4.6.v20170531" % "container;compile",

      "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
      "org.scalatra" %% "scalatra-json" % ScalatraVersion,
       //used for dependecy injection
      "com.sun.jersey" % "jersey-server" % "1.2",
      //library used for json conversion
      "org.json4s" %% "json4s-jackson" % "3.5.2"
      //add further required dependencies here

    ),
    assemblyJarName in assembly := "wiki_rest.jar",
    mainClass in assembly := Some("de.htwberlin.f4.wikiplag.rest.launcher.JettyLauncher"),

    assemblyMergeStrategy in assembly := {
      //discard all files in meta-inf directories
      case PathList("META-INF", xs@_*) => MergeStrategy.discard
      //else if files have the same name (e.g application.config.json) use the first one found in the class path tree
      case x => MergeStrategy.first
    }

  ).dependsOn(utils, plagiarismFinder).
  enablePlugins(SbtTwirl).
  enablePlugins(ScalatraPlugin)



