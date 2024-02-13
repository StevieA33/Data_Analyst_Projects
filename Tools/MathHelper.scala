package com.Tools

import org.apache.spark.sql.DataFrame
import scala.util.Random

object MathHelper {
  def main(args: Array[String]): Unit = {
    val myGaussian: (Double) => Double = getNormalPDF(1/2, 1)
    myGaussian(1/2)
  }

  def chooseFromList[T](list: List[T]): T = {
    list(Random.nextInt(list.length))
  }

  def chooseFromWeightedList[T](list: List[T], probabilities: List[Double]): T = {
    val sum = probabilities.sum
    val normalized = probabilities.map(_ / sum)
    var rand = Random.nextDouble()

    //
    for ((l, p) <- list.zip(normalized)) {
      if (rand < p)
        return l
      else
        rand -= p
    }
    chooseFromList(list)
  }

  def functionToDataFrame(func: Double => Double, samples: Int = 10, initialX: Double = 0.0): DataFrame = {
    val spark = SparkHelper.spark
    import spark.implicits._

    val step = (1.0 - initialX) / samples
    val x = (initialX to 1.0 by step)
    val y = x.map(func)
    spark.sparkContext.parallelize(x.zip(y)).toDF()
  }

  def functionsToString(samples: Int, initialX: Double, funcs: Double => Double*): Unit = {
    val step = (1.0 - initialX) / samples
    val x = (initialX to 1.0 by step)

    println("%matplotlib inline")
    println("import matplotlib.pyplot as plt")
    println("plt.style.use('seaborn-whitegrid')")
    println("import numpy as np\n")
    println("fig = plt.figure()")
    println("ax = plt.axes()\n")

    println(s"x = (${x.mkString(", ")})")
    funcs
      .zipWithIndex.
      foreach(f => {
        val y = x.map(f._1)
        val i = f._2 + 1
        println(s"y$i = (${y.mkString(", ")})")
      })
    println()
    funcs.zipWithIndex
      .foreach(f => println(s"ax.plot(x, y${f._2 + 1});"))
  }

  def areaUnderCurve(function: Double => Double, minutesPerSample: Int = 15): Double = {
    val step = minutesPerSample/1440.0
    (0.0 to 1.0 by step).map(function(_)).sum * step
  }

  // Given a function, shift it ahead in time by hoursAhead
  // NOTE: F must eventually go to zero! e.g x^2 would break this
  def shiftTimezone(f: Double => Double, hoursAhead: Double): Double => Double = {
    val shift = hoursAhead / 24.0
    x: Double => f(x - shift + 1) max f(x - shift) max f(x - shift - 1)
  }

  def addFuncs(functions: Double => Double*): Double => Double = {
    functions.reduce((a, b) => (x: Double) => a(x) + b(x))
  }

  def subtractFunc(func1: Double => Double, func2: Double => Double): Double => Double = {
    x: Double => Math.max(func1(x) - func2(x), 0.01)
  }

  def scaleFunc(function: Double => Double, scale: Double): Double => Double = {
    (x: Double) => function(x) * scale
  }

  def getBimodalFunc(mean1: Double, var1: Double, scale1: Double,
                     mean2: Double, var2: Double, scale2: Double): Double => Double = {
    x: Double => Math.max(getNormalPDF(mean1, var1, scale1)(x), getNormalPDF(mean2, var2, scale2)(x))
  }
  def getQuadModal(f: Double => Double, startHour: Double, endHour: Double, endScale: Double = 1): Double => Double = {
    val start = startHour / 24.0
    val end = endHour / 24.0
    val scales = if (endScale == 1.0) List(1.0, 1.0, 1.0, 1.0) else (1.0 to endScale by (endScale - 1.0)/3)
//    (start to end by (end - start) / 4).zip(scales).foreach(println)

    val quadFuncs = (start to end by (end - start) / 4).zip(scales)
      .map(t => (x: Double) => f(x - t._1) * t._2)
    maxOfFunctions(quadFuncs:_*)
  }

  def getSiestaFunction(startTime: Double, var1: Double, scale1: Double,
                        endTime: Double, var2: Double, scale2: Double,
                        midTime: Double, mvar: Double, mscale: Double): Double => Double = {
    subtractFunc(
      getBimodalFunc(startTime/24, var1, scale1, endTime/24, var2, scale2),
      MathHelper.getNormalPDF(midTime/24, mvar, mscale))
  }

  def minOfFunctions(functions: Double => Double*): Double => Double = {
    functions.reduce((a, b) => (x: Double) => Math.min(a(x), b(x)))
  }

  def maxOfFunctions(functions: Double => Double*): Double => Double = {
    functions.reduce((a, b) => (x: Double) => Math.max(a(x), b(x)))
  }

  // https://www.itl.nist.gov/div898/handbook/eda/section3/eda3661.htm
  def getNormalPDF(mean: Double = 0, stdDev: Double = 1, scale: Double = 1): Double => Double = {
    (x: Double) => scale * Math.exp(-Math.pow(x - mean, 2) / (2 * Math.pow(stdDev, 2))) / (stdDev * Math.sqrt(2 * Math.PI))
  }

  def getLinearFunc(slope: Double, height: Double): Double => Double = {
    (x: Double) => slope * x + height
  }

  def getConstantFunc(height: Double): Double => Double = {
    (x: Double) => height
  }

  def getGaussianFunc(mean: Double, variance: Double): Double => Double = {
    (_: Double) => Random.nextGaussian() * variance + mean
  }

  def roundDouble(double: Double): Double = {
    Math.floor(double * 100) / 100
  }



}
