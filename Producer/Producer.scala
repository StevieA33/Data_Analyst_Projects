package com.Producer

import com.Producer.ProducerPipeline.useEC2
import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object Producer{

  val props:Properties = new Properties()
  if (useEC2)
    props.put("bootstrap.servers","ec2-44-202-112-109.compute-1.amazonaws.com:9092")
  else
    props.put("bootstrap.servers","[::1]:9092")

  props.put("key.serializer",
    "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer",
    "org.apache.kafka.common.serialization.StringSerializer")
  props.put("acks","all")
//  props.put("batch.size", "5")
  val producer = new KafkaProducer[String, String](props)
  // TODO: Change this to team2
  val topic = ProducerPipeline.writeTopic

  def send(pStr:String) {
    try {
      val key = Math.abs(pStr.hashCode).toString
      val record = new ProducerRecord[String, String](topic, key, pStr)

      val metadata = producer.send(record)

//      printf(s"sent record(key=%s value=%s) " +
//        "meta(partition=%d, offset=%d)\n",
//        record.key(), record.value(),
//        metadata.get().partition(),
//        metadata.get().offset())
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      //producer.close()
    }
  }
}

