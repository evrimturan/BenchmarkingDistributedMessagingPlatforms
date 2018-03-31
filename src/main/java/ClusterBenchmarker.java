import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.io.InputStream;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.BlobMessage;
import org.apache.activemq.command.ActiveMQBytesMessage;

import javax.jms.*;

public class ClusterBenchmarker {

    private class Consumer extends Thread{
        private int tNum;
        private String folderName;
        private String platform;
        private long totalTimeEllapsed;
        private int queueNum;

        public long getTotalTimeEllapsed() {
            return totalTimeEllapsed;
        }

        @Override
        public void run(){
            System.out.println(Thread.currentThread().getId()+" says hello consumer :)");
            //long start = System.currentTimeMillis();
            if(platform.equals("activemq")){
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://ubuntu-s-1vcpu-1gb-fra1-01:61616");
                try{
                    FileOutputStream fos = new FileOutputStream(folderName+"/consumer.data-"+queueNum);
                    Connection connection = connectionFactory.createConnection("admin","admin");
                    connection.start();
        
                    // Create a Session
                    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    
                    Queue dest = session.createQueue("queue-"+queueNum);
    
                    MessageConsumer consume = session.createConsumer(dest);
                    ActiveMQBytesMessage rc =  (ActiveMQBytesMessage)consume.receive(1000);

                    byte[] buffer = new byte[1024];
                    
                    while((rc.readBytes(buffer)) != -1){
                        fos.write(buffer);
                    }
                    fos.close();

                }catch(Exception ex){
                    ex.printStackTrace();
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
                    Connection connection = connectionFactory.createConnection("admin","admin");
                    connection.start();

                    // Create a Session
                    ActiveMQSession session = (ActiveMQSession)connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                    Queue dest = session.createQueue("queue-"+queueNum);
                    MessageProducer producer = session.createProducer(dest);

                    byte[] buffer = new byte[1024];
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
        for(Consumer c : cList){
            c.run();
        }
        /* TODO: subNum kadar thread daha oluşturulacak
        *  TODO: tüm thread ler run edilecek
        *  TODO: producer thread'inde klasör sonuna geldiği anlaşılacak ve ondan sonra data memory'e alınacak
        *  TODO: System.getmillis ile zaman ölçülecek ve consumer thread ları bittikten sonra zaman alınıp farkı alınacak
        *
        */
        for(Producer p : pList){
            try {
                p.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
