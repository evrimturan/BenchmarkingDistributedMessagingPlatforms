import java.io.*;
import java.lang.management.ManagementFactory;
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

        double finalAvgCPU;
        double finalAvgMem;

        /* This is redundant right now, maybe enable in the future ?
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
        */

        if(config.getPubOrSub().equals("producer")){
            Synchronizer synchronizer = new Synchronizer(config.getId(),"producer");
            for(int j = 0;j<3;j++){

                Producer producer;

                String brokerIP = null;
                int queueNumber = 0;

                switch (config.getTest()) {
                    case "pubsub":
                        for (int k = 0; k < pubNum; k++) {
                            if (config.getId().equals("A")) {
                                if (k < pubNum / 2) {
                                    brokerIP = bInfo.get(0).getIp();
                                    queueNumber = 0;
                                } else {
                                    brokerIP = bInfo.get(1).getIp();
                                    queueNumber = 1;
                                }
                            } else if (config.getId().equals("B")) {
                                if (k < pubNum / 2) {
                                    brokerIP = bInfo.get(2).getIp();
                                    queueNumber = 2;
                                } else {
                                    brokerIP = bInfo.get(3).getIp();
                                    queueNumber = 3;
                                }
                            }
                            producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId());
                            pList.add(producer);
                        }

                        break;
                    case "topic1":
                        if (topicNum == 1) {
                            for (int k = 0; k < pubNum; k++) {
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), 0, bInfo.get(0).getIp(), config.getType(), config.getId());
                                pList.add(producer);
                            }
                        } else if (topicNum == 4) {
                            for (int k = 0; k < pubNum; k++) {
                                if (config.getId().equals("A")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber = 0;
                                    } else {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber = 1;
                                    }
                                } else if (config.getId().equals("B")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber = 2;
                                    } else {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber = 3;
                                    }
                                }
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId());
                                pList.add(producer);
                            }
                        } else if (topicNum == 8) {
                            //TODO: bir thread 2 queueya atıcak
                            System.out.println("Now empty, will be resolved.");
                        }
                        break;
                    case "topic2":
                        if (topicNum == 4) {
                            for (int k = 0; k < pubNum; k++) {
                                if (config.getId().equals("A")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber = 0;
                                    } else {
                                        brokerIP = bInfo.get(1).getIp();
                                        queueNumber = 1;
                                    }
                                } else if (config.getId().equals("B")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(2).getIp();
                                        queueNumber = 2;
                                    } else {
                                        brokerIP = bInfo.get(3).getIp();
                                        queueNumber = 3;
                                    }
                                }
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId());
                                pList.add(producer);
                            }
                        } else if (topicNum == 8) {
                            for (int k = 0; k < pubNum; k++) {
                                if (config.getId().equals("A")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber = k;
                                    } else {
                                        brokerIP = bInfo.get(1).getIp();
                                        queueNumber = k;
                                    }
                                } else if (config.getId().equals("B")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(2).getIp();
                                        queueNumber = k + 4;
                                    } else {
                                        brokerIP = bInfo.get(3).getIp();
                                        queueNumber = k + 4;
                                    }
                                }
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId());
                                pList.add(producer);
                            }
                        } else if (topicNum == 16) {
                            System.out.println("Now empty, will be resolved.");
                            //TODO: bir thread 2 queueya atıcak
                        }
                        break;
                    case "broker":
                        if (brokerNum == 1) {
                            for (int k = 0; k < pubNum; k++) {
                                if (config.getId().equals("A")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber = 0;
                                    } else {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber = 1;
                                    }
                                } else if (config.getId().equals("B")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber = 2;
                                    } else {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber = 3;
                                    }
                                }
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId());
                                pList.add(producer);
                            }
                        } else if (brokerNum == 2) {
                            for (int k = 0; k < pubNum; k++) {
                                if (config.getId().equals("A")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber = 0;
                                    } else {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber = 1;
                                    }
                                } else if (config.getId().equals("B")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(1).getIp();
                                        queueNumber = 2;
                                    } else {
                                        brokerIP = bInfo.get(1).getIp();
                                        queueNumber = 3;
                                    }
                                }
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId());
                                pList.add(producer);
                            }
                        } else if (brokerNum == 3) {
                            for (int k = 0; k < pubNum; k++) {
                                if (config.getId().equals("A")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber = 0;
                                    } else {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber = 1;
                                    }
                                } else if (config.getId().equals("B")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(1).getIp();
                                        queueNumber = 2;
                                    } else {
                                        brokerIP = bInfo.get(2).getIp();
                                        queueNumber = 3;
                                    }
                                }
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId());
                                pList.add(producer);
                            }
                        } else if (brokerNum == 4) {
                            for (int k = 0; k < pubNum; k++) {
                                if (config.getId().equals("A")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber = 0;
                                    } else {
                                        brokerIP = bInfo.get(1).getIp();
                                        queueNumber = 1;
                                    }
                                } else if (config.getId().equals("B")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(2).getIp();
                                        queueNumber = 2;
                                    } else {
                                        brokerIP = bInfo.get(3).getIp();
                                        queueNumber = 3;
                                    }
                                }
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId());
                                pList.add(producer);
                            }
                        }
                        break;
                    case "messagesize":
                        for (int k = 0; k < pubNum; k++) {
                            if (config.getId().equals("A")) {
                                if (k < pubNum / 2) {
                                    brokerIP = bInfo.get(0).getIp();
                                    queueNumber = 0;
                                } else {
                                    brokerIP = bInfo.get(1).getIp();
                                    queueNumber = 1;
                                }
                            } else if (config.getId().equals("B")) {
                                if (k < pubNum / 2) {
                                    brokerIP = bInfo.get(2).getIp();
                                    queueNumber = 2;
                                } else {
                                    brokerIP = bInfo.get(3).getIp();
                                    queueNumber = 3;
                                }
                            }
                            producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId());
                            pList.add(producer);
                        }
                        break;
                }
                ScheduledExecutorService ex = Executors.newScheduledThreadPool(pList.size());
                List<Thread> threadList = new ArrayList<>();

                synchronizer.sync();

                for(Producer p : pList) {
                    Callable<Void> call = () -> {
                        System.out.println("Running producer AGAIN");
                        p.run();
                        return null;
                    };
                    Thread temp = new Thread(() -> {
                        try {
                            ex.invokeAll(Collections.singletonList(call),10,TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
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

            synchronizer.closeConnection();

        }else if(config.getPubOrSub().equals("consumer")){
            Synchronizer synchronizer = new Synchronizer(config.getId(),"consumer");
            for(int j = 0;j<3;j++){
                for(int i=0; i<subNum; i++){
                    /*
                    * MKDIR removed from consumer because it is redundant, i will check if it creates problems
                    *
                    */
                    Random r = new Random();
                    int bId = r.nextInt(brokerNum);
                    String bIp = bInfo.get(bId).getIp();

                    Consumer c = new Consumer(topicNum,("ConsumerFolder"+"-"+i),config.getPlatform(),i, bIp);
                    cList.add(c);
                }

                ScheduledExecutorService ex = Executors.newScheduledThreadPool(cList.size());
                List<Thread> threadList = new ArrayList<>();

                synchronizer.sync();

                for(Consumer c : cList) {
                    Callable<Void> call = () -> {
                        System.out.println("Running Consumer AGAIN");
                        c.run();
                        return null;
                    };
                    Thread temp = new Thread(() -> {
                        try {
                            ex.invokeAll(Collections.singletonList(call),10,TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
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
                cList.clear();
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

            synchronizer.closeConnection();
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
