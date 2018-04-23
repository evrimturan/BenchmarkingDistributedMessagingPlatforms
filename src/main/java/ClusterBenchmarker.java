import java.io.*;
import java.lang.management.ManagementFactory;
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

        double finalCPU = 0;
        double finalMem = 0;

        double finalAvgCPU = 0;
        double finalAvgMem = 0;

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


            try {
                Process process = Runtime.getRuntime().exec("scripts/data-generator.sh " + dataSize / messageSize + " " + messageSize + " " + "ProducerFolder-" + 0);
                //System.out.println("sh -c \"scripts/data-generator.sh " + p.dSize/p.mSize + " " + p.mSize + " " +  System.getProperty("user.dir")+"/scripts/ProducerFolder-"+i+"\"");
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }catch(IOException | InterruptedException ex){
            ex.printStackTrace();
            System.exit(1);
        }

        if(config.getPubOrSub().equals("producer")){
            for(int j = 0;j<3;j++){

                Producer producer = null;

                if(config.getTest().equals("pubsub")) {

                    for(int k=0; k<pubNum; k++) {
                        if(config.getId().equals("A")) {
                            if(k < pubNum/2) {
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 0, bInfo.get(0).getIp(), config.getType(),config.getId());
                            }
                            else {
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 1, bInfo.get(1).getIp(), config.getType(),config.getId());
                            }
                        }
                        else if(config.getId().equals("B")) {
                            if(k < pubNum/2) {
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 2, bInfo.get(2).getIp(), config.getType(),config.getId());
                            }
                            else {
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 3, bInfo.get(3).getIp(), config.getType(),config.getId());
                            }
                        }
                        pList.add(producer);
                    }

                }

                else if(config.getTest().equals("topic1")) {
                   if(topicNum == 1) {
                        for(int k=0; k<pubNum; k++) {
                            producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 0, bInfo.get(0).getIp(), config.getType(),config.getId());
                            pList.add(producer);
                        }
                   }
                   else if(topicNum == 4) {
                       for(int k=0; k<pubNum; k++) {
                           if(config.getId().equals("A")) {
                               if(k < pubNum/2) {
                                   producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 0, bInfo.get(0).getIp(), config.getType(),config.getId());
                               }
                               else {
                                   producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 1, bInfo.get(0).getIp(), config.getType(),config.getId());
                               }
                           }
                           else if(config.getId().equals("B")) {
                               if(k < pubNum/2) {
                                   producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 2, bInfo.get(0).getIp(), config.getType(),config.getId());
                               }
                               else {
                                   producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 3, bInfo.get(0).getIp(), config.getType(),config.getId());
                               }
                           }
                           pList.add(producer);
                       }
                   }

                   else if(topicNum == 8) {
                       //TODO: bir thread 2 queueya atıcak
                   }
                }

                else if(config.getTest().equals("topic2")) {
                    if(topicNum == 4) {
                        for(int k=0; k<pubNum; k++) {
                            if(config.getId().equals("A")) {
                                if(k < pubNum/2) {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 0, bInfo.get(0).getIp(), config.getType(),config.getId());
                                }
                                else {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 1, bInfo.get(1).getIp(), config.getType(),config.getId());
                                }
                            }
                            else if(config.getId().equals("B")) {
                                if(k < pubNum/2) {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 2, bInfo.get(2).getIp(), config.getType(),config.getId());
                                }
                                else {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 3, bInfo.get(3).getIp(), config.getType(),config.getId());
                                }
                            }
                            pList.add(producer);
                        }
                    }

                    else if(topicNum == 8) {
                        for(int k=0; k<pubNum; k++) {
                            if(config.getId().equals("A")) {
                                if(k < pubNum/2) {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), k, bInfo.get(0).getIp(), config.getType(),config.getId());
                                }
                                else {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), k, bInfo.get(1).getIp(), config.getType(),config.getId());
                                }
                            }
                            else if(config.getId().equals("B")) {
                                if(k < pubNum/2) {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), k + 4, bInfo.get(2).getIp(), config.getType(),config.getId());
                                }
                                else {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), k + 4, bInfo.get(3).getIp(), config.getType(),config.getId());
                                }
                            }
                            pList.add(producer);
                        }
                    }

                    else if(topicNum == 16) {
                        //TODO: bir thread 2 queueya atıcak
                    }
                }

                else if(config.getTest().equals("broker")) {
                    if(brokerNum == 1) {
                        for(int k=0; k<pubNum; k++) {
                            if(config.getId().equals("A")) {
                                if(k < pubNum/2) {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 0, bInfo.get(0).getIp(), config.getType(),config.getId());
                                }
                                else {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 1, bInfo.get(0).getIp(), config.getType(),config.getId());
                                }
                            }
                            else if(config.getId().equals("B")) {
                                if(k < pubNum/2) {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 2, bInfo.get(0).getIp(), config.getType(),config.getId());
                                }
                                else {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 3, bInfo.get(0).getIp(), config.getType(),config.getId());
                                }
                            }
                            pList.add(producer);
                        }
                    }

                    else if(brokerNum == 2) {
                        for(int k=0; k<pubNum; k++) {
                            if(config.getId().equals("A")) {
                                if(k < pubNum/2) {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 0, bInfo.get(0).getIp(), config.getType(),config.getId());
                                }
                                else {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 1, bInfo.get(0).getIp(), config.getType(),config.getId());
                                }
                            }
                            else if(config.getId().equals("B")) {
                                if(k < pubNum/2) {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 2, bInfo.get(1).getIp(), config.getType(),config.getId());
                                }
                                else {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 3, bInfo.get(1).getIp(), config.getType(),config.getId());
                                }
                            }
                            pList.add(producer);
                        }
                    }

                    else if(brokerNum == 3) {
                        for(int k=0; k<pubNum; k++) {
                            if(config.getId().equals("A")) {
                                if(k < pubNum/2) {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 0, bInfo.get(0).getIp(), config.getType(),config.getId());
                                }
                                else {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 1, bInfo.get(0).getIp(), config.getType(),config.getId());
                                }
                            }
                            else if(config.getId().equals("B")) {
                                if(k < pubNum/2) {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 2, bInfo.get(1).getIp(), config.getType(),config.getId());
                                }
                                else {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 3, bInfo.get(2).getIp(), config.getType(),config.getId());
                                }
                            }
                            pList.add(producer);
                        }
                    }

                    else if(brokerNum == 4) {
                        for(int k=0; k<pubNum; k++) {
                            if(config.getId().equals("A")) {
                                if(k < pubNum/2) {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 0, bInfo.get(0).getIp(), config.getType(),config.getId());
                                }
                                else {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 1, bInfo.get(1).getIp(), config.getType(),config.getId());
                                }
                            }
                            else if(config.getId().equals("B")) {
                                if(k < pubNum/2) {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 2, bInfo.get(2).getIp(), config.getType(),config.getId());
                                }
                                else {
                                    producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 3, bInfo.get(3).getIp(), config.getType(),config.getId());
                                }
                            }
                            pList.add(producer);
                        }
                    }
                }

                else if(config.getTest().equals("messagesize")) {
                    for(int k=0; k<pubNum; k++) {
                        if(config.getId().equals("A")) {
                            if(k < pubNum/2) {
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 0, bInfo.get(0).getIp(), config.getType(),config.getId());
                            }
                            else {
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 1, bInfo.get(1).getIp(), config.getType(),config.getId());
                            }
                        }
                        else if(config.getId().equals("B")) {
                            if(k < pubNum/2) {
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 2, bInfo.get(2).getIp(), config.getType(),config.getId());
                            }
                            else {
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 3, bInfo.get(3).getIp(), config.getType(),config.getId());
                            }
                        }
                        pList.add(producer);
                    }
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
                Long pId = Long.parseLong(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
                Utilizer u = new Utilizer(pId);
                Thread uThread = new Thread(u);
                uThread.start();

                for(Thread e : threadList){
                    try {
                        e.join();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                ex.shutdown();
                u.setStop(true);
                try{
                    uThread.join();
                }catch (InterruptedException ex1){
                    ex1.printStackTrace();
                }
                double avgCPU = u.getAvgCPU(),avgMem = u.getAvgMem();

                System.out.println("Average TEST: " + avgCPU + " " + avgMem);

                finalCPU += avgCPU;
                finalMem += avgMem;

                pList.clear();
                cList.clear();
            }

            finalAvgCPU = finalCPU / 3;
            finalAvgMem = finalMem / 3;

            String testResult = "PRODUCER --> Average CPU Utilization: " + finalAvgCPU + " Average Memory Utilization: " + finalAvgMem;

            System.out.println(testResult);

            try {
                FileOutputStream fileOutputStream = new FileOutputStream("util.log", false);
                fileOutputStream.write(testResult.getBytes());
                fileOutputStream.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }else if(config.getPubOrSub().equals("consumer")){
            for(int j = 0;j<3;j++){
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
                ScheduledExecutorService ex = Executors.newScheduledThreadPool(cList.size());
                List<Thread> threadList = new ArrayList<>();

                for(Consumer c : cList) {
                    Callable<Void> call = () -> {
                        System.out.println("Running Consumer AGAIN");
                        c.run();
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

                Long pId = Long.parseLong(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
                Utilizer u = new Utilizer(pId);
                Thread uThread = new Thread(u);
                uThread.start();

                for(Thread e : threadList){
                    try {
                        e.join();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                ex.shutdown();
                u.setStop(true);
                try{
                    uThread.join();
                }catch (InterruptedException ex1){
                    ex1.printStackTrace();
                }
                double avgCPU = u.getAvgCPU(),avgMem = u.getAvgMem();

                System.out.println("Average TEST: " + avgCPU + " " + avgMem);

                finalCPU += avgCPU;
                finalMem += avgMem;
            }

            finalAvgCPU = finalCPU / 3;
            finalAvgMem = finalMem / 3;

            String testResult = "CONSUMER --> Average CPU Utilization: " + finalAvgCPU + " Average Memory Utilization: " + finalAvgMem;

            System.out.println(testResult);

            try {
                FileOutputStream fileOutputStream = new FileOutputStream("util.log", false);
                fileOutputStream.write(testResult.getBytes());
                fileOutputStream.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            //HTTP request final
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
