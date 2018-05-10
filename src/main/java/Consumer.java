import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import javax.jms.*;
import javax.jms.Queue;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("InfiniteLoopStatement")
public class Consumer {
    private int tNum;
    private String folderName;
    private String platform;
    private long totalTimeElapsed;
    private List<Integer> queueNum;
    private String brokerIp;
    private javax.jms.Connection activemqConnection;
    private ActiveMQSession activemqSession;
    private com.rabbitmq.client.Connection rabbitmqConnection;
    private Channel rabbitmqChannel;
    private int fileNumber = 0;
    private MessageConsumer activemqConsumer;
    private com.rabbitmq.client.Consumer rabbitmqConsumer;
    private org.apache.kafka.clients.consumer.KafkaConsumer<String, byte[]> kafkaConsumer;
    private HashMap<String,Queue> consumers;
    private static int counter = 0;

    public static int getCounter() {
        return counter;
    }

    public static void setCounter(int counter) { Consumer.counter = counter; }

    public long getTotalTimeElapsed() {
        return totalTimeElapsed;
    }

    public void run() {
        System.out.println(Thread.currentThread().getId() + " says hello consumer :)");
        int count = 0;
        if (platform.equals("activemq")) {
            try {

                MessageListener listener = message -> {
                    try{
                        if(message instanceof BytesMessage){
                            counter = getCounter() + 1;
                            //FileOutputStream fos = new FileOutputStream(folderName + "/consumer.data-" + queueNum);
                            //System.out.println("ACTIVEMQ CONSUMING FROM " + brokerIp + " queue is : "+queueNum.get(0)); removed for now
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
                activemqConsumer.setMessageListener(listener);
                while(count < 120){
                    count++;
                    //System.out.println("Waiting consumer...");
                    Thread.sleep(1000);
                }
                activemqConsumer.close();
                activemqSession.close();
                activemqConnection.close();

            } catch (Exception e) {
                try{
                    activemqConsumer.close();
                    activemqSession.close();
                    activemqConnection.close();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                //e.printStackTrace();
            }
        } else if (platform.equals("rabbitmq")) {
            try {

                System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

                for(Integer a : queueNum){
                    rabbitmqChannel.basicConsume("queue-" + a, true, rabbitmqConsumer);
                }

                while(count < 120){
                    count++;
                    System.out.println("Waiting consumer...");
                    Thread.sleep(1000);
                }

            } catch (Exception e) {
                try{
                    rabbitmqChannel.close();
                    rabbitmqConnection.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                //e.printStackTrace();
            }
        } else if (platform.equals("kafka")) {
            try {
                while(true){
                    kafkaConsumer.subscription().forEach(System.out::println);
                    ConsumerRecords<String, byte[]> records = kafkaConsumer.poll(100);
                    System.out.println("Record Size: " + records.count());
                    for (ConsumerRecord<String, byte[]> ignored : records) {
                        System.out.println("KAFKA CONSUMING FROM " + brokerIp + " TOPIC : "+ignored.topic());
                        counter = getCounter() + 1;

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
                    }
                    kafkaConsumer.commitAsync();
                }
                //kafkaConsumer.unsubscribe();
            } catch (Exception e) {
                //e.printStackTrace();
                kafkaConsumer.commitSync();
                kafkaConsumer.close();
            }
        }
        //long finish = System.currentTimeMillis();
    }

    public void shutdown(){
        switch (platform) {
            case "activemq":
                try {
                    activemqConsumer.close();
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
                kafkaConsumer.commitSync();
                kafkaConsumer.close();
                break;
        }
    }

    Consumer(int tNum, String folderName, String platform, List<Integer> queueNum, String brokerIp) {
        this.tNum = tNum;
        this.folderName = folderName;
        this.platform = platform;
        this.queueNum = new ArrayList<>();
        this.brokerIp = brokerIp;
        this.queueNum.addAll(queueNum);

        switch (platform) {
            case "activemq":
                try {
                    consumers = new HashMap<>();
                    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://" + brokerIp + ":61616");
                    this.activemqConnection = connectionFactory.createConnection("admin", "admin");
                    activemqConnection.start();
                    this.activemqSession = (ActiveMQSession) activemqConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                    //Queue dest = activemqSession.createQueue("queue-" + queueNum);
                    String dest = "";
                    for(Integer a : this.queueNum){
                        dest+=("queue-"+a);
                        dest+=",";
                    }
                    dest = dest.substring(0,dest.length()-1);
                    System.out.println("queue is : "+dest);
                    activemqConsumer = activemqSession.createConsumer(activemqSession.createQueue(dest));

                    for (Integer aQueueNum : this.queueNum) {
                        consumers.put("queue-" + aQueueNum, activemqSession.createQueue("queue-" + aQueueNum));
                    }
                    System.out.println("ActiveMQ connection established.");

                } catch (Exception e) {
                    e.printStackTrace();
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

                    rabbitmqConsumer = new DefaultConsumer(rabbitmqChannel) {
                        @Override
                        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {

                            //System.out.println("RABBITMQ CONSUMING FROM " + brokerIp);
                            counter = getCounter() + 1;
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

                    System.out.println("RabbitMQ connection established.");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "kafka":
                Properties props = new Properties();
                props.put("bootstrap.servers", brokerIp + ":9092");
                props.put("group.id", "group-1");
                props.put("enable.auto.commit", "true");
                props.put("auto.commit.interval.ms", "1000");
                props.put("auto.offset.reset", "latest");
                props.put("session.timeout.ms", "30000");
                props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
                props.put("max.poll.records",10000);
                kafkaConsumer = null;

                try {
                    kafkaConsumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(props);

                    String[] topics = new String[queueNum.size()];
                    for(int i = 0; i<queueNum.size(); i++) {
                        topics[i] = "queue-" + queueNum.get(i);
                    }
                    kafkaConsumer.subscribe(Arrays.asList(topics));

                    System.out.println("Kafka consumer has subscriptions of these : ");
                    kafkaConsumer.subscription().forEach(System.out::println);

                    System.out.println("Kafka connection established.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
