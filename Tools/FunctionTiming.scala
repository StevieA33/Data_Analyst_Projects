package com.Tools

/**
 * Usage:
 * Val s = start()
 * Call a function
 * end(s)
 */
object FunctionTiming{
  def start(): Long = { System.currentTimeMillis() }
  def end(a:Long): Unit ={
    val seconds = (System.currentTimeMillis() - a) / 1000.0
    val minutes = seconds / 60.0
    println(s"Function ran for ${System.currentTimeMillis() - a} milliseconds or $seconds seconds or $minutes minutes")
  }
}
