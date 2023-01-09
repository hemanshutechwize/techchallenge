import com.typesafe.sbt.packager.docker._

name := "spark-application"
version := "0.1"
scalaVersion := "2.12.12"

parallelExecution in run := true

enablePlugins(DockerPlugin, JavaAppPackaging)
disablePlugins(AssemblyPlugin)

mainClass in Compile := Some("com.mangalam.techchallenge.DataPipeline")

packageName in Docker := packageName.value

version in Docker := "1.0"

libraryDependencies ++= Seq(
  "mysql"                  % "mysql-connector-java"             % "8.0.23",
  "org.apache.spark"       % "spark-sql_2.12"                   % "3.0.1",
  "org.apache.spark"      %% "spark-hive"                       % "3.0.1" ,
  "org.apache.spark"       % "spark-core_2.12"                  % "3.0.1",
  "org.mongodb.spark"      % "mongo-spark-connector_2.12"       % "3.0.1",
  "org.mongodb"            % "mongo-java-driver"                % "3.12.8",
  "com.singlestore"        % "singlestore-spark-connector_2.12" % "3.1.1-spark-3.0.0",
  "org.mariadb.jdbc"       % "mariadb-java-client"              % "2.+",
  "com.redislabs"         %% "spark-redis"                      % "2.5.0",
  "com.typesafe"           % "config"                           % "1.4.1",
  "io.circe"               % "circe-core_2.12"                  % "0.11.1",
  "io.circe"               % "circe-parser_2.12"                % "0.11.1",
  "io.circe"               % "circe-generic_2.12"               % "0.11.1",
  "io.circe"               % "circe-scalajs_sjs0.6_2.12"        % "0.11.1",
  "com.beachape"           % "enumeratum-circe_2.12"            % "1.5.13",
  "com.beachape"           % "enumeratum_2.12"                  % "1.5.13",
  "joda-time"              % "joda-time"                        % "2.10.5",
  "com.softwaremill.sttp" %% "core"                             % "1.0.5",
  "com.softwaremill.sttp" %% "async-http-client-backend-future" % "1.7.2",
  "org.scalactic"          % "scalactic_2.12"                   % "3.2.5",
  "org.scalatest"          % "scalatest_2.12"                   % "3.2.5",
  "org.apache.hadoop"      % "hadoop-common"                    % "3.0.0",
  "org.apache.hadoop"      % "hadoop-client"                    % "3.0.0",
  "org.apache.hadoop"      % "hadoop-aws"                       % "3.0.0",
  "org.scalatest"         %% "scalatest"                        % "3.2.11",
  "software.amazon.awssdk" % "regions"                          % "2.17.8",
  "software.amazon.awssdk" % "services"                         % "2.17.8",
  "org.quartz-scheduler"   % "quartz"                           % "2.3.2",
  "com.lihaoyi"           %% "requests"                         % "0.8.0",
  "com.lihaoyi"           %% "ujson"                            % "0.7.1",
  "org.mongodb.scala"     %% "mongo-scala-driver"               % "2.9.0",
  "org.mongodb"           %% "casbah"                           % "3.1.1"
)

dockerExposedPorts ++= Seq(9000, 9001)
dockerExposedVolumes := Seq("/opt/docker/logs")
dockerCommands ++= Seq(
  Cmd("USER", "root")
)

val `semanticdb` = "org.scalameta" % "semanticdb-scalac_2.12.12" % "4.4.11"
addCompilerPlugin(`semanticdb`)
scalacOptions ++= Seq("-deprecation", "-Ywarn-unused")

configs(IntegrationTest)
Defaults.itSettings

jacocoReportDirectory := target.value / "site" / "jacoco"
jacocoReportSettings := JacocoReportSettings(
  "Jacoco Coverage Report",
  None,
  JacocoThresholds(),
  Seq(JacocoReportFormats.CSV),
  "utf-8"
)
