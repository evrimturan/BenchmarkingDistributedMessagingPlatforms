import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Arrays;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.command.ActiveMQBytesMessage;
import com.rabbitmq.client.*;
//import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.jms.*;

public class ClusterBenchmarker {

    private class Consumer extends Thread{
        private int tNum;
        private String folderName;
        private String platform;
        private long totalTimeElapsed;
        private int queueNum;
        private final static String QUEUE_NAME = "hello";

        public long getTotalTimeElapsed() {
            return totalTimeElapsed;
        }

        @Override
        public void run(){
            System.out.println(Thread.currentThread().getId()+" says hello consumer :)");
            if(platform.equals("activemq")){
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://ubuntu-s-1vcpu-1gb-fra1-01:61616");
                try{
                    FileOutputStream fos = new FileOutputStream(folderName+"/consumer.data-"+queueNum);
                    javax.jms.Connection connection = connectionFactory.createConnection("admin","admin");
                    connection.start();
        
                    // Create a Session
                    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    
                    Queue dest = session.createQueue("queue-"+queueNum);
    
                    MessageConsumer consume = session.createConsumer(dest);
                    //System.out.println("LO LO LO");
                    long start = System.currentTimeMillis();
                    ActiveMQBytesMessage rc =  (ActiveMQBytesMessage)consume.receive(100);
                    long end = System.currentTimeMillis();
                    totalTimeElapsed = end-start;
                    System.out.println("Consumed in "+ totalTimeElapsed +" ms");
                    //System.out.println("LO LO LO bitti");
                    byte[] buffer = new byte[81920];
                    
                    while((rc.readBytes(buffer)) != -1){
                        fos.write(buffer);
                    }
                    fos.close();
                    session.close();
                    connection.close();

                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }else if (platform.equals("rabbitmq")){
                try{
                    com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
                    factory.setUsername("admin");
                    factory.setPassword("admin");
                    factory.setPort(5672);
                    factory.setHost("159.65.120.184");
                    com.rabbitmq.client.Connection connection = factory.newConnection();
                    Channel channel = connection.createChannel();

                    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

                    com.rabbitmq.client.Consumer consumer = new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                            String message = new String(body, "UTF-8");
                            System.out.println(" [x] Received '" + message + "'");
                        }
                    };
                    channel.basicConsume(QUEUE_NAME, true, consumer);   
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }else if (platform.equals("kafka")){
                Properties props = new Properties();
                props.put("bootstrap.servers", "159.65.120.184:9092");
                //props.put("bootstrap.servers", "159.89.102.49:9092");
                props.put("group.id", "group-1");
                props.put("enable.auto.commit", "true");
                props.put("auto.commit.interval.ms", "1000");
                props.put("auto.offset.reset", "earliest");
                props.put("session.timeout.ms", "30000");
                props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                org.apache.kafka.clients.consumer.KafkaConsumer<String, String> consumer = null;
                try {
                    consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<String, String>(props);
                    consumer.subscribe(Arrays.asList("failsafe"));
                    ConsumerRecords<String, String> records = consumer.poll(100);
                    for (ConsumerRecord<String, String> record : records) {
                        System.out.println("Received = " + record.value());
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //long finish = System.currentTimeMillis();
        }

        public Consumer(int tNum,String folderName,String platform,int queueNum){
            this.tNum = tNum;
            this.folderName = folderName;
            this.platform = platform;
            this.queueNum = queueNum;
        }
    }

    private class Producer extends Thread{
        private double mSize;
        private double dSize;
        private int tNum;
        private String platform;
        private long totalTimeEllapsed;
        private int queueNum;
        private String folderName;
        private final static String QUEUE_NAME = "hello";

        public long getTotalTimeEllapsed() {
            return totalTimeEllapsed;
        }

        @Override
        public void run(){
            System.out.println(Thread.currentThread().getId()+" says hello producer :)");
            //long start = System.currentTimeMillis();
            if(platform.equals("activemq")){
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://ubuntu-s-1vcpu-1gb-fra1-01:61616");
                connectionFactory.setProducerWindowSize((int)dSize);
                try{
                    FileInputStream in = new FileInputStream(new File(folderName+"/producer.data-"+queueNum));
                    javax.jms.Connection connection = connectionFactory.createConnection("admin","admin");
                    connection.start();

                    // Create a Session
                    ActiveMQSession session = (ActiveMQSession)connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                    Queue dest = session.createQueue("queue-"+queueNum);
                    MessageProducer producer = session.createProducer(dest);

                    byte[] buffer = new byte[81920];
                    BytesMessage bMessage = session.createBytesMessage();
                    
                    int content;
                    System.out.println("--------------------------\nStarted writing to file\n-----------------------");
                    while((content = in.read(buffer)) != -1){
                        bMessage.writeBytes(buffer);
                    }
                    producer.send(bMessage);
                    in.close();
                    // Clean up
                    session.close();
                    connection.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }else if (platform.equals("rabbitmq")){
                try{
                    com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
                    factory.setUsername("admin");
                    factory.setPassword("admin");
                    factory.setPort(5672);
                    factory.setHost("159.89.102.49");
                    com.rabbitmq.client.Connection connection = factory.newConnection();
                    Channel channel = connection.createChannel();

                    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                    String message = "Hello World!";
                    channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
                    System.out.println(" [x] Sent '" + message + "'");

                    channel.close();
                    connection.close();
                }catch(Exception e){

                }
            }else if(platform.equals("kafka")){
                String topicName = "failsafe";
                Properties props = new Properties();
                props.put("bootstrap.servers", "159.89.102.49:9092");
                //props.put("bootstrap.servers", "159.65.120.184:9092");
                props.put("acks", "all");
                props.put("retries", 0);
                props.put("batch.size", 16384);
                props.put("linger.ms", 1);
                props.put("buffer.memory", 33554432);

                props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");

                props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

                org.apache.kafka.clients.producer.Producer<String, String> producer = null;
                try {
                    producer = new org.apache.kafka.clients.producer.KafkaProducer<String, String>(props);

                    for(int i = 0; i < 10; i++) {
                        producer.send(new ProducerRecord<String, String>(topicName, "Message " + Integer.toString(i + 100)));
                        System.out.println("Message sent successfully");
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }

                finally {
                    producer.close();
                }

            }
            
            //long finish = System.currentTimeMillis();
        }

        public Producer(double mSize, double dSize, int tNum,String folderName,String platform,int queueNum){
            this.mSize = mSize;
            this.dSize = dSize;
            this.tNum = tNum;
            this.platform = platform;
            this.queueNum = queueNum;
            this.folderName = folderName;
        }
    }

    public static void main(String[] args) {

        ClusterBenchmarker init = new ClusterBenchmarker();
        TestConfiguration config = new TestConfiguration(args[0]);
        int brokerNum = config.getBrokerNum();
        int pubNum = config.getPubNum();
        int subNum = config.getSubNum();
        double messageSize = config.getMessageSize();
        double dataSize = config.getDataSize();
        int topicNum = config.getTopicNum();
        List<Producer> pList = new ArrayList<>();
        List<Consumer> cList = new ArrayList<>();

        try{
            Process broker = null;
            switch(config.getPlatform()){
                case "activemq":
                    broker = Runtime.getRuntime().exec("sh -c \"scripts/start-activemq-brokers.sh 123456mem " + brokerNum + "\"");
                    break;
                case "rabbitmq":
                    broker = Runtime.getRuntime().exec("sh -c \"scripts/start-rabbitmq-brokers.sh 123456mem " + brokerNum + "\"");
                    break;
                case "kafka":
                    broker = Runtime.getRuntime().exec("sh -c \"scripts/start-kafka-brokers.sh 123456mem " + brokerNum + "\"");
                    break;
                default:
                    System.err.println("Platform mismatch.");
                    System.exit(1);
                    break;
            }
            broker.waitFor();
            System.out.println("Brokers started.");

        }catch(IOException | InterruptedException ex){
            ex.printStackTrace();
            System.exit(1);
        }
        
        for(int i=0; i<pubNum; i++){
            Producer p = init.createProducer(messageSize,dataSize/pubNum,topicNum,("ProducerFolder-"+i),config.getPlatform(),i);

            Path path = Paths.get("ProducerFolder"+"-"+i);

            if (!Files.exists(path)) {
                File folder = new File("ProducerFolder"+"-"+i);
                folder.mkdir();
            }
            pList.add(p);
        }

        for(Producer p : pList){
            p.run();
        }

        for(int i=0; i<subNum; i++){
            Path path = Paths.get("ConsumerFolder"+"-"+i);

            if (!Files.exists(path)) {
                File folder = new File("ConsumerFolder"+"-"+i);
                folder.mkdir();
            }

            Consumer c = init.createConsumer(topicNum,("ConsumerFolder"+"-"+i),config.getPlatform(),i);
            cList.add(c);

        }

        for(Producer p : pList){
            try {
                p.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(Consumer c : cList){
            c.run();
        }
        /* TODO: subNum kadar thread daha oluşturulacak
        *  TODO: tüm thread ler run edilecek
        *  TODO: producer thread'inde klasör sonuna geldiği anlaşılacak ve ondan sonra data memory'e alınacak
        *  TODO: System.getmillis ile zaman ölçülecek ve consumer thread ları bittikten sonra zaman alınıp farkı alınacak
        *
        */
        for(Consumer c : cList){
            try {
                c.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("All threads finished.");
    }

    private Producer createProducer(double mSize,double dSize,int tNum,String folderName,String platform,int queueNum) {  return new Producer(mSize,dSize,tNum,folderName,platform,queueNum); }
    private Consumer createConsumer(int tNum,String folderName,String platform,int queueNum) {  return new Consumer(tNum,folderName,platform,queueNum); }
}
