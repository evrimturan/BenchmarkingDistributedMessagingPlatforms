import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ClusterBenchmarker {

    private class Consumer extends Thread{
        private int tNum;
        private String folderName;
        private long totalTimeEllapsed;

        public long getTotalTimeEllapsed() {
            return totalTimeEllapsed;
        }

        @Override
        public void run(){
            System.out.println(Thread.currentThread().getId()+" says hello consumer :)");
            long start = System.currentTimeMillis();


            long finish = System.currentTimeMillis();
        }

        public Consumer(int tNum,String folderName){
            this.tNum = tNum;
            this.folderName = folderName;
        }
    }

    private class Producer extends Thread{
        private double mSize;
        private double dSize;
        private int tNum;
        private long totalTimeEllapsed;

        public long getTotalTimeEllapsed() {
            return totalTimeEllapsed;
        }

        @Override
        public void run(){
            System.out.println(Thread.currentThread().getId()+" says hello producer :)");
            long start = System.currentTimeMillis();


            long finish = System.currentTimeMillis();
        }

        public Producer(double mSize, double dSize, int tNum){
            this.mSize = mSize;
            this.dSize = dSize;
            this.tNum = tNum;
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

        }catch(IOException | InterruptedException ex){
            ex.printStackTrace();
            System.exit(1);
        }
        
        for(int i=0; i<pubNum; i++){
            Producer p = init.createProducer(messageSize,dataSize/pubNum,topicNum);
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

            Consumer c = init.createConsumer(topicNum,("ConsumerFolder"+"-"+i));
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

    private Producer createProducer(double mSize,double dSize,int tNum) {  return new Producer(mSize,dSize,tNum); }
    private Consumer createConsumer(int tNum,String folderName) {  return new Consumer(tNum,folderName); }
}
