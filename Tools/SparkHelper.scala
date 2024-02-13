package com.Tools

import org.apache.spark.sql.SparkSession

object SparkHelper {

  val spark: SparkSession = {
    suppressLogs(List("org", "akka", "org.apache.spark", "org.spark-project"))
    //    System.setProperty("hadoop.home.dir", "C:\\hadoop")
    val sparkSession = SparkSession
      .builder
      .appName("Spark Pipeline")
      .config("spark.master", "local")
      .enableHiveSupport()
      .getOrCreate()
    sparkSession.conf.set("hive.exec.dynamic.partition.mode", "nonstrict")
    sparkSession.sparkContext.setLogLevel("ERROR")
    sparkSession
  }


  def suppressLogs(params: List[String]): Unit = {
    // Levels: all, debug, error, fatal, info, off, trace, trace_int, warn
    import org.apache.log4j.{Level, Logger}
    params.foreach(Logger.getLogger(_).setLevel(Level.OFF))
  }
}
