package com.Producer.Generators

import com.ProductOrder
import scala.util.Random

object PriceMultiplier {

  /**
   * Purpose:
   * @param file is the name of the CSV or other text file to be parsed.
   * @param x is the number of modifications (corruptions) to be p
   */

  def multiplyPrice(po: ProductOrder): ProductOrder = {
    val r = new Random()
    val min = 90
    val max = 110
    po.price = Math.floor(po.price * ((r.nextInt(max-min) + min) / 100.00) * 100) / 100
    return po
  }

}
