package com.mangalam.techchallenge.utility

// IMPORTS
import java.util.Calendar
import java.text.SimpleDateFormat

// THIS MODULE IS FOR GETTING CURRENT TIME
class TimeStatus {

  def gettingCurrentTime() = {

    val format = new SimpleDateFormat("yyyy-MM-dd")
    val currentDate = format.format(Calendar.getInstance().getTime())

    currentDate
  }
}
