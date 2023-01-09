package com.mangalam.techchallenge

import scala.collection.mutable
import org.apache.spark.sql.SparkSession
import com.mangalam.techchallenge.acquisition.APIDataAccess
import com.mangalam.techchallenge.logs.{DateSnapshot, DateValidation}
import com.mangalam.techchallenge.utility.{Properties, SparkConfiguration}
import com.mangalam.techchallenge.ingestion.{AWSIngestion, TempSave}
import com.mangalam.techchallenge.utility.PipelineConstants._
import com.mangalam.techchallenge.utility.TimeStatus


object DataPipeline {

  def main(args: Array[String]): Unit = {

    val applicationProperties: mutable.Map[String, String] = Properties.readApplicationProperties(args(0))
    val sparkSession = SparkConfiguration.createSparkSession(applicationProperties)
    new DataPipeline().execute(sparkSession, applicationProperties)

  }

  class DataPipeline {

    def execute(sparkSession: SparkSession,
                applicationProperties: mutable.Map[String, String]): Unit = {

      val aws_flag = applicationProperties.get(AWS_FLAG).get
      val ingestionTime = applicationProperties.get(INGESTION_TIME).get

      while (true) {

        val AccessingAPIData = new APIDataAccess
        val TempSave = new TempSave
        val AWSIngestion = new AWSIngestion
        val DateSnapshot = new DateSnapshot
        val checkStatus = new DateValidation
        val currentDate = new TimeStatus

        val logStatus = checkStatus.dateValidation(applicationProperties)

        if (logStatus) {
          println()
          println(s"-----Job Already Ran for ${currentDate.gettingCurrentTime()} ... Next Run Tommorrow")
          println()
        }
        else {
          println()
          println(s"----- Job Started for ${currentDate.gettingCurrentTime()} -----")
          println()

          // Accessing data from API
          val accessAPIData = AccessingAPIData.accessAPIData(sparkSession, applicationProperties)

          TempSave.saveToLocal(sparkSession, accessAPIData, applicationProperties)

          // Data Ingestion into AWS bucket
          if (aws_flag == "true"){
            AWSIngestion.writeToAWS(sparkSession, accessAPIData, applicationProperties)
          } else {
            println("Skipping ... AWS Ingestion")
          }

           // Creating Data snapshot for further use
          DateSnapshot.createSnapshot(applicationProperties)

        }
        Thread.sleep(ingestionTime.toInt) // Run Every 24 hours
      }
    }
  }
}
