package com.Analyzer

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, SparkSession}

object SparkQueryLoader {
  //Define main method to test SparkQueryLoader Object
  //Will be deleted later
  def main(args: Array[String]): Unit = {
    loadQuery(1).show()
  }

  def loadQuery(question: Int): DataFrame = {
    getSparkSession().sparkContext.setLogLevel("INFO")
    val logger = Logger.getLogger("QueryLoader")
    val df: DataFrame = question match {
      case 1 => question1()
    }
    getSparkSession().sparkContext.setLogLevel("ERROR")
    df
  }

  protected def getSparkSession(): SparkSession = {
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.spark-project").setLevel(Level.ERROR)
    Logger.getLogger("org").setLevel(Level.ERROR);
    val spark: SparkSession = SparkSession
      .builder
      .appName("Products Analyzer")
      .config("spark.master", "local[*]")
      .enableHiveSupport()
      .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
    spark
  }


  //Testing will be edited later for each member's query
  protected def question1(): DataFrame = {
    val test = getSparkSession().read.csv("clean_data/grocery_data_china.csv")
    test
  }

}
