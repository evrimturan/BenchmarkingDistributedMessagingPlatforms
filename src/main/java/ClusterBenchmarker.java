import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
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
        private String brokerIp;
        private final static String QUEUE_NAME = "hello";

        public long getTotalTimeElapsed() {
            return totalTimeElapsed;
        }

        @Override
        public void run(){
            System.out.println(Thread.currentThread().getId()+" says hello consumer :)");
            if(platform.equals("activemq")){
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://"+brokerIp+":61616");
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
                    System.out.println("CONSUMING FROM "+brokerIp);
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
                    factory.setHost(brokerIp);
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
                            System.out.println("CONSUMING FROM "+brokerIp);
                        }
                    };
                    channel.basicConsume(QUEUE_NAME, true, consumer);   
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }else if (platform.equals("kafka")){
                Properties props = new Properties();
                props.put("bootstrap.servers", brokerIp+":9092");
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
                        System.out.println("CONSUMING FROM "+brokerIp);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //long finish = System.currentTimeMillis();
        }

        public Consumer(int tNum,String folderName,String platform,int queueNum, String brokerIp){
            this.tNum = tNum;
            this.folderName = folderName;
            this.platform = platform;
            this.queueNum = queueNum;
            this.brokerIp = brokerIp;
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
        private String brokerIp;
        private javax.jms.Connection activemqConnection;
        private ActiveMQSession activemqsession;
        private com.rabbitmq.client.Connection rabbitmqConnection;
        private Channel rabbitmqChannel;

        public long getTotalTimeEllapsed() {
            return totalTimeEllapsed;
        }

        @Override
        public void run(){
            System.out.println(Thread.currentThread().getId()+" says hello producer :)");
            //long start = System.currentTimeMillis();
            if(platform.equals("activemq")){
                try{
                    Queue dest = activemqsession.createQueue("queue-"+queueNum);
                    MessageProducer producer = activemqsession.createProducer(dest);

                    for (int i=0; i<Math.pow(2, dSize-mSize); i++) {
                        FileInputStream in = new FileInputStream(new File(folderName+"/producer.data-"+i));


                        byte[] buffer = new byte[81920];
                        BytesMessage bMessage = activemqsession.createBytesMessage();

                        int content;
                        System.out.println("--------------------------\nStarted writing to file\n-----------------------");
                        while((content = in.read(buffer)) != -1){
                            bMessage.writeBytes(buffer);
                        }
                        producer.send(bMessage);
                        System.out.println("ActiveMQ PRODUCED TO:  "+brokerIp);
                        in.close();
                    }

                    activemqsession.close();
                    activemqConnection.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else if (platform.equals("rabbitmq")){
                try{

                    rabbitmqChannel.queueDeclare("queue-"+queueNum, false, false, false, null);

                    for (int i=0; i<Math.pow(2, dSize-mSize); i++) {
                        FileInputStream in = new FileInputStream(new File(folderName+"/producer.data-"+i));


                        byte[] buffer = new byte[81920];

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        while(in.read(buffer) != -1) {
                            outputStream.write(buffer);
                        }
                        rabbitmqChannel.basicPublish("", "queue-"+queueNum, null, outputStream.toByteArray());
                        System.out.println("RabbitMQ PRODUCED TO:  "+brokerIp);
                        in.close();
                    }

                    rabbitmqChannel.close();
                    rabbitmqConnection.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else if(platform.equals("kafka")){
                Properties props = new Properties();
                props.put("bootstrap.servers", brokerIp +":9092");
                props.put("acks", "all");
                props.put("retries", 0);
                props.put("batch.size", 16384);
                props.put("linger.ms", 1);
                props.put("buffer.memory", 33554432);

                props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");

                props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

                org.apache.kafka.clients.producer.Producer<String, byte[]> producer = null;
                try {
                    producer = new org.apache.kafka.clients.producer.KafkaProducer<String, byte[]>(props);

                    for (int i=0; i<Math.pow(2, dSize-mSize); i++) {
                        FileInputStream in = new FileInputStream(new File(folderName+"/producer.data-"+i));


                        byte[] buffer = new byte[81920];

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        while(in.read(buffer) != -1) {
                            outputStream.write(buffer);
                        }

                        producer.send(new ProducerRecord<String, byte[]>("queue-"+queueNum, buffer));

                        System.out.println("Kafka PRODUCED TO:  "+brokerIp);
                        in.close();
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

        public Producer(double mSize, double dSize, int tNum,String folderName,String platform,int queueNum, String brokerIp){
            this.mSize = mSize;
            this.dSize = dSize;
            this.tNum = tNum;
            this.platform = platform;
            this.queueNum = queueNum;
            this.folderName = folderName;
            this.brokerIp = brokerIp;

            if(platform.equals("activemq")){
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://"+brokerIp+":61616");
                connectionFactory.setProducerWindowSize((int)Math.pow(2, dSize));
                try{
                    this.activemqConnection = connectionFactory.createConnection("admin","admin");
                    activemqConnection.start();
                    this.activemqsession = (ActiveMQSession)activemqConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            else if(platform.equals("rabbitmq")) {
                try{
                    com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
                    factory.setUsername("admin");
                    factory.setPassword("admin");
                    factory.setPort(5672);
                    factory.setHost(brokerIp);
                    rabbitmqConnection = factory.newConnection();
                    rabbitmqChannel = rabbitmqConnection.createChannel();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            else if(platform.equals("kafka")) {

            }
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
        List<TestConfiguration.BrokerInfo> bInfo = config.getBInfo();
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
            Random r = new Random();
            int bId = r.nextInt(brokerNum - 0);
            String bIp = bInfo.get(bId).getIp();

            Producer p = init.createProducer(messageSize,dataSize/pubNum,topicNum,("ProducerFolder-"+i),config.getPlatform(),i, bIp);

            Path path = Paths.get("ProducerFolder"+"-"+i);

            if (!Files.exists(path)) {
                File folder = new File("ProducerFolder"+"-"+i);
                folder.mkdir();
            }

            try {
                Process process = Runtime.getRuntime().exec("sh -c \"scripts/data-generator.sh " + Math.pow(2, (p.dSize-p.mSize)) + " " + p.mSize);
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
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

            Random r = new Random();
            int bId = r.nextInt(brokerNum - 0);
            String bIp = bInfo.get(bId).getIp();
           
            Consumer c = init.createConsumer(topicNum,("ConsumerFolder"+"-"+i),config.getPlatform(),i, bIp);
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

    private Producer createProducer(double mSize,double dSize,int tNum,String folderName,String platform,int queueNum, String brokerIp) {  return new Producer(mSize,dSize,tNum,folderName,platform,queueNum,brokerIp); }
    private Consumer createConsumer(int tNum,String folderName,String platform,int queueNum, String brokerIp) {  return new Consumer(tNum,folderName,platform,queueNum,brokerIp); }
}
