package com.mangalam.techchallenge.logs

// IMPORTS
import scala.collection.mutable
import java.sql.{Connection, ResultSet}
import com.mangalam.techchallenge.utility.{DatabaseInstance, TimeStatus}
import com.mangalam.techchallenge.utility.PipelineConstants.{DATABASE_LOG_TABLE, DATABASE_SCHEMA}


class DateValidation {

  def dateValidation(applicationProperties : mutable.Map[String, String])={
    println("----- Validating Date into DATABASE -----")
    val databaseSchema = applicationProperties.get(DATABASE_SCHEMA).get
    val logTable = applicationProperties.get(DATABASE_LOG_TABLE).get

    val getCurrentTIme = new TimeStatus
    val getDatabaseConnection = new DatabaseInstance

    val currentDate = getCurrentTIme.gettingCurrentTime()
    val conn: Connection= getDatabaseConnection.mySQLConnection(applicationProperties)

    val check = s"SELECT count(*) as total_count from ${databaseSchema}.${logTable} WHERE date = '${currentDate}';"
    val pstmt = conn.prepareStatement(check)
    val rs: ResultSet = pstmt.executeQuery()
    var total = 0
    while (rs.next()) {
      total = rs.getInt("total_count")
    }

    var checkStatus: Boolean = false
    if (total > 0) {
      println("Current Date Already Exists in Database")
      checkStatus = true
    }
    println("----- Validation Completed -----")
    checkStatus
  }
}
