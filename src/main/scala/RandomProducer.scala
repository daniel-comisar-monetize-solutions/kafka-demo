package com.monetizesolutions.kafkademo

import java.util.Properties
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.KafkaException
import org.apache.kafka.common.errors._
import org.apache.kafka.common.serialization._

object RandomProducer extends App { 
  // TODO: read properties from arguments
  val topic = "test"
  val numRecords = 1000
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");
  props.put("transactional.id", "my-transactional-id")

  val producer = new KafkaProducer(props, new IntegerSerializer(), new StringSerializer())
  producer.initTransactions()
  try {
    producer.beginTransaction()
    for (_ <- 1 to numRecords) {
      val record = MathRecord.random
      println(record)
      producer.send(new ProducerRecord(topic, record.key, record.toString))
    }
    producer.commitTransaction()
  }
  catch {
    case e @ (_: ProducerFencedException | _: OutOfOrderSequenceException | _: AuthorizationException) =>
      println("Fatal error, exiting: " + e)
    case (e: KafkaException) =>
      // TODO: retry transaction
      println("Minor error, exiting: " + e)
  }
  producer.close()
}
