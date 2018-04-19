import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

public class ClusterBenchmarker {

    public static void main(String[] args) {

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
        if(config.getPubOrSub().equals("activemqProducer")){
            for(int i=0; i<pubNum; i++) {
                Random r = new Random();
                int bId = r.nextInt(brokerNum - 0);
                String bIp = bInfo.get(bId).getIp();

                Producer p = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + i), config.getPlatform(), i, bIp, config.getType());

                Path path = Paths.get("ProducerFolder" + "-" + i);

                if (!Files.exists(path)) {
                    File folder = new File("ProducerFolder" + "-" + i);
                    folder.mkdir();
                }

                try {
                    Process process = Runtime.getRuntime().exec("scripts/data-generator.sh " + p.getdSize() / p.getmSize() + " " + p.getmSize() + " " + "ProducerFolder-" + i);
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
                    System.out.println("Running activemqProducer AGAIN");
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

                Consumer c = new Consumer(topicNum,("ConsumerFolder"+"-"+i),config.getPlatform(),i, bIp);
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

    //TODO: topic sayisi ve pubs/subs sayisi degisince hangi queueya message atilip cekilecek,broker connection refuse olunca başka brokera bağlanmayı denesin, kafka consumer global olsun
}
