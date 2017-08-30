import java.util.Properties
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.KafkaException
import org.apache.kafka.common.errors._
import org.apache.kafka.common.serialization.StringSerializer
import scala.util.Random

object RandomProducer extends App { 
  class Record(val variable: Char, val value: Double) {
    override def toString: String = variable + "=" + value
  }

  object Record {
    private val letters = ((0 to 25) zip ('a' to 'z')).toMap
    private def randomLetter = letters(Random.nextInt(26))
    private def randomNumber = Random.nextInt(100)
    def random = new Record(randomLetter, randomNumber)
  }

  // TODO: read properties from arguments
  val topic = "test"
  val key = "0" // determines partition only
  val numRecords = 50
  var props = new Properties();
  props.put("bootstrap.servers", "localhost:9092");
  props.put("transactional.id", "my-transactional-id");

  val producer = new KafkaProducer(props, new StringSerializer(), new StringSerializer());
  producer.initTransactions();
  try {
    producer.beginTransaction();
    for (_ <- 1 to numRecords) {
      producer.send(new ProducerRecord(topic, key, Record.random.toString));
    }
    producer.commitTransaction();
  }
  catch {
    case e @ (_: ProducerFencedException | _: OutOfOrderSequenceException | _: AuthorizationException) =>
      println("Fatal error, exiting: " + e)
    case (e: KafkaException) =>
      // TODO: retry transaction
      println("Minor error, exiting: " + e)
  }
  producer.close();
}
