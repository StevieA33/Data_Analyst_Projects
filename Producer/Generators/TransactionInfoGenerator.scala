package com.Producer.Generators

import com.ProductOrder
import scala.util.Random

object TransactionInfoGenerator {
  var counter = 1000

  def addTransactionInfo(po: ProductOrder): ProductOrder = {
    val r = new Random()

    val min = 1000
    val max = 999999999

    //    Assigns order ID and transaction ID to po object.
    po.order_id = counter
    counter += 1
    po.payment_txn_id = Math.abs(r.nextInt(max-min).toLong + min) * 100
    //println(po.payment_txn_id)

    //  Calls Bao's paymentType function to set paymentType attribute of po object.
    PaymentTypeGenerator.genPaymentType(po)

    //   Assigns payment_txn_success status to po object.
    po.payment_txn_success = if(r.nextInt(10) == 9) "N" else "Y"
    //   Calls Bao's genFailReason function to assign failure reason to po object.
    FailureReasonGenerator.genFailReason(po)

    return po

  }
}
