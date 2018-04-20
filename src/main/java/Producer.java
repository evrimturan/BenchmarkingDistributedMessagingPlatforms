import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.jms.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Producer {
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
    private MessageProducer activemqProducer;
    private BytesMessage bMessage;
    private byte[] rabbitByteArray;
    private byte[] kafkaByteArray;
    private org.apache.kafka.clients.producer.Producer<String, byte[]> kafkaProducer;
    private int counter;

    public long getTotalTimeEllapsed() {
        return totalTimeEllapsed;
    }

    public void run(){
        System.out.println(Thread.currentThread().getId()+" says hello Producer :)");
        //long start = System.currentTimeMillis();
        if(platform.equals("activemq")){
            try{
                while(true){
                    activemqProducer.send(bMessage);
                    counter++;
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
                    counter++;
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
                    counter++;
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
        this.setmSize(mSize);
        this.setdSize(dSize);
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
                activemqProducer = activemqSession.createProducer(dest);
                activemqProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

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

                FileInputStream in = new FileInputStream(new File(folderName+"/activemqProducer.data-"+type));

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

                FileInputStream in = new FileInputStream(new File(folderName+"/activemqProducer.data-"+type));

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

    public long getmSize() {
        return mSize;
    }

    public void setmSize(long mSize) {
        this.mSize = mSize;
    }

    public long getdSize() {
        return dSize;
    }

    public void setdSize(long dSize) {
        this.dSize = dSize;
    }
}
