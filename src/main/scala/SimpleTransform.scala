package com.monetizesolutions.kafkademo

import java.util._
import org.apache.kafka.clients.consumer._
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.KafkaException
import org.apache.kafka.common.errors._
import org.apache.kafka.common.serialization._
import scala.collection.JavaConversions._
import scala.util.Try

object SimpleTransform extends App { 
  // TODO: read properties from arguments
  val consumerProperties = new Properties();
  consumerProperties.put("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");
  consumerProperties.put("group.id", "test-consumer-group");
  consumerProperties.put("enable.auto.commit", "true");
  consumerProperties.put("auto.commit.interval.ms", "1000");
  consumerProperties.put("key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
  consumerProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
  val consumer = new KafkaConsumer[String, String](consumerProperties);
  consumer.subscribe(Arrays.asList("test"))
  val topic = "test2"
  val producerProperties = new Properties()
  producerProperties.put("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");
  producerProperties.put("transactional.id", "my-transform-id")
  val producer = new KafkaProducer(producerProperties, new IntegerSerializer(), new StringSerializer())
  producer.initTransactions()

  try {
    while (true) {
      val assignment = "^([^=]+)=(-?[0-9]+(?:\\.[0-9]+)?)$".r
      val records = consumer.poll(100);
      producer.beginTransaction()
      for (record <- records) {
        record.value match {
          case assignment(variable, stringValue) => {
            val doubleValue = Try { stringValue.toDouble }
            if (doubleValue.isSuccess) {
              val newValue = 10.0 * Math.sqrt(doubleValue.get)
              val newRecord = new MathRecord(variable, newValue)
              println(newRecord)
              producer.send(new ProducerRecord(topic, newRecord.key, newRecord.toString))
            }
          }
        }
      }
      producer.commitTransaction()
    }
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
