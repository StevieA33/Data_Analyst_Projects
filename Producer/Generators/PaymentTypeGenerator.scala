package com.Producer.Generators

import com.ProductOrder

object PaymentTypeGenerator {
  def genPaymentType(po:ProductOrder): Unit = {
    val pTypes = List("Card", "Internet Banking", "UPI", "Wallet")
    val ran = scala.util.Random.nextInt(4)
    po.payment_type = pTypes(ran)
  }
}
