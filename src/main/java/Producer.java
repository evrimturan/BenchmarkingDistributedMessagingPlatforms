import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaFuture;

import javax.jms.*;
import java.io.*;
import java.util.*;

@SuppressWarnings("InfiniteLoopStatement")
public class Producer {
    private long mSize;
    private long dSize;
    private int tNum;
    private String platform;
    private long totalTimeEllapsed;
    private List<Integer> queueNum;
    private String folderName;
    private String brokerIp;
    private javax.jms.Connection activemqConnection;
    private Session activemqSession;
    private com.rabbitmq.client.Connection rabbitmqConnection;
    private Channel rabbitmqChannel;
    private String type;
    private BytesMessage bMessage;
    private byte[] rabbitByteArray;
    private byte[] kafkaByteArray;
    private org.apache.kafka.clients.producer.Producer<String, byte[]> kafkaProducer;
    private int counter = 0;
    private String id;
    private MessageProducer activemqProducer;
    private static boolean deleteTopics = false;

    public static boolean isDeleteTopics() {
        return deleteTopics;
    }

    public static void setDeleteTopics(boolean deleteTopics) {
        Producer.deleteTopics = deleteTopics;
    }

    public long getTotalTimeEllapsed() {
        return totalTimeEllapsed;
    }

    public void run(){
        System.out.println(Thread.currentThread().getId()+" says hello Producer :)");
        //long start = System.currentTimeMillis();
        if(platform.equals("activemq")){
            try{
                System.out.println(queueNum.size());
                while(true){
                    for (Integer aQueueNum : queueNum) {
                        //String queue = "queue-" + aQueueNum;
                        //System.out.println(producers.get(queue));
                        activemqProducer.send(activemqSession.createQueue("queue-"+aQueueNum),bMessage);
                        counter = getCounter() + 1;
                        //System.out.println("ACTIVEMQ PRODUCED TO:  " + brokerIp + " to queue "+ queueNum.get(0)); removed for now
                    }
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
                    for (Integer aQueueNum : queueNum) {
                        rabbitmqChannel.basicPublish("", "queue-" + aQueueNum, MessageProperties.PERSISTENT_TEXT_PLAIN, rabbitByteArray);
                        counter = getCounter() + 1;
                        System.out.println("RABBITMQ PRODUCED TO:  " + brokerIp);
                    }
                }

            }catch(Exception e){
                //e.printStackTrace();
                try {
                    rabbitmqConnection.close();
                    rabbitmqChannel.close();
                }
                catch (Exception ex) {
                    e.printStackTrace();
                }
            }
        }else if(platform.equals("kafka")){
            try {
                while(true) {
                    for(Integer a : queueNum){
                        kafkaProducer.send(new ProducerRecord<>("queue-" + a, kafkaByteArray));
                        counter = getCounter() + 1;
                        System.out.println("KAFKA PRODUCED TO:  "+brokerIp);
                    }
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

    public void shutdown(){
        switch (platform) {
            case "activemq":
                try {
                    activemqProducer.close();
                    activemqSession.close();
                    activemqConnection.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case "rabbitmq":
                try {
                    rabbitmqChannel.close();
                    rabbitmqConnection.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case "kafka":
                kafkaProducer.close();
                break;
        }
    }

    Producer(long mSize, long dSize, int tNum, String folderName, String platform, List<Integer> queueNum, String brokerIp, String type, String id){
        this.mSize = mSize;
        this.dSize=dSize;
        this.tNum = tNum;
        this.platform = platform;
        this.queueNum = new ArrayList<>();
        this.folderName = folderName;
        this.brokerIp = brokerIp;
        this.type = type;
        this.id = id;
        this.queueNum.addAll(queueNum);
// normal socket aç
// bağlan connect consumer

        switch (platform) {
            case "activemq":
                try {
                    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://" + brokerIp + ":61616");
                    connectionFactory.setProducerWindowSize((int) dSize);
                    this.activemqConnection = connectionFactory.createConnection("admin", "admin");
                    activemqConnection.start();
                    this.activemqSession = activemqConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                    activemqProducer = activemqSession.createProducer(null);
                    activemqProducer.setDeliveryMode(DeliveryMode.PERSISTENT);

                    //activemqProducer = activemqSession.createProducer(activemqSession.createTemporaryQueue());

                    /*for (Integer aQueueNum : queueNum) {
                        String queue = "queue-" + aQueueNum;
                        System.out.println("adding to hashmap :"+queue);
                        Destination destination = activemqSession.createQueue(queue);
                        producers.put(queue, destination);
                    }*/

                    FileInputStream in = new FileInputStream(new File(folderName + "/producer.data-" + type));

                    byte[] buffer = new byte[81920];
                    bMessage = activemqSession.createBytesMessage();

                    //System.out.println("--------------------------\nStarted writing to file\n-----------------------");
                    while (in.read(buffer) != -1) {
                        bMessage.writeBytes(buffer);
                    }
                    in.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case "rabbitmq":
                try {
                    com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
                    factory.setUsername("admin");
                    factory.setPassword("admin");
                    factory.setPort(5672);
                    factory.setHost(brokerIp);
                    this.rabbitmqConnection = factory.newConnection();
                    this.rabbitmqChannel = rabbitmqConnection.createChannel();

                    for(Integer a : queueNum){
                        rabbitmqChannel.queueDeclare("queue-" + a, true, false, false, null);
                    }

                    FileInputStream in = new FileInputStream(new File(folderName + "/producer.data-" + type));

                    byte[] buffer = new byte[81920];

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    while (in.read(buffer) != -1) {
                        outputStream.write(buffer);
                    }
                    in.close();
                    rabbitByteArray = outputStream.toByteArray();
                    outputStream.close();

                } catch (Exception e) {
                    System.exit(1);
                    e.printStackTrace();
                }
                break;
            case "kafka":
                Properties props = new Properties();
                props.put("bootstrap.servers", brokerIp + ":9092");
                props.put("acks", "all");
                props.put("retries", 0);
                props.put("batch.size", 16384);
                props.put("linger.ms", 1);
                props.put("buffer.memory", 33554432);

                props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");

                props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

                kafkaProducer = null;

                AdminClient kafkaAdmin = null;

                try {

                    if(isDeleteTopics()) {
                        kafkaAdmin = AdminClient.create(props);
                        KafkaFuture<java.util.Set<java.lang.String>> topicList = kafkaAdmin.listTopics().names();
                        Set<String> topicSet = topicList.get();
                        kafkaAdmin.deleteTopics(topicSet);
                        System.out.println("Topics Deleted");
                        setDeleteTopics(false);
                    }

                    kafkaProducer = new org.apache.kafka.clients.producer.KafkaProducer<>(props);

                    FileInputStream in = new FileInputStream(new File(folderName + "/producer.data-" + type));

                    byte[] buffer = new byte[81920];

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    while (in.read(buffer) != -1) {
                        outputStream.write(buffer);
                    }
                    in.close();
                    kafkaByteArray = outputStream.toByteArray();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public int getCounter() {
        return counter;
    }
}
