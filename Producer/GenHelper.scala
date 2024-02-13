package com.Producer

import com.Producer.Generators._
import com.ProductOrder
import com.Tools.CountryFunctions.{chinaScale, spainScale, usScale}
import com.Tools.{CountryFunctions, MathHelper}
import com.Producer.ProducerPipeline.debugMode

import scala.util.Random

/**
 * This object helps store the rate of production of various goods organized by country, day of week and product category
 */
object GenHelper {
  val countries = List("China", "United States", "Spain")

  val chinaDaily: List[Double => Double] = CountryFunctions.getChineseDaily()
  val usDaily: List[Double => Double]    = CountryFunctions.getUSDaily()
  val spainDaily: List[Double => Double] = CountryFunctions.getSpainDaily()

  // TODO: Finish and make canonical
  val categories = List("E-Commerce", "Gas", "Groceries", "Medicine", "Music")

  val corruptionChance: Double = 0.03

  var orderIDAccumulator = 1000 // A globally incremented value.

  var totalCnt = 0
  var totalCorrupt = 0


  def getCountryProbabilities(dayPercent: Double, day: Int): List[Double] = {
    List(chinaDaily(day)(dayPercent), usDaily(day)(dayPercent), spainDaily(day)(dayPercent))
  }

  def addCategory(poOpt: Option[ProductOrder], chinaProbs: List[Double], usProbs: List[Double], spainProbs: List[Double]): Option[ProductOrder] = {
    try {
      if (poOpt.isDefined) {
        val po = poOpt.get
        po.product_category = po.country match {
          case "China" => MathHelper.chooseFromWeightedList(categories, chinaProbs)
          case "United States" => MathHelper.chooseFromWeightedList(categories, usProbs)
          case "Spain" => MathHelper.chooseFromWeightedList(categories, spainProbs)
        }
        Some(po)
      } else None
    } catch {
      case e: Throwable  => if (debugMode) throw e else None
    }
  }

  def addProduct(poOpt: Option[ProductOrder], dayPercent: Double, day: Int): Option[ProductOrder] = {
    try {
      if (poOpt.isDefined) {
        val po = poOpt.get
        po.product_category match {
          case "Gas" => GasStationGenerator.generateStations(po)
          case "Groceries" => GroceryGenerator.generateGroceries(po, day)
          case "E-Commerce" => ECommGenerator.genECommOrder(po)
          case "Medicine" => MedicineGenerator.getMedicine(po)
          case "Music" => MusicGenerator.genMusic(po)
          case e =>
            println("Bad category: ", e)
            po.product_category = "Medicine"
            MedicineGenerator.getMedicine(po)
        }
        Some(po)
      } else None
    } catch {
      case e: Throwable  => if (debugMode) throw e else None
    }
  }

  def addCustomerInfo(poOpt: Option[ProductOrder], dayPercent: Double, day: Int): Option[ProductOrder] = {
    try {
      if (poOpt.isDefined) {
        Some(CustomerInfoGenerator.generateCustomer(poOpt.get))
      } else None
    } catch {
      case e: Throwable => if (debugMode) throw e else None
    }
  }

  //  def addTransactionInfo(dayPercent: Double, day: Int, po: ProductOrder): ProductOrder = {
  def addTransactionInfo(poOpt: Option[ProductOrder]): Option[ProductOrder] = {
    try {
      if (poOpt.isDefined) {
        Some(TransactionInfoGenerator.addTransactionInfo(poOpt.get))
      } else None
    } catch {
      case e: Throwable => if (debugMode) throw e else None
    }
  }

  def addWebsiteInfo(poOpt: Option[ProductOrder]): Option[ProductOrder] = {
    try {
      if (poOpt.isDefined) {
        Some(WebsiteGenerator.getWebsite(poOpt.get))
      } else None
    } catch {
      case e: Throwable => if (debugMode) throw e else None
    }
  }

  def toFinalString(poOpt: Option[ProductOrder]): String = {
    totalCnt += 1
    try {
      if (poOpt.isDefined) {
        val po = poOpt.get
        po.price = MathHelper.roundDouble(Math.abs(po.price) + 0.01)
        po.qty = Math.max(1, Math.abs(po.qty))
        if (Random.nextDouble() > GenHelper.corruptionChance) {
          return ProductOrder.toString(po)
        } else {
          totalCorrupt += 1
          return TrashMaker5000.makeTrash(poOpt)
        }
      }
      totalCorrupt += 1
      return TrashMaker5000.makeTrash(poOpt)
    } catch {
      case e: Throwable  => if (debugMode) throw e else "as;lakdjsfla;sdfj;lkaj;lks| lkasdf|"
    }
  }
}
