package com.Producer.Generators

import com.ProductOrder
import com.Tools.MathHelper

import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.Random

object GasStationGenerator {
  //private final val ran = scala.util.Random
  //private final val gasStations1 = "data/unique_gas_stations.csv"
  val gasStations = os
    .read
    .lines(os.pwd / "clean_data" / "unique_gas_stations.csv")
    .map(line => line)
    .toList

  def generateStations(po:ProductOrder): ProductOrder = {
    po.country match {
      case "China" => genChinaGasStations(po)
      case "United States" => genUsGasStations(po)
      case "Spain" => genSpainGasStations(po)
    }
  }

  def genUsGasStations(po:ProductOrder): ProductOrder = {
    val (name) = MathHelper.chooseFromList(gasStations)
    val quantity = Random.nextInt(10) + 1
    po.product_name = name
    po.product_category = "Gas"
    po.qty = quantity
    po.price = MathHelper.roundDouble(po.qty * Random.nextDouble()*3 + 1)
    po.product_id = ("name").hashCode()
    po.country = "United States"
    return po
  }
  def genChinaGasStations(po:ProductOrder): ProductOrder = {
    val (name) = MathHelper.chooseFromList(gasStations)
    val quantity = Random.nextInt(10) + 1
    po.product_name = name
    po.product_category = "Gas"
    po.qty = quantity
    po.price = MathHelper.roundDouble(po.qty * Random.nextDouble()*3 + 1)
    po.product_id = ("name").hashCode()
    po.country = "China"
    return po
  }
  def genSpainGasStations(po:ProductOrder): ProductOrder = {
    val (name) = MathHelper.chooseFromList(gasStations)
    val quantity = Random.nextInt(10) + 1
    po.product_name = name
    po.product_category = "Gas"
    po.qty = quantity
    po.price = MathHelper.roundDouble(po.qty * Random.nextDouble()*3 + 1)
    po.product_id = ("name").hashCode()
    po.country = "Spain"
    return po
  }
}
