package com.mangalam.techchallenge.utility

// IMPORTS
import scala.collection.mutable
import java.sql.DriverManager
import com.mangalam.techchallenge.utility.PipelineConstants._

// CONNECTING TO MYSQL DATABASE
class DatabaseInstance {

  def mySQLConnection(
                       applicationProperties : mutable.Map[String, String]
                     ) = {

    val databaseURL = applicationProperties.get(DATABASE_URL).get
    val databaseUsername = applicationProperties.get(DATABASE_USERNAME).get
    val databasePassword = applicationProperties.get(DATABASE_PASSWORD).get

    val Connection = DriverManager.getConnection(
      databaseURL,
      databaseUsername,
      databasePassword
    )
    Connection
  }
}
