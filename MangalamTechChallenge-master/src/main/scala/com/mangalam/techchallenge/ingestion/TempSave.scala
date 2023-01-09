package com.mangalam.techchallenge.ingestion

import scala.collection.mutable
import com.mangalam.techchallenge.utility.TimeStatus
import org.apache.spark.sql.{Dataset, Row, SparkSession}
import com.mangalam.techchallenge.utility.PipelineConstants._

class TempSave{

  def saveToLocal(sparkSession: SparkSession,
                  dataSet: Dataset[Row],
                  applicationProperties: mutable.Map[String, String]){

    val num_repartition = applicationProperties.get(REPARTITION).get
    val outputDataPath = applicationProperties.get(OUTPUT_DATA_PATH).get

    val getCurrentTIme = new TimeStatus
    val currentDate = getCurrentTIme.gettingCurrentTime()
    val output_data_path = outputDataPath.replace("$DATE$", currentDate)

    try {
      showDataset(sparkSession, dataSet)
      dataSet
        .repartition(num_repartition.toInt)
        .write
        .mode("overwrite")
        .parquet(s"${output_data_path}")

      println(s"----- Data saved to ${output_data_path} completed -----")
    }
    catch {
      case e: Exception => {
        println(s"----- Failed to save data into ${output_data_path} ------")
      }
    }
  }

  def showDataset(sparkSession: SparkSession,
                  dataSet: Dataset[Row]) = {

    import sparkSession.sql

    dataSet.createOrReplaceTempView("Bikers_Info_show")
    val showCount = sql("SELECT count(*) as Bikers_24h_count from Bikers_Info_show")
    println(showCount.show(10))
  }
}
