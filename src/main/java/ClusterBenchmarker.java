import org.apache.kafka.common.Cluster;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.xml.crypto.Data;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ClusterBenchmarker {

    private class Consumer extends Thread{

        @Override
        public void run(){
            System.out.println(Thread.currentThread().getId()+" says hello consumer:)");
        }

        public Consumer(){

        }
    }

    private class Producer extends Thread{

        @Override
        public void run(){
            System.out.println(Thread.currentThread().getId()+" says hello producer:)");
            DataGenerator dgenerator = new DataGenerator();
            dgenerator.start();
            

        }

        public Producer(){

        }
    }
    private class DataGenerator extends Thread{

        public void run(){

        }

        public DataGenerator(){

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
        DataGenerator dGenerator2 = init.createDataGenerator();
        dGenerator2.start();

        try {
            dGenerator2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i=0; i<pubNum; i++){
            Producer p = init.createProducer();
            pList.add(p);
            Path path = Paths.get("ProducerFolder"+"-"+i);

            if (!Files.exists(path)) {
                File folder = new File("ProducerFolder"+"-"+i);
                folder.mkdir();
            }
        }
        for(Producer p : pList){
            p.run();
        }

        for(int i=0; i<subNum; i++){
            Consumer c = init.createConsumer();
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

    private Producer createProducer(){
        return new Producer();
    }
    private Consumer createConsumer() { return new Consumer(); }
    private DataGenerator createDataGenerator(){ return new DataGenerator(); }
}
