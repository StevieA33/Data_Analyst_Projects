package com.Producer.Generators

import com.Producer.GenHelper
import com.ProductOrder
import com.Tools.MathHelper

import java.time.LocalDateTime
import scala.util.Random

object ECommGenerator {

  private final val r = new Random()
  //  private final val eCommerceFile = "clean_data/ecommerce_cleaned_teddy.txt"
  //  private final val quantities = List(1,2,3,4,5)
  var baseQuant = 2

  val eCommerceList = os
    .read
    .lines
    //    .stream(os.pwd / "clean_data" / "ecommerce_data.csv" )
    .stream(DataValidator.validatedData("clean_data/ecommerce_data.csv"))
    .drop(1)
    .map(line => line.split("\\|"))
    .filter(_.length == 6)
    .filter(!_(2).contains("$"))
    .toList
    .map(splitArray => {
      val name = splitArray(3)
      val price = splitArray(2).toDouble
      val site = splitArray(1)
      (name, price, site)
    })

  def genECommOrder(po:ProductOrder): ProductOrder = {
    //    val quantity = MathHelper.chooseFromWeightedList(quantities,List(0.))
    val (name, price, site) = MathHelper.chooseFromList(eCommerceList)

    val quant = price match {
      case price if price < 10 => MathHelper.chooseFromWeightedList(
        List(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30),
        List(0.125,	0.0555555556,	0.0357142857,	0.0263157895,	0.0208333333,	0.0172413793,	0.0147058824,	0.0128205128,0.0113636364,0.0102040816,0.0092592593,0.0084745763,0.0078125,0.0072463768,0.0067567568,0.0063291139,0.005952381,0.0056179775,0.0053191489,0.0050505051,0.0048076923,0.004587156,0.0043859649,0.0042016807,0.0040322581,0.003875969,0.0037313433,0.0035971223,0.0034722222,0.0033557047)
      )
      case price if price < 20 => MathHelper.chooseFromWeightedList(
        List(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30),
        List(0.7,	0.1,	0.028,	0.0210526316,	0.0166666667,	0.0137931034,	0.0117647059,	0.0102564103,	0.0090909091,	0.0081632653,	0.0074074074,	0.006779661,	0.00625,	0.0057971014,	0.0054054054,	0.0050632911,	0.0047619048,	0.004494382,	0.0042553191,	0.004040404,	0.0038461538,	0.0036697248,	0.0035087719,	0.0033,	0.0032,	0.003,	0.0029,	0.0, 0.0, 0.0)
      )
      case price if price < 50 => MathHelper.chooseFromWeightedList(
        List(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30),
        List(0.7,	0.1,	0.028,	0.0210526316,	0.0166666667,	0.0137931034,	0.0117647059,	0.0102564103,	0.0090909091,	0.0081632653,	0.0074074074,	0.006779661,	0.00625,	0.0057971014,	0.0054054054,	0.0050632911,	0.0047619048,	0.004494382,	0.0042553191,	0.004040404,	0.0038461538,	0.0036697248,	0.0035087719,	0.0033,	0.0032,	0.003,	0.0029,	0.0,	0.0, 0.0)
      )
      case price if price < 100 => MathHelper.chooseFromWeightedList(
        List(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30),
        List(0.728,	0.0661818182,	0.0346666667,	0.023483871,	0.0177560976,	0.0142745098,	0.0119344262,	0.0102535211,	0.0089876543,	0.008,	0.0072079208,	0.0065585586,	0.0060165289,	0.0055572519,	0.0051631206,	0.0048211921,	0.0045217391,	0.0042573099,	0.0040220994,	0.0038115183,	0.0036218905,	0.003450237,	0.0032941176,	0.0031515152,	0.0030207469,	0.0029003984,	0.002789272,0,0,0)
      )
      case _ => MathHelper.chooseFromWeightedList(
        List(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30),
        List(0.728,	0.0661818182,	0.0346666667,	0.023483871,	0.0177560976,	0.0142745098,	0.0119344262,	0.0102535211,	0.0089876543,	0.008,	0.0072079208,	0.0065585586,	0.0060165289,	0.0055572519,	0.0051631206,	0.0048211921,	0.0045217391,	0.0042573099,	0.0040220994,	0.0038115183,	0.0036218905,	0.003450237,	0.0032941176,	0.0031515152,	0.0030207469,	0.0029003984,	0.002789272,0,0,0)
      )
    }

    po.qty = quant
    po.price = price
    po.product_name = name
    po.ecommerce_website_name = site
    po.product_id = math.abs(name.hashCode)

    return po

  }

  def main(args: Array[String]): Unit = {
    val po = ProductOrder.getInitialOrder(LocalDateTime.now)
    for (i <- 1 to 1000) {
      println("Order " + i + ":")
      genECommOrder(po)
      println("name: " + po.product_name)
      println("price: " + po.price)
      println("qty: " + po.qty)
      GenHelper.addTransactionInfo(Option(po))
      println(GenHelper.toFinalString(Option(po)))
      println
    }
  }


}
