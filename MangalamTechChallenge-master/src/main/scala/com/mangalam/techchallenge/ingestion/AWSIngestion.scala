package com.mangalam.techchallenge.ingestion

// IMPORTS
import scala.collection.mutable
import org.apache.spark.sql.{Dataset, Row, SparkSession}
import com.mangalam.techchallenge.utility.TimeStatus
import com.mangalam.techchallenge.utility.PipelineConstants._


class AWSIngestion {

    // WRITING SPARK DATAFRAME TO AWS BUCKET
    def writeToAWS(sparkSession: SparkSession,
                    dataSet: Dataset[Row],
                    applicationProperties: mutable.Map[String, String])={

      println("----- INGESTING DATA TO AWS -----")
      val bucket = applicationProperties.get(BUCKET).get
      val num_repartition = applicationProperties.get(REPARTITION).get

      try {
        val getCurrentTIme = new TimeStatus
        val currentDate = getCurrentTIme.gettingCurrentTime()

        dataSet
          .repartition(num_repartition.toInt)
          .write
          .mode("overwrite")
          .parquet(s"s3a://${bucket}/${currentDate}")

        println("Upload to s3 completed")
        println(s"----- DATA TO AWS INGESTED s3a://${bucket}/${currentDate}-----")
      }
      catch {
        case e: Exception => {
          println("----- Failed to write data into AWS s3 Bucket ------")
        }
      }
    }


}
