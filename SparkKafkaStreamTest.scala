package com

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.spark.sql.SparkSession
import org.apache.spark

import java.util.Properties

object SparkKafkaStreamTest extends App {
  val spark = SparkSession
    .builder
    .appName("SparkKafkaStreamingExample")
    .config("spark.master", "local")
    .getOrCreate()

  import spark.implicits._

  val coll = spark.sparkContext.parallelize(List((1,2,3), (4,5,6), (7,8,9))).toDF()

  val props:Properties = new Properties()
  props.put("bootstrap.servers","[::1]:9092")
  props.put("key.serializer",
    "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer",
    "org.apache.kafka.common.serialization.StringSerializer")
  props.put("acks","all")
  val producer = new KafkaProducer[String, String](props)
  val topic = "text_topic"
  try {
    val collStrings = coll.collect.map(_.getInt(2)).mkString(" ")
    val record = new ProducerRecord[String, String](topic, collStrings, collStrings)
    val metadata = producer.send(record)
    printf(s"sent record(key=%s value=%s) " +
      "meta(partition=%d, offset=%d)\n",
      record.key(), record.value(),
      metadata.get().partition(),
      metadata.get().offset())
  }catch{
    case e:Exception => e.printStackTrace()
  }finally {
    producer.close()
  }



















  //  val df = spark.readStream
  //    .format("kafka")
  //    .option("kafka.bootstrap.servers", "[::1]:9092")
  //    .option("subscribe", "TOPICNAME")
  //    .option("startingOffsets", "from-beginning") // Starts reading messages from the beginning.
  //    .load()

  spark.close()

}
