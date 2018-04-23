import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import javax.jms.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Properties;

public class Consumer {
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
    private MessageConsumer activemqConsumer;
    private com.rabbitmq.client.Consumer rabbitmqConsumer;
    private org.apache.kafka.clients.consumer.KafkaConsumer<String, byte[]> kafkaConsumer;

    public long getTotalTimeElapsed() {
        return totalTimeElapsed;
    }

    public void run() {
        System.out.println(Thread.currentThread().getId() + " says hello consumer :)");
        int count = 0;
        if (platform.equals("activemq")) {
            try {
                System.out.println("queue-"+queueNum);
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
                while(count < 20){
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

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (platform.equals("kafka")) {
            try {
                while(true){
                    ConsumerRecords<String, byte[]> records = kafkaConsumer.poll(100);
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
                    }
                }
                //kafkaConsumer.unsubscribe();
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


        ServerSocket ss = null;
        ServerSocket ss2 = null;
        Socket clientSocket = null;
        Socket clientSocket2 = null;
        BufferedReader rd1 = null;
        BufferedReader rd2 = null;
        String msg = "Oldu";
        Boolean run = false;
        Boolean run2 = false;

        try{
            ss = new ServerSocket(10001);
            ss2 = new ServerSocket(10002);
            clientSocket = ss.accept();
            clientSocket2 = ss2.accept();
            rd1 = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            rd2 = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));

            System.out.println("Connection established with both producers");

        }catch (Exception e){
            System.out.println(e);
            System.exit(1);
        }


        if (platform.equals("activemq")) {
            try {
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://" + brokerIp + ":61616");
                this.activemqConnection = connectionFactory.createConnection("admin", "admin");
                activemqConnection.start();
                this.activemqSession = (ActiveMQSession) activemqConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                Queue dest = activemqSession.createQueue("queue-" + queueNum);
                activemqConsumer = activemqSession.createConsumer(dest);

                if(rd1.readLine().equals(msg)){
                    run = true;
                }
                if (rd2.readLine().equals(msg)){
                    run2 = true;
                }
                if (!run && !run2){
                    System.exit(1);
                }
                System.out.println("ActiveMQ connection established.");

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
                if(rd1.readLine().equals(msg)){
                    run = true;
                }
                if (rd2.readLine().equals(msg)){
                    run2 = true;
                }
                if (!run && !run2){
                    System.exit(1);
                }

                System.out.println("RabbitMQ connection established.");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else if (platform.equals("kafka")) {
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
                kafkaConsumer.subscribe(Arrays.asList("queue-" + queueNum));

                if(rd1.readLine().equals(msg)){
                    run = true;
                }
                if (rd2.readLine().equals(msg)){
                    run2 = true;
                }
                if (!run && !run2){
                    System.exit(1);
                }

                System.out.println("Kafka connection established.");
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
