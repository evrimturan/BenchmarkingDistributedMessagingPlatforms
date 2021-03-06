//import util.properties packages
import java.util.Properties;

//import simple producer packages
import org.apache.kafka.clients.producer.Producer;

//import KafkaProducer packages
import org.apache.kafka.clients.producer.KafkaProducer;

//import ProducerRecord packages
import org.apache.kafka.clients.producer.ProducerRecord;

//Create java class named “SimpleProducer”
public class KafkaProducer {

    public static void main(String[] args){

        //Assign topicName to string variable
        String topicName = "failsafe";

        // create instance for properties to access producer configs
        Properties props = new Properties();

        //Assign localhost id
        props.put("bootstrap.servers", "159.89.102.49:9092");
        //props.put("bootstrap.servers", "159.65.120.184:9092");

        //Set acknowledgements for producer requests.
        props.put("acks", "all");

        //If the request fails, the producer can automatically retry,
        props.put("retries", 0);

        //Specify buffer size in config
        props.put("batch.size", 16384);

        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);

        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);

        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = null;
        try {
            producer = new org.apache.kafka.clients.producer.KafkaProducer<String, String>(props);

            for(int i = 0; i < 10; i++) {
                producer.send(new ProducerRecord<String, String>(topicName, "Message " + Integer.toString(i + 100)));
                System.out.println("Message sent successfully");
            }
        }

        catch (Exception e) {
            e.printStackTrace();

        }

        finally {
            producer.close();
        }

    }

}

