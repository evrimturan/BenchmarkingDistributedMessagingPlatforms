import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Arrays;
import java.util.concurrent.*;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
//import org.apache.activemq.command.ActiveMQBytesMessage;
import com.rabbitmq.client.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.jms.*;

public class ClusterBenchmarker {
    //private final List<Boolean> finish = new ArrayList<>();
    private static int sentData = 0;

    private class Consumer{
        private int tNum;
        private String folderName;
        private String platform;
        private long totalTimeElapsed;
        private int queueNum;
        private String brokerIp;
        private javax.jms.Connection activemqConnection;
        private ActiveMQSession activemqSession;
        private com.rabbitmq.client.Connection rabbitmqConnection;
        private Channel rabbitmqChannel;
        private int fileNumber = 0;

        public long getTotalTimeElapsed() {
            return totalTimeElapsed;
        }

        public void run() {
            System.out.println(Thread.currentThread().getId() + " says hello consumer :)");
            if (platform.equals("activemq")) {
                try {
                    Queue dest = activemqSession.createQueue("queue-" + queueNum);
                    MessageConsumer consumer = activemqSession.createConsumer(dest);
                    System.out.println("queue-"+queueNum);
                    MessageListener listener = message -> {
                        try{
                            //System.out.println("LO LO LO");
                            if(message instanceof BytesMessage){
                                //FileOutputStream fos = new FileOutputStream(folderName + "/consumer.data-" + queueNum);
                                System.out.println("ACTIVEMQ CONSUMING FROM " + brokerIp);
                                /*long start = System.currentTimeMillis();

                                long end = System.currentTimeMillis();
                                totalTimeElapsed = end - start;
                                System.out.println("Consumed in " + totalTimeElapsed + " ms");*/
                                /*byte[] buffer = new byte[81920];

                                while ((((BytesMessage) message).readBytes(buffer)) != -1) {
                                    fos.write(buffer);
                                }*/
                                //fos.close();
                            }

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    };
                    consumer.setMessageListener(listener);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (platform.equals("rabbitmq")) {
                try {

                    rabbitmqChannel.queueDeclare("queue-" + queueNum, false, false, false, null);
                    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

                    com.rabbitmq.client.Consumer consumer = new DefaultConsumer(rabbitmqChannel) {
                        @Override
                        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                                throws IOException {

                            System.out.println("RABBITMQ CONSUMING FROM " + brokerIp);
                            /*
                            FileOutputStream fos = new FileOutputStream(folderName + "/consumer.data-" + fileNumber);
                            fileNumber++;

                            byte[] buffer = new byte[81920];

                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
                            while(byteArrayInputStream.read(buffer) != -1) {
                                fos.write(buffer);
                            }
                            fos.close();
                            */
                        }
                    };
                    rabbitmqChannel.basicConsume("queue-" + queueNum, true, consumer);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (platform.equals("kafka")) {
                Properties props = new Properties();
                props.put("bootstrap.servers", brokerIp + ":9092");
                props.put("group.id", "group-1");
                props.put("enable.auto.commit", "true");
                props.put("auto.commit.interval.ms", "1000");
                props.put("auto.offset.reset", "earliest");
                props.put("session.timeout.ms", "30000");
                props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
                org.apache.kafka.clients.consumer.KafkaConsumer<String, byte[]> consumer = null;
                try {
                    consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(props);
                    consumer.subscribe(Arrays.asList("queue-" + queueNum));
                    Thread.sleep(1000);
                    while(true){
                        ConsumerRecords<String, byte[]> records = consumer.poll(100);
                        for (ConsumerRecord<String, byte[]> record : records) {
                            System.out.println("CONSUMING FROM " + brokerIp);

                            /*FileOutputStream fos = new FileOutputStream(folderName + "/consumer.data-" + fileNumber);
                            fileNumber++;

                            byte[] buffer = new byte[81920];

                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(record.value());
                            while(byteArrayInputStream.read(buffer) != -1) {
                                fos.write(buffer);
                            }
                            fos.close();
                            */
                            /*synchronized(finish){
                                if(finish.get(queueNum)){
                                    System.out.println("FINISHED CONSUMER");
                                    consumer.unsubscribe();
                                    break;
                                }
                            }*/
                            sentData++;
                        }
                        if(sentData >= 30){
                            consumer.unsubscribe();
                            break;
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //long finish = System.currentTimeMillis();
        }

        public Consumer(int tNum, String folderName, String platform, int queueNum, String brokerIp) {
            this.tNum = tNum;
            this.folderName = folderName;
            this.platform = platform;
            this.queueNum = queueNum;
            this.brokerIp = brokerIp;

            if (platform.equals("activemq")) {
                try {
                    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://" + brokerIp + ":61616");
                    this.activemqConnection = connectionFactory.createConnection("admin", "admin");
                    activemqConnection.start();
                    this.activemqSession = (ActiveMQSession) activemqConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (platform.equals("rabbitmq")) {
                try {
                    com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
                    factory.setUsername("admin");
                    factory.setPassword("admin");
                    factory.setPort(5672);
                    factory.setHost(brokerIp);
                    this.rabbitmqConnection = factory.newConnection();
                    this.rabbitmqChannel = rabbitmqConnection.createChannel();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private class Producer {
        private long mSize;
        private long dSize;
        private int tNum;
        private String platform;
        private long totalTimeEllapsed;
        private int queueNum;
        private String folderName;
        private String brokerIp;
        private javax.jms.Connection activemqConnection;
        private ActiveMQSession activemqSession;
        private com.rabbitmq.client.Connection rabbitmqConnection;
        private Channel rabbitmqChannel;
        private String type;
        private Queue dest;
        private MessageProducer producer;
        private BytesMessage bMessage;
        private byte[] rabbitByteArray;
        private byte[] kafkaByteArray;
        private org.apache.kafka.clients.producer.Producer<String, byte[]> kafkaProducer;

        public long getTotalTimeEllapsed() {
            return totalTimeEllapsed;
        }

        public void run(){
            System.out.println(Thread.currentThread().getId()+" says hello producer :)");
            //long start = System.currentTimeMillis();
            if(platform.equals("activemq")){
                try{
                    while(true){
                        producer.send(bMessage);
                        System.out.println("ACTIVEMQ PRODUCED TO:  " + brokerIp);
                    }

                }catch(Exception e){
                    //e.printStackTrace();
                    try{
                        activemqSession.close();
                        activemqConnection.close();
                    }catch(Exception ex){ /*unimportant*/ }

                }
            }else if (platform.equals("rabbitmq")){
                try{
                    while(true) {
                        rabbitmqChannel.basicPublish("", "queue-"+queueNum, MessageProperties.PERSISTENT_TEXT_PLAIN, rabbitByteArray);
                        System.out.println("RABBITMQ PRODUCED TO:  "+brokerIp);
                    }

                }catch(Exception e){
                    e.printStackTrace();
                    try {
                        rabbitmqChannel.close();
                        rabbitmqConnection.close();
                    }
                    catch (Exception ex) {
                        e.printStackTrace();
                    }
                }
            }else if(platform.equals("kafka")){


                try {
                    while(true) {
                        kafkaProducer.send(new ProducerRecord<String, byte[]>("queue-"+queueNum, kafkaByteArray));
                        System.out.println("KAFKA PRODUCED TO:  "+brokerIp);
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                    try {
                        kafkaProducer.close();
                    }
                    catch (Exception ex) {
                        e.printStackTrace();
                    }
                }

            }
            
            //long finish = System.currentTimeMillis();
        }

        public Producer(long mSize, long dSize, int tNum,String folderName,String platform,int queueNum, String brokerIp, String type){
            this.mSize = mSize;
            this.dSize = dSize;
            this.tNum = tNum;
            this.platform = platform;
            this.queueNum = queueNum;
            this.folderName = folderName;
            this.brokerIp = brokerIp;
            this.type = type;

            if(platform.equals("activemq")){
                try{
                    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://"+brokerIp+":61616");
                    connectionFactory.setProducerWindowSize((int)dSize);
                    this.activemqConnection = connectionFactory.createConnection("admin","admin");
                    activemqConnection.start();
                    this.activemqSession = (ActiveMQSession)activemqConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                    dest = activemqSession.createQueue("queue-"+queueNum);
                    producer = activemqSession.createProducer(dest);
                    producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

                    FileInputStream in = new FileInputStream(new File(folderName+"/producer.data-"+type));

                    byte[] buffer = new byte[81920];
                    bMessage = activemqSession.createBytesMessage();

                    //System.out.println("--------------------------\nStarted writing to file\n-----------------------");
                    while(in.read(buffer) != -1){
                        bMessage.writeBytes(buffer);
                    }
                    in.close();

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
                    this.rabbitmqConnection = factory.newConnection();
                    this.rabbitmqChannel = rabbitmqConnection.createChannel();

                    rabbitmqChannel.queueDeclare("queue-"+queueNum, true, false, false, null);

                    FileInputStream in = new FileInputStream(new File(folderName+"/producer.data-"+type));

                    byte[] buffer = new byte[81920];

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    while(in.read(buffer) != -1) {
                        outputStream.write(buffer);
                    }
                    in.close();
                    rabbitByteArray = outputStream.toByteArray();
                    outputStream.close();

                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            else if(platform.equals("kafka")) {
                Properties props = new Properties();
                props.put("bootstrap.servers", brokerIp +":9092");
                props.put("acks", "all");
                props.put("retries", 0);
                props.put("batch.size", 16384);
                props.put("linger.ms", 1);
                props.put("buffer.memory", 33554432);

                props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");

                props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

                kafkaProducer = null;

                try {
                    kafkaProducer = new org.apache.kafka.clients.producer.KafkaProducer<String, byte[]>(props);

                    FileInputStream in = new FileInputStream(new File(folderName+"/producer.data-"+type));

                    byte[] buffer = new byte[81920];

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    while(in.read(buffer) != -1) {
                        outputStream.write(buffer);
                    }
                    in.close();
                    kafkaByteArray = outputStream.toByteArray();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {

        ClusterBenchmarker init = new ClusterBenchmarker();
        TestConfiguration config = new TestConfiguration(args[0]);
        int brokerNum = config.getBrokerNum();
        int pubNum = config.getPubNum();
        int subNum = config.getSubNum();
        long messageSize = config.getMessageSize();
        long dataSize = config.getDataSize();
        int topicNum = config.getTopicNum();
        List<TestConfiguration.BrokerInfo> bInfo = config.getBInfo();
        List<Producer> pList = new ArrayList<>();
        List<Consumer> cList = new ArrayList<>();

        try{
            Process broker = null;
            switch(config.getPlatform()){
                case "activemq":
                    broker = Runtime.getRuntime().exec("scripts/start-activemq-brokers.sh 123456mem " + brokerNum );
                    break;
                case "rabbitmq":
                    broker = Runtime.getRuntime().exec("scripts/start-rabbitmq-brokers.sh 123456mem " + brokerNum);
                    break;
                case "kafka":
                    broker = Runtime.getRuntime().exec("scripts/start-kafka-brokers.sh 123456mem " + brokerNum);
                    break;
                default:
                    System.err.println("Platform mismatch.");
                    System.exit(1);
                    break;
            }
            broker.waitFor();
            Thread.sleep(20000); //wait for brokers to complete setup
            System.out.println("Brokers started.");

        }catch(IOException | InterruptedException ex){
            ex.printStackTrace();
            System.exit(1);
        }
        if(config.getPubOrSub().equals("producer")){
            for(int i=0; i<pubNum; i++) {
                Random r = new Random();
                int bId = r.nextInt(brokerNum - 0);
                String bIp = bInfo.get(bId).getIp();

                Producer p = init.createProducer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + i), config.getPlatform(), i, bIp, config.getType());

                Path path = Paths.get("ProducerFolder" + "-" + i);

                if (!Files.exists(path)) {
                    File folder = new File("ProducerFolder" + "-" + i);
                    folder.mkdir();
                }

                try {
                    Process process = Runtime.getRuntime().exec("scripts/data-generator.sh " + p.dSize / p.mSize + " " + p.mSize + " " + "ProducerFolder-" + i);
                    //System.out.println("sh -c \"scripts/data-generator.sh " + p.dSize/p.mSize + " " + p.mSize + " " +  System.getProperty("user.dir")+"/scripts/ProducerFolder-"+i+"\"");
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                pList.add(p);
            }
            ScheduledExecutorService ex = Executors.newScheduledThreadPool(pList.size());
            List<Thread> threadList = new ArrayList<>();
            for(Producer p : pList) {
                Callable<Void> call = () -> {
                    System.out.println("Running producer AGAIN");
                    p.run();
                    return null;
                };
                Thread temp = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ex.invokeAll(Arrays.asList(call),10,TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                threadList.add(temp);
                temp.start();

                /*final Future handler = ex.submit(call);
                ex.schedule(() -> {
                    handler.cancel(true); // bura sıkıntılı
                },10,TimeUnit.SECONDS);*/
                System.out.println("--------------------------------");
            }

            for(Thread e : threadList){
                try {
                    e.join();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            ex.shutdown();
        }else if(config.getPubOrSub().equals("consumer")){
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
            /*
            for(Consumer c : cList){
                c.start();
            }
            */
        }


        //System.out.println("BURAYA GELDI");
        //System.out.println("All threads finished.");
    }

    private Producer createProducer(long mSize,long dSize,int tNum,String folderName,String platform,int queueNum, String brokerIp, String type) {  return new Producer(mSize,dSize,tNum,folderName,platform,queueNum,brokerIp,type); }
    private Consumer createConsumer(int tNum,String folderName,String platform,int queueNum, String brokerIp) {  return new Consumer(tNum,folderName,platform,queueNum,brokerIp); }

    //TODO: topic sayisi ve pubs/subs sayisi degisince hangi queueya message atilip cekilecek,broker connection refuse olunca başka brokera bağlanmayı denesin, kafka consumer global olsun
}
