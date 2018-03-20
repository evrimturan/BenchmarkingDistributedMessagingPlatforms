//import util.properties packages
import java.util.Arrays;
import java.util.Properties;

//import KafkaConsumer packages
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;


public class Kafka_Consumer {

    public static void main(String[] args){

        Properties props = new Properties();
        props.put("bootstrap.servers", "159.89.102.49:9092");
        props.put("group.id", "group-1");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "earliest");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = null;
        try {
            consumer = new KafkaConsumer<String, String>(props);
            consumer.subscribe(Arrays.asList("musa"));
            while (true) {
                Thread.sleep(2000);
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("value = " + record.value());
                }
                System.out.println("Here");
            }
        }

        catch (Exception e) {
            e.printStackTrace();

        }

    }

}
