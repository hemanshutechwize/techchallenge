package com.mangalam.techchallenge.acquisition

// IMPORTS
import java.io.File
import java.io.PrintWriter
import scala.collection.mutable
import org.json4s.JsonAST.JValue
import scala.util.parsing.json._
import org.apache.spark.sql.SparkSession
import org.json4s.jackson.Serialization.write
import org.json4s.{DefaultFormats, Extraction}
import com.mangalam.techchallenge.utility.TimeStatus
import com.mangalam.techchallenge.utility.PipelineConstants._

class APIDataAccess {

  // ACCESSING DATA FROM API AND CONVERTING IT TO SPARK DATAFRAME
  def accessAPIData(sparkSession: SparkSession,
                       applicationProperties: mutable.Map[String, String]) = {

    val getCurrentTIme = new TimeStatus
    val currentDate = getCurrentTIme.gettingCurrentTime()

    val bikerUrl = applicationProperties.get(BIKER_URL).get
    val incidentUrl = applicationProperties.get(INCIDENT_URL).get
    val outputJsonPath = applicationProperties.get(OUTPUT_JSON_PATH).get
    val incidentPerPage = applicationProperties.get(INCIDENT_PER_PAGE).get
    val incidentLocation = applicationProperties.get(INCIDENT_LOCATION).get
    val incidentDistance = applicationProperties.get(INCIDENT_DISTANCE).get
    val incidentTotalPage = applicationProperties.get(INCIDENT_TOTAL_PAGE).get
    val incidentStolenness = applicationProperties.get(INCIDENT_STOLENNESS).get

    // parse the url for incidents
    val parsedURL = setAPIURL(incidentUrl,
      incidentTotalPage,
      incidentPerPage,
      incidentLocation,
      incidentDistance,
      incidentStolenness
    )

    // get bike ID
    val parsedBikerUrl = parseByID(parsedURL, bikerUrl)
    val bikerInfo = getBikersInfo(parsedBikerUrl)
    writingJsonFile(outputJsonPath, currentDate, bikerInfo)
    creatingSparkDataFrame(sparkSession, outputJsonPath, currentDate)
  }

  private def setAPIURL(incidentUrl:String,
                incidentTotalPage:String,
                incidentPerPage: String,
                incidentLocation:String,
                incidentDistance: String,
                incidentStolenness: String)={

    var parsedURL : List[String] = List.empty

    for (totalPages <- 1 to incidentTotalPage.toInt) {
      val qualifiedURL =
        incidentUrl.replace("$TOTAL_PAGES$", totalPages.toString)
          .replace("$PER_PAGE$", incidentPerPage)
          .replace("$LOCATION$", incidentLocation)
          .replace("$DISTANCE$", incidentDistance)
          .replace("$STOLEN$", incidentStolenness)

      parsedURL = parsedURL :+ qualifiedURL
    }
    // return the parsed url
    println(parsedURL)
    parsedURL
  }

  private def parseByID(parsedURL: List[String], bikerUrl: String)={

    var allIds: List[Double] = List.empty
    var getParsedBikerUrl: List[String] = List.empty

    for (url <- parsedURL) {
      val jsonStr = scala.io.Source.fromURL(url).mkString.replace("\n//", "")
      JSON.parseFull(jsonStr) match {
        case Some(json) =>
          val bikes = json.asInstanceOf[Map[String, Any]]("bikes")
            .asInstanceOf[List[Map[String, Any]]]
          val getID = bikes.map(bike => bike("id").asInstanceOf[Double])
          for (id <- getID){
            println(id)
            allIds = allIds :+ id
          }
        case None => println(List())
      }
    }
    for (setBikerUrl <- allIds) {
      val parsedBikerUrl = bikerUrl.replace("$BIKER_ID$",setBikerUrl.toString)
      getParsedBikerUrl = getParsedBikerUrl :+ parsedBikerUrl
    }
    println(getParsedBikerUrl)
    getParsedBikerUrl
  }

  private def getBikersInfo(parsedBikerUrl: List[String])={

    implicit val formats: DefaultFormats.type = DefaultFormats
    var createJsonFormat: List[Any] = List.empty

    for (url <- parsedBikerUrl) {

      val jsonStr = scala.io.Source.fromURL(url).mkString.replace("\n//", "")
      val parsedJson = scala.util.parsing.json.JSON.parseFull(jsonStr).get
      val parsedId = parsedJson
        .asInstanceOf[Map[String, Any]]
        .apply("bike")

      createJsonFormat = createJsonFormat :+ parsedId

    }
    val bikeJson: JValue = Extraction.decompose(createJsonFormat)
    val bikeJsonString = write(bikeJson)
    println(bikeJsonString)
    bikeJsonString
  }

  // WRITING JSON TO FILE
  private def writingJsonFile(jsonPath: String, currentDate: String, APIData: String) = {

    println("----- CREATING LOG JSON FILE -----")
    println("---- json path", jsonPath)
    val file_Object = new File(s"${jsonPath}${currentDate}.json")
    val print_Writer = new PrintWriter(file_Object)

    print_Writer.write(APIData)
    print_Writer.close()
    println(s"----- LOG JSON FILE CREATED ${jsonPath}-----")
  }

  // CREATING SPARK DATAFRAME
  private def creatingSparkDataFrame(sparkSession: SparkSession, jsonPath: String, currentDate: String) = {

    val spark_df = sparkSession.read.json(s"${jsonPath}${currentDate}.json")
    spark_df.show(10, false)
    println("----- SPARK DATAFRAME CREATED -----")

    spark_df
  }

}
