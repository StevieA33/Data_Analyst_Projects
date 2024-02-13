package com.Producer.Generators

import scala.collection.mutable.ListBuffer
import scala.io.Source

object
CityGenerator {
  private final val usCityFile = "data/american_cities.txt"
  private final val cnCityFile = "data/chinese_cities.txt"
  private final val spCityFile = "data/spain_cities.txt"
  private final val countries = new ListBuffer[String]
  private final val ran = scala.util.Random
  for (line <- Source.fromFile("data/countries.txt").getLines) {
    countries += line
  }

  private def genUS(): String = {
    val cities = new ListBuffer[String]
    for (line <- Source.fromFile(usCityFile).getLines) {
      cities += line
    }
    cities(ran.nextInt(cities.length))
  }

  private def genCN(): String = {
    val cities = new ListBuffer[String]
    for (line <- Source.fromFile(cnCityFile).getLines) {
      cities += line
    }
    cities(ran.nextInt(cities.length))
  }

  private def genSP(): String = {
    val cities = new ListBuffer[String]
    for (line <- Source.fromFile(spCityFile).getLines) {
      cities += line
    }
    cities(ran.nextInt(cities.length))
  }

  def genCity(productOrder: String, country: String): String = {
    country match {
      case "United States" => return productOrder + "," + genUS()
      case "China" => return productOrder + "," + genCN()
      case "Spain" => return productOrder + "," + genSP()
      case _ => return "Data For This Country Does Not Exist"
    }
  }
}
