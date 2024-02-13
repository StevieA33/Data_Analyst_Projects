package com.Tools

import MathHelper.shiftTimezone
import MathHelper._

object CountryFunctions {
  val globalScale: Double = 0.2
  val chinaScale:  Double = 1047.0*2/3
  val usScale:     Double = 300
  val spainScale:  Double = 250
  val tacoScale:   Double = 2.0

  val categoryScale = List(1.3, 0.6, 0.7, 0.5, 0.2)
  val dailyScale    = List(1.1, 0.6, 0.7, 0.8, 0.9, 1.2, 1.3)


  // Zips the nth element of each list together
  //  (List(1, 2, 3), List(10, 20, 30), List(100, 200, 300))
  //  => List(List(1, 10, 100), List(2, 20, 200), List(3, 30, 300))
  def combineLists[A](ss: List[A]*): List[List[A]] = {
    val sa = ss.reverse;
    (sa.head.map(List(_)) /: sa.tail) (_.zip(_).map(p => p._2 :: p._1))
  }

  def getChineseDaily(): List[Double => Double] = {
    combineLists(getChinaCategories(): _*).map(dayFuncs => addFuncs(dayFuncs: _*))
  }

  def getUSDaily(): List[Double => Double] = {
    combineLists(getUSCategories(): _*).map(dayFuncs => addFuncs(dayFuncs: _*))
  }

  def getSpainDaily(): List[Double => Double] = {
    combineLists(getSpainCategories(): _*).map(dayFuncs => addFuncs(dayFuncs: _*))
  }

  def getCategoryProbabilities(country: String, day: Int, dayPercent: Double): List[Double] = {
    val categoriesForWeek = country match {
      case "China" => getChinaCategories()
      case "United States" => getUSCategories()
      case "Spain" => getSpainCategories()
    }
    categoriesForWeek.map(_ (day)(dayPercent))
  }

  def applyScales(countryCats: List[List[Double => Double]], countryScale: Double): List[List[Double => Double]] = {
    countryCats
      .map(cat => cat.zip(dailyScale).map{ case (day, scale) => scaleFunc(day, scale * countryScale)})
  }

  def getChinaCategories(): List[List[Double => Double]] = {
    applyScales(List(chineseEcommerce, chineseGas, chineseGroceries, chineseMedicine, chineseMusic)
      .map(_.map(shiftTimezone(_, chinaTimeDiff))), chinaScale)
  }

  def getUSCategories(): List[List[Double => Double]] = {
    applyScales(List(usEcommerce, usGas, usGroceries, usMedicine, usMusic), usScale)
  }

  def getSpainCategories(): List[List[Double => Double]] = {
    applyScales(List(spainEcommerce, spainGas, spainGroceries, spainMedicine, spainMusic)
      .map(_.map(shiftTimezone(_, spainTimeDiff))), spainScale)
  }

  //////////////////////////////////////////
  val chinaTimeDiff = 12
  val chinaStart: Double = 12 - 4
  val chinaEnd: Double = 12 + 4
  val chineseF: Double => Double = getNormalPDF(0, 0.1, 0.4)
  val chinaFScale = 0.85

  val chineseEcommerce: List[Double => Double]  = (0 until 7)
    .map(_ => getQuadModal(chineseF, chinaStart, chinaEnd, chinaFScale * 1.3))
    .toList

  val chineseGas: List[Double => Double]  = (0 until 7)
    .map(_ => getQuadModal(chineseF, chinaStart, chinaEnd, chinaFScale))
    .toList

  val chineseGroceries = (0 until 7)
    .map(_ => getQuadModal(chineseF, chinaStart, chinaEnd, chinaFScale))
    .toList

  val chineseMedicine: List[Double => Double]  = (0 until 7)
    .map(_ => getQuadModal(chineseF, chinaStart, chinaEnd, chinaFScale * 0.8))
    .toList

  val chineseMusic: List[Double => Double]  = (0 until 7)
    .map(_ => getQuadModal(chineseF, chinaStart, chinaEnd, chinaFScale * 0.3))
    .toList

  /////////////////////////////////////////////
  val usVar = 0.25

  val usEcommerce: List[Double => Double]  = (0 until 7)
    .map(_ => getNormalPDF(0.5, usVar))
    .toList

  val usGas: List[Double => Double]  = (0 until 7)
    .map(_ => getNormalPDF(0.5, usVar))
    .toList

  val usGroceries: List[Double => Double]  = List(
    getNormalPDF(0.5, usVar),
    getNormalPDF(0.5, usVar, 2.0),
    getNormalPDF(0.5, usVar),
    getNormalPDF(0.5, usVar),
    getNormalPDF(0.5, usVar),
    getNormalPDF(0.5, usVar),
    getNormalPDF(0.5, usVar),
  )

  val usMedicine: List[Double => Double]  = (0 until 7)
    .map(_ => getNormalPDF(0.5, usVar))
    .toList

  val usMusic: List[Double => Double]  = (0 until 7)
    .map(_ => getNormalPDF(0.5, usVar))
    .toList
  ///////////////////////////////////////////////
  val spainTimeDiff = 6
  val spainStartTime: Double = 14
  val spainEndTime: Double = 17
  val spainMidtime: Double = (spainStartTime + spainEndTime) / 2
  val sVar = 0.1
  val sScale = 0.6
  val sminusVar = 0.1
  val sMinusScale = 0.6

  val spainEcommerce: List[Double => Double] = (0 until 7)
    .map(_ => getSiestaFunction(spainStartTime, sVar, sScale, spainEndTime, sVar, sScale, spainMidtime, sminusVar, sMinusScale))
    .toList

  val spainGas: List[Double => Double]  = (0 until 7)
    .map(_ => getSiestaFunction(spainStartTime, sVar, sScale, spainEndTime, sVar, sScale, spainMidtime, sminusVar, sMinusScale))
    .toList

  val spainGroceries: List[Double => Double]  = List(
    getSiestaFunction(spainStartTime, sVar, sScale, spainEndTime, sVar, sScale, spainMidtime, sminusVar, sMinusScale),
    getSiestaFunction(spainStartTime, sVar, 2 * sScale, spainEndTime, sVar, 2 * sScale, spainMidtime, 0.05, 1),
    getSiestaFunction(spainStartTime, sVar, sScale, spainEndTime, sVar, sScale, spainMidtime, sminusVar, sMinusScale),
    getSiestaFunction(spainStartTime, sVar, sScale, spainEndTime, sVar, sScale, spainMidtime, sminusVar, sMinusScale),
    getSiestaFunction(spainStartTime, sVar, sScale, spainEndTime, sVar, sScale, spainMidtime, sminusVar, sMinusScale),
    getSiestaFunction(spainStartTime, sVar, sScale, spainEndTime, sVar, sScale, spainMidtime, sminusVar, sMinusScale),
    getSiestaFunction(spainStartTime, sVar, sScale, spainEndTime, sVar, sScale, spainMidtime, sminusVar, sMinusScale)
  )

  val spainMedicine: List[Double => Double]  = (0 until 7)
    .map(_ => getSiestaFunction(spainStartTime, sVar, sScale, spainEndTime, sVar, sScale, spainMidtime, sminusVar, sMinusScale))
    .toList

  val spainMusic: List[Double => Double]  = (0 until 7)
    .map(_ => getSiestaFunction(spainStartTime, sVar, sScale, spainEndTime, sVar, sScale, spainMidtime, sminusVar, sMinusScale))
    .toList


  def main(args: Array[String]): Unit = {
    compareTotals()
    printWeeklySum()

  }

  def compareTotals(): Unit = {
    val globalScale = 1
    val chinaScale = 2
    val usScale = 1
    val spainScale = 1

    val c = getChineseDaily()
    val u = getUSDaily()
    val s = getSpainDaily()
    val chinaSum = areaUnderCurve(c(0), 1) * chinaScale
    val usSum = areaUnderCurve(u(0), 1) * usScale
    val spainSum = areaUnderCurve(s(0), 1) * spainScale
    val all = chinaSum + usSum + spainSum
    println(chinaSum, usSum, spainSum, Math.ceil(all) * globalScale)
  }

  def printWeeklySum(): Unit = {
    val globalScale = 5
    val chinaScale = 1402
    val usScale = 330
    val spainScale = 47
    val weekly = (0 to 2).map(getDailySum(_, chinaScale, usScale, spainScale)).sum * globalScale
    println(s"Weekly sum: $weekly")
  }

  def getDailySum(day: Int, chinaScale: Double, usScale: Double, spainScale: Double): Double = {
    val c = getChineseDaily()
    val u = getUSDaily()
    val s = getSpainDaily()
    val chinaSum = MathHelper.areaUnderCurve(c(day), 1) * chinaScale
    val usSum = MathHelper.areaUnderCurve(u(day), 1) * usScale
    val spainSum = MathHelper.areaUnderCurve(s(day), 1) * spainScale
    chinaSum + usSum + spainSum
  }

}
