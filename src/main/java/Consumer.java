import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import javax.jms.*;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

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
                activemqConsumer.setMessageListener(listener);
                while(count < 120){
                    count++;
                    System.out.println("Waiting consumer...");
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

                rabbitmqChannel.basicConsume("queue-" + queueNum, true, rabbitmqConsumer);

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
                    ConsumerRecords<String, byte[]> records = kafkaConsumer.poll(100);
                    for (ConsumerRecord<String, byte[]> ignored : records) {
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
                    }
                }
                //kafkaConsumer.unsubscribe();
            } catch (Exception e) {
                //e.printStackTrace();
                kafkaConsumer.unsubscribe();
            }
        }
        //long finish = System.currentTimeMillis();
    }

    Consumer(int tNum, String folderName, String platform, List<Integer> queueNum, String brokerIp) {
        this.tNum = tNum;
        this.folderName = folderName;
        this.platform = platform;
        this.queueNum = queueNum;
        this.brokerIp = brokerIp;

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
                    for(Integer a : queueNum){
                        dest+=("queue://queue-"+a);
                        dest+=",";
                    }
                    dest = dest.substring(0,dest.length()-1);
                    System.out.println("queue is : "+dest);
                    activemqConsumer = activemqSession.createConsumer(activemqSession.createQueue(dest));

                    for (Integer aQueueNum : queueNum) {
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

                    rabbitmqChannel.queueDeclare("queue-" + queueNum, true, false, false, null);

                    rabbitmqConsumer = new DefaultConsumer(rabbitmqChannel) {
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
                props.put("auto.offset.reset", "earliest");
                props.put("session.timeout.ms", "30000");
                props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
                kafkaConsumer = null;

                try {
                    kafkaConsumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(props);
                    kafkaConsumer.subscribe(Collections.singletonList("queue-" + queueNum));

                    System.out.println("Kafka connection established.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
