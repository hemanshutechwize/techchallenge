package com.mangalam.techchallenge.utility

// FILE IMPORTS
import scala.collection.mutable
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import com.mangalam.techchallenge.utility.PipelineConstants._

object SparkConfiguration {

  def createSparkSession(applicationProperties: mutable.Map[String, String]): SparkSession = {
    val sparkSession: SparkSession =
      SparkSession
        .builder()
        .config(getSparkConf(applicationProperties))
        .getOrCreate()

    sparkSession.sparkContext.setLogLevel("warn")
    // hadoop configuration
    sparkSession.sparkContext.hadoopConfiguration.set("fs.s3a.access.key", applicationProperties.get(ACCESS_KEY).get)
    sparkSession.sparkContext.hadoopConfiguration.set("fs.s3a.secret.key", applicationProperties.get(SECRET_KEY).get)
    sparkSession.sparkContext.hadoopConfiguration.set("fs.s3a.endpoint", applicationProperties.get(FILESYSTEM).get)
    sparkSession.sparkContext.hadoopConfiguration.set("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")

    sparkSession
  }

  def getSparkConf(applicationProperties: mutable.Map[String, String]): SparkConf = {
    val sparkConf = new SparkConf()
    sparkConf.setAll(applicationProperties.filterKeys(_.startsWith("spark.")))

    sparkConf
  }

  def getSparkSession: SparkSession =
    SparkSession
      .builder()
      .getOrCreate()
}
