package com.Producer

import java.time.temporal.ChronoUnit
import com.Tools.DateHelper._
import GenHelper._
import com.ProductOrder
import com.Tools.CountryFunctions.globalScale
import com.Tools.{CountryFunctions, DateHelper, FunctionTiming}
import os.RelPath

import scala.util.Random

/**
 * This object deals with creating a burst of orders for blocks of time,
 * assigning a country of origin and product category for each order
 * and fills out the rest of the info pseudo-randomly based off that starting info
 * It then sends it to a Kafka topic
 */
object ProducerPipeline {
  val debugMode = false
  val useEC2 = false
  val useKafka = true
  val writeToFileNotHDFS = false // CHANGED
  val verbose = false

  val readerGroupID = "Kafkanaut-" // + Math.abs(Random.nextInt())
  val writeTopic = "teststream"
  val readTopic = "teststream"
  val consumerPath = os.pwd / RelPath("team1/teststream.csv")
  val hdfsPath = "hdfs://localhost:9000/Kafkanauts/their-stream-data.csv" // "/Kafkanauts/our-stream-data.csv"
  val isTheirData = false

  def main(args: Array[String]): Unit = {
    val increment = 5
    estimateTotal("2022-01-31", increment, 1, 288 * 7 * 2)
    val start = FunctionTiming.start()
    startProducing("2022-01-31", increment, 10000, 288 * 7 * 2)
    FunctionTiming.end(start)
  }

  def estimateTotal(startDateStr: String, minuteIncrements: Long = 12*60, processDelay: Long = 5000, maxIterations: Int = Int.MaxValue): Unit = {
    val startDate = strToLocalDate(startDateStr).atStartOfDay()

    println(s"Previewing Production at ${DateHelper.print(startDate)} with $minuteIncrements minute increments x $maxIterations, delayed by $processDelay ms")
    val total = (1 until maxIterations)
      .map(i => {
        val batchDateTime = startDate.plus(minuteIncrements * i, ChronoUnit.MINUTES)
        val dayPercentage = getPercentThroughDay(batchDateTime)
        val dayOfWeek: Int = DateHelper.getDayOfWeek(batchDateTime)
        val countryProbs = GenHelper.getCountryProbabilities(dayPercentage, dayOfWeek)
        val batchSize: Int = Math.ceil(countryProbs.sum * globalScale).toInt
        batchSize
      }).sum
    println(s"This will create about $total records in total\n")
  }

  def startProducing(startDateStr: String, minuteIncrements: Long = 12*60, processDelay: Long = 5000, maxIterations: Int = Int.MaxValue): Unit = {
    val startDate = strToLocalDate(startDateStr).atStartOfDay()
    var lastDay = DateHelper.getDayOfWeek(startDate)

    if (useKafka) {
      println(s"Producing into topic: ${writeTopic}")
    }

    println(s"Starting Production at ${DateHelper.print(startDate)} with $minuteIncrements minute increments, delayed by $processDelay")
    for (i <- (1 until maxIterations)) {
      val batchDateTime = startDate.plus(minuteIncrements * i, ChronoUnit.MINUTES)
      val dayPercentage = getPercentThroughDay(batchDateTime)
      val dayOfWeek: Int = DateHelper.getDayOfWeek(batchDateTime)
      val countryProbs = GenHelper.getCountryProbabilities(dayPercentage, dayOfWeek)
      val batchSize: Int = Math.ceil(countryProbs.sum * globalScale).toInt

      if (dayOfWeek != lastDay) {
        println(s"New day: $batchDateTime")
        lastDay = dayOfWeek
      }

      if (verbose)
        println(s"Batch of: $batchSize")

      val chinaCats = CountryFunctions.getCategoryProbabilities("China", dayOfWeek, dayPercentage)
      val usCats    = CountryFunctions.getCategoryProbabilities("United States", dayOfWeek, dayPercentage)
      val spainCats = CountryFunctions.getCategoryProbabilities("Spain", dayOfWeek, dayPercentage)

      val dataBatch = (1 to batchSize)
        .map(_ => ProductOrder.getInitialOptOrder(batchDateTime, countryProbs))
        .map(p => GenHelper.addWebsiteInfo(p))
        .map(p => GenHelper.addCategory(p, chinaCats, usCats, spainCats))
        .map(p => GenHelper.addProduct(p, dayPercentage, dayOfWeek))
        .map(p => GenHelper.addCustomerInfo(p, dayPercentage, dayOfWeek))
        .map(p => GenHelper.addTransactionInfo(p))
        .map(toFinalString)
        .map(_.replace("\n", ""))

      if (verbose) {
        println("Starting send")
        dataBatch.foreach(println)
      }

      if (useKafka)
        dataBatch.foreach(Producer.send)


//      println("Sleeping...")
      Thread.sleep(processDelay)
    }

    println("Total " + GenHelper.totalCnt)
    println("Corrupt: " + GenHelper.totalCorrupt)
  }
}
