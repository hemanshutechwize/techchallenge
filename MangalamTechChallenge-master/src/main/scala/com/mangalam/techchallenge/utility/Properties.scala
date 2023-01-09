package com.mangalam.techchallenge.utility

import java.io.File
import java.util.Properties
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.io.Source

object Properties {

  def readApplicationProperties(
                                 applicationPropertiesFilePath: String
                               ): mutable.Map[String, String] = {
    val filePath = new File(getClass.getClassLoader.getResource(applicationPropertiesFilePath).getPath)
    val applicationProperties = new Properties()
    applicationProperties.load(Source.fromFile(filePath).reader())
    applicationProperties.asScala
  }
}