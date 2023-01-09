package com.mangalam.techchallenge.logs

// IMPORTS
import scala.collection.mutable
import java.sql.{Connection, Statement}
import com.mangalam.techchallenge.utility.PipelineConstants.{DATABASE_LOG_TABLE, DATABASE_SCHEMA}
import com.mangalam.techchallenge.utility.{DatabaseInstance, TimeStatus}


class DateSnapshot {
  // CREATING SNAPSHOT OF CURRENT UPLOAD
  def createSnapshot(applicationProperties : mutable.Map[String, String]): Unit = {

    val databaseSchema = applicationProperties.get(DATABASE_SCHEMA).get
    val logTable = applicationProperties.get(DATABASE_LOG_TABLE).get

    val getCurrentTIme = new TimeStatus
    val getDatabaseConnection = new DatabaseInstance

    try {
      val currentDate = getCurrentTIme.gettingCurrentTime()
      val conn: Connection = getDatabaseConnection.mySQLConnection(applicationProperties)

      val stmt: Statement = conn.createStatement
      val insertSql = s"INSERT INTO ${databaseSchema}.${logTable}(date) VALUES (?)"
      val pstmt = conn.prepareStatement(insertSql)
      pstmt.setString(1, currentDate)
      pstmt.executeUpdate()

      println(s"----- New Snapshot created for ${currentDate}-----")
    }
    catch {
      case e: Exception =>
        {
          println(s"----- Failed to Create snapshot in ${databaseSchema}.${logTable} -----")
        }
    }
  }
}