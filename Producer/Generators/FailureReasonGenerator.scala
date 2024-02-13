package com.Producer.Generators

import scala.collection.mutable.ListBuffer
import scala.io.Source
import com.ProductOrder

import java.time.LocalDateTime
import scala.util.Random

object FailureReasonGenerator {
  private final val reasoncard = "clean_data/transaction_failure_reasons_card.txt"
  private final val reasonother = "clean_data/transaction_failure_reasons_other.txt"
  private final val lbcard = new ListBuffer[String]
  private final val lbother = new ListBuffer[String]
  private final val ran = scala.util.Random
  for (line <- Source.fromFile(reasoncard).getLines()) {
    lbcard += line
  }
  for (line <- Source.fromFile(reasonother).getLines()) {
    lbother += line
  }

  def genFailReason(po: ProductOrder): Unit = {
    if (po.payment_txn_success == "N") {
      if (po.payment_type == "Card") {
        po.failure_reason = lbcard(ran.nextInt(lbcard.length))
      } else {
        po.failure_reason = lbother(ran.nextInt(lbother.length))
      }
    }
    else {
      po.failure_reason = "Payment Was Success"
    }
  }

  //  Proof of concept:
  def main(args: Array[String]): Unit = {
    val po = ProductOrder.getInitialOrder(LocalDateTime.now())
    println(ProductOrder.toString(po))
    val r = new Random()
    val ran = r.nextInt(10)
    ran match {
      case 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 => po.payment_txn_success = "Y"
      case _ => po.payment_txn_success = "N"
    }
    genFailReason(po)
    println(ProductOrder.toString(po))
  }

}
