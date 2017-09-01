package com.monetizesolutions.kafkademo

import java.util.Properties
import org.apache.kafka.clients.consumer._
import org.apache.kafka.common.serialization.StringSerializer
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.util._
import slick.jdbc.SQLiteProfile.api._

object AggregateConsumer extends App {
  // TODO: read properties from arguments
  val consumerProperties = new Properties();
  consumerProperties.put("bootstrap.servers", "localhost:9092");
  consumerProperties.put("group.id", "database-consumer-group");
  consumerProperties.put("enable.auto.commit", "true");
  consumerProperties.put("auto.commit.interval.ms", "1000");
  consumerProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
  consumerProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
  val consumer = new KafkaConsumer[String, String](consumerProperties);
  consumer.subscribe(Array("test2"): Iterable[String])

  val db = Database.forURL("jdbc:sqlite:test.db", driver = "org.sqlite.JDBC")
  class Variables(tag: Tag) extends Table[(String, Double)](tag, "VARIABLES") {
    def name = column[String]("NAME", O.PrimaryKey)
    def value = column[Double]("VALUE")
    def * = (name, value)
  }
  val variables = TableQuery[Variables]

  // TODO: create if not exists
  Await.ready(db.run(variables.schema.create), 30.seconds)

  // TODO: prepared statement, check for failures
  while (true) {
    val assignment = "^([^=]+)=(-?[0-9]+(?:\\.[0-9]+)?)$".r
    val records = consumer.poll(10);
    for (record <- records) {
      record.value match {
        case assignment(variable, stringValue) => {
          val doubleValue = Try { stringValue.toDouble }
          if (doubleValue.isSuccess) {
            println(variable + "=" + doubleValue.get)
            val update = db.run(variables += (variable, doubleValue.get))
            Await.ready(update, 30.seconds)
          }
        }
      }
    }
  }
  db.close
}
