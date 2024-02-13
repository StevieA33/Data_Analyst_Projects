package com.Producer.Generators

import scala.util.Random
import com.ProductOrder
import com.Tools.MathHelper

object MedicineGenerator {
  private final var medicineList = os.read.lines(os.pwd / "clean_data" /"medicine"/ "medicine_2021.txt")
    .drop(1)
    .filter(_.nonEmpty).toList

  // this fills in the product related fields of the ProductOrder object
  def getMedicine(po: ProductOrder):ProductOrder = {
    val ran = new Random()
    val rownum = Math.abs(ran.nextInt(medicineList.length))
    val row = medicineList(rownum).split("""\|""")
    val product = row(0)
    val price = Math.min(Math.max(row(2).toDouble, 0.49), 2000.0 + Random.nextDouble()*400)
    val quantity = Math.abs(ran.nextInt(10) + 1)
    val totalPrice = price * quantity
    po.product_id = Math.abs(product.hashCode() + 1)
    po.product_name = product
    po.product_category = "Medicine"
    po.price = MathHelper.roundDouble(Math.abs(totalPrice))
    po.qty = quantity
    po
  }
}
