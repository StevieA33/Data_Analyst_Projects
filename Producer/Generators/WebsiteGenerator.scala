package com.Producer.Generators

import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.Random
import com.ProductOrder
import com.ProductOrder.{isValidOrder, toString}

object WebsiteGenerator {
  private final var usWebsite = "./clean_data/us_websites.txt"
  private final var chinaWebsite = "./clean_data/china_websites.txt"
  private final var spainWebsite = "./clean_data/spain_websites.txt"

  private var usList:ListBuffer[String] = ListBuffer() //object medicine list starts empty
  private var chinaList:ListBuffer[String] = ListBuffer()
  private var spainList:ListBuffer[String] = ListBuffer()
  var isListFilled:Boolean = false
  def fillWebsiteList():Unit = {
    if (!isListFilled) {
      for (lines <- Source.fromFile(usWebsite).getLines()) {
        usList += lines
      }
      for (lines <- Source.fromFile(chinaWebsite).getLines()) {
        chinaList += lines
      }
      for (lines <- Source.fromFile(spainWebsite).getLines()) {
        spainList += lines
      }
    }
    isListFilled = true
  }

  def getWebsite(po: ProductOrder):ProductOrder = {
    fillWebsiteList()
    val ran = new Random()
    var rownum = 0
    var row = ""
    if (po.country == "United States") {
      rownum = Math.abs(ran.nextInt(usList.length))
      row = usList(Math.abs(rownum))
    }
    else if (po.country == "China") {
      rownum = Math.abs(ran.nextInt(chinaList.length))
      row = chinaList(Math.abs(rownum))
    }
    else if (po.country == "Spain") {
      rownum = Math.abs(ran.nextInt(chinaList.length))
      row = chinaList(Math.abs(rownum))
    }
    po.ecommerce_website_name = row
    return po
  }
}