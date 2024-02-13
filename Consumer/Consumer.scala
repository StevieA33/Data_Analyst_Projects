package com.Consumer

import com.Producer.ProducerPipeline
import com.Producer.ProducerPipeline.useEC2
import com.Tools.SparkHelper
import org.apache.kafka.clients.consumer.KafkaConsumer

import java.util.Properties
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer


object Consumer extends App {

  val props: Properties = new Properties()
  props.put("group.id", ProducerPipeline.readerGroupID)
  if (useEC2)
    props.put("bootstrap.servers", "ec2-44-202-112-109.compute-1.amazonaws.com:9092")
  else
    props.put("bootstrap.servers", "[::1]:9092")

  props.put("key.deserializer",
    "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer",
    "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("enable.auto.commit", "true")
  props.put("auto.commit.interval.ms", "1000")
  val consumer = new KafkaConsumer(props)
  val topics = List(ProducerPipeline.readTopic)
  val path = ProducerPipeline.consumerPath

  val buffer: ListBuffer[String] = ListBuffer[String]()
  val bufferLimit = 50
  var isNewTable = true

  println(s"Consumer reading from topic: ${topics.head}")
  println(s"GroupID: ${ProducerPipeline.readerGroupID}")
  println(s"Writing into ${path.toString()}")

  try {
    consumer.subscribe(topics.asJava)
    while (true) {
      val records = consumer.poll(10000L)
      val size = records.asScala.toList.length
      if (size != 0) {
        println(size)
        buffer ++= records.asScala.map(_.value.toString)
      }
      if (buffer.size > bufferLimit) {
        val batch = buffer.toList
//        batch.foreach(println)
        buffer.clear()
        if (ProducerPipeline.writeToFileNotHDFS)
          os.write.append(path, batch.map(_ + "\n"), createFolders = true)
        else {
          val dataset = ConsumerParser.parseIntoDataSet(batch, ProducerPipeline.isTheirData)
          dataset.show()
          val mode = if (isNewTable) "overwrite" else "append"

          dataset
            .write
            .mode(mode)
            .option("header", "true")
            .option("delimiter", "|")
            .csv(ProducerPipeline.hdfsPath)
          isNewTable = false
        }
      }
    }
  } catch {
    case e: Exception => e.printStackTrace()
  } finally {
    consumer.close()
  }
}
