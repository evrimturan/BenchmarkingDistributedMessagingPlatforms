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
        double finalThroughput = 0;

        double finalAvgCPU;
        double finalAvgMem;
        double finalAvgThroughput;


        /* This is redundant right now, maybe enable in the future ?
        better fix required
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

        if(config.getTest().equals("zigzag1")) {
            Producer producer;
            Consumer consumer;

            String brokerIP = null;
            ArrayList<Integer> queueNumber = new ArrayList<>();
            queueNumber.add(0);

            if(config.getServerNum() == 0) {
                brokerIP = bInfo.get(0).getIp();
                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), true);
                pList.add(producer);
            }

            else if(config.getServerNum() == 1) {
                brokerIP = bInfo.get(0).getIp();
                consumer = new Consumer(topicNum,("ConsumerFolder"+"-"+ 0),config.getPlatform(),queueNumber, brokerIP);
                cList.add(consumer);
            }
        }

        else if(config.getTest().equals("zigzag2")) {
            Producer producer;
            Consumer consumer;

            String brokerIP = null;
            ArrayList<Integer> queueNumber = new ArrayList<>();
            queueNumber.add(0);

            if(config.getServerNum() == 0) {
                brokerIP = bInfo.get(0).getIp();
                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), true);
                pList.add(producer);
            }

            else if(config.getServerNum() == 1) {
                brokerIP = bInfo.get(0).getIp();
                consumer = new Consumer(topicNum,("ConsumerFolder"+"-"+ 0),config.getPlatform(),queueNumber, brokerIP);
                cList.add(consumer);

                brokerIP = bInfo.get(1).getIp();
                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), true);
                pList.add(producer);
            }

            else if(config.getServerNum() == 2) {
                brokerIP = bInfo.get(1).getIp();
                consumer = new Consumer(topicNum,("ConsumerFolder"+"-"+ 0),config.getPlatform(),queueNumber, brokerIP);
                cList.add(consumer);
            }
        }

        else if(config.getTest().equals("zigzag3")) {
            Producer producer;
            Consumer consumer;

            String brokerIP = null;
            ArrayList<Integer> queueNumber = new ArrayList<>();
            queueNumber.add(0);

            if(config.getServerNum() == 0) {
                brokerIP = bInfo.get(0).getIp();
                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), true);
                pList.add(producer);
            }

            else if(config.getServerNum() == 1) {
                brokerIP = bInfo.get(0).getIp();
                consumer = new Consumer(topicNum,("ConsumerFolder"+"-"+ 0),config.getPlatform(),queueNumber, brokerIP);
                cList.add(consumer);

                brokerIP = bInfo.get(1).getIp();
                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), true);
                pList.add(producer);
            }

            else if(config.getServerNum() == 2) {
                brokerIP = bInfo.get(1).getIp();
                consumer = new Consumer(topicNum,("ConsumerFolder"+"-"+ 0),config.getPlatform(),queueNumber, brokerIP);
                cList.add(consumer);

                brokerIP = bInfo.get(2).getIp();
                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), true);
                pList.add(producer);
            }

            else if(config.getServerNum() == 3) {
                brokerIP = bInfo.get(2).getIp();
                consumer = new Consumer(topicNum,("ConsumerFolder"+"-"+ 0),config.getPlatform(),queueNumber, brokerIP);
                cList.add(consumer);

                brokerIP = bInfo.get(3).getIp();
                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), true);
                pList.add(producer);
            }

            else if(config.getServerNum() == 4) {
                brokerIP = bInfo.get(3).getIp();
                consumer = new Consumer(topicNum,("ConsumerFolder"+"-"+ 0),config.getPlatform(),queueNumber, brokerIP);
                cList.add(consumer);
            }
        }

        else if(config.getPubOrSub().equals("producer")){
            Synchronizer synchronizer = new Synchronizer(config.getId(),"producer");
            for(int j = 0;j<3;j++){

                Producer producer;

                String brokerIP = null;
                ArrayList<Integer> queueNumber = new ArrayList<>();

                switch (config.getTest()) {

                    case "pubsub":
                        for (int k = 0; k < pubNum; k++) {
                            if (config.getId().equals("A")) {
                                if (k < pubNum / 2) {
                                    brokerIP = bInfo.get(0).getIp();
                                    queueNumber.add(0);
                                } else {
                                    brokerIP = bInfo.get(1).getIp();
                                    queueNumber.add(1);
                                }
                            } else if (config.getId().equals("B")) {
                                if (k < pubNum / 2) {
                                    brokerIP = bInfo.get(2).getIp();
                                    queueNumber.add(2);
                                } else {
                                    brokerIP = bInfo.get(3).getIp();
                                    queueNumber.add(3);
                                }
                            }
                            producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), false);
                            pList.add(producer);
                            queueNumber.clear();
                        }

                        break;
                    case "topic1":
                        if (topicNum == 1) {
                            for (int k = 0; k < pubNum; k++) {
                                queueNumber.add(0);
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, bInfo.get(0).getIp(), config.getType(), config.getId(), config.getServerNum(), false);
                                pList.add(producer);
                                //queueNumber.clear();
                            }
                        } else if (topicNum == 4) {
                            for (int k = 0; k < pubNum; k++) {
                                if (config.getId().equals("A")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber.add(0);
                                    } else {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber.add(1);
                                    }
                                } else if (config.getId().equals("B")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber.add(2);
                                    } else {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber.add(3);
                                    }
                                }
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), false);
                                pList.add(producer);
                                queueNumber.clear();
                            }
                        } else if (topicNum == 8) {
                            //TODO:2 queue 1 producer
                            for (int k = 0; k < pubNum; k++) {
                                if (config.getId().equals("A")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber.add(0);
                                        queueNumber.add(4);
                                    } else {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber.add(1);
                                        queueNumber.add(5);
                                    }
                                } else if (config.getId().equals("B")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber.add(2);
                                        queueNumber.add(6);
                                    } else {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber.add(3);
                                        queueNumber.add(7);
                                    }
                                }
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), false);
                                pList.add(producer);
                                queueNumber.clear();
                            }
                        }
                        break;
                    case "topic2":
                        if (topicNum == 4) {
                            for (int k = 0; k < pubNum; k++) {
                                if (config.getId().equals("A")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber.add(0);
                                    } else {
                                        brokerIP = bInfo.get(1).getIp();
                                        queueNumber.add(1);
                                    }
                                } else if (config.getId().equals("B")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(2).getIp();
                                        queueNumber.add(2);
                                    } else {
                                        brokerIP = bInfo.get(3).getIp();
                                        queueNumber.add(3);
                                    }
                                }
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), false);
                                pList.add(producer);
                                queueNumber.clear();
                            }
                        } else if (topicNum == 8) {
                            for (int k = 0; k < pubNum; k++) {
                                if (config.getId().equals("A")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber.add(k);
                                    } else {
                                        brokerIP = bInfo.get(1).getIp();
                                        queueNumber.add(k);
                                    }
                                } else if (config.getId().equals("B")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(2).getIp();
                                        queueNumber.add(k + 4);
                                    } else {
                                        brokerIP = bInfo.get(3).getIp();
                                        queueNumber.add(k + 4);
                                    }
                                }
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), false);
                                pList.add(producer);
                                queueNumber.clear();
                            }
                        } else if (topicNum == 16) {
                            //TODO:2 queue 1 producer
                            for (int k = 0; k < pubNum; k++) {
                                if (config.getId().equals("A")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        if(k % 2 == 0) {
                                            queueNumber.add(0);
                                            queueNumber.add(8);
                                        }
                                        else {
                                            queueNumber.add(1);
                                            queueNumber.add(9);
                                        }

                                    } else {
                                        brokerIP = bInfo.get(1).getIp();
                                        if(k % 2 == 0) {
                                            queueNumber.add(2);
                                            queueNumber.add(10);
                                        }
                                        else {
                                            queueNumber.add(3);
                                            queueNumber.add(11);
                                        }
                                    }
                                } else if (config.getId().equals("B")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(2).getIp();
                                        if(k % 2 == 0) {
                                            queueNumber.add(4);
                                            queueNumber.add(12);
                                        }
                                        else {
                                            queueNumber.add(5);
                                            queueNumber.add(13);
                                        }

                                    } else {
                                        brokerIP = bInfo.get(3).getIp();
                                        if(k % 2 == 0) {
                                            queueNumber.add(6);
                                            queueNumber.add(14);
                                        }
                                        else {
                                            queueNumber.add(7);
                                            queueNumber.add(15);
                                        }
                                    }
                                }
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), false);
                                pList.add(producer);
                                queueNumber.clear();
                            }
                        }
                        break;
                    case "broker":
                        if (brokerNum == 1) {
                            for (int k = 0; k < pubNum; k++) {
                                if (config.getId().equals("A")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber.add(0);
                                    } else {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber.add(1);
                                    }
                                } else if (config.getId().equals("B")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber.add(2);
                                    } else {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber.add(3);
                                    }
                                }
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), false);
                                pList.add(producer);
                                queueNumber.clear();
                            }
                        } else if (brokerNum == 2) {
                            for (int k = 0; k < pubNum; k++) {
                                if (config.getId().equals("A")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber.add(0);
                                    } else {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber.add(1);
                                    }
                                } else if (config.getId().equals("B")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(1).getIp();
                                        queueNumber.add(2);
                                    } else {
                                        brokerIP = bInfo.get(1).getIp();
                                        queueNumber.add(3);
                                    }
                                }
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), false);
                                pList.add(producer);
                                queueNumber.clear();
                            }
                        } else if (brokerNum == 3) {
                            for (int k = 0; k < pubNum; k++) {
                                if (config.getId().equals("A")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber.add(0);
                                    } else {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber.add(1);
                                    }
                                } else if (config.getId().equals("B")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(1).getIp();
                                        queueNumber.add(2);
                                    } else {
                                        brokerIP = bInfo.get(2).getIp();
                                        queueNumber.add(3);
                                    }
                                }
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), false);
                                pList.add(producer);
                                queueNumber.clear();
                            }
                        } else if (brokerNum == 4) {
                            for (int k = 0; k < pubNum; k++) {
                                if (config.getId().equals("A")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(0).getIp();
                                        queueNumber.add(0);
                                    } else {
                                        brokerIP = bInfo.get(1).getIp();
                                        queueNumber.add(1);
                                    }
                                } else if (config.getId().equals("B")) {
                                    if (k < pubNum / 2) {
                                        brokerIP = bInfo.get(2).getIp();
                                        queueNumber.add(2);
                                    } else {
                                        brokerIP = bInfo.get(3).getIp();
                                        queueNumber.add(3);
                                    }
                                }
                                producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), false);
                                pList.add(producer);
                                queueNumber.clear();
                            }
                        }
                        break;
                    case "messagesize":
                        for (int k = 0; k < pubNum; k++) {
                            if (config.getId().equals("A")) {
                                if (k < pubNum / 2) {
                                    brokerIP = bInfo.get(0).getIp();
                                    queueNumber.add(0);
                                } else {
                                    brokerIP = bInfo.get(1).getIp();
                                    queueNumber.add(1);
                                }
                            } else if (config.getId().equals("B")) {
                                if (k < pubNum / 2) {
                                    brokerIP = bInfo.get(2).getIp();
                                    queueNumber.add(2);
                                } else {
                                    brokerIP = bInfo.get(3).getIp();
                                    queueNumber.add(3);
                                }
                            }
                            producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), false);
                            pList.add(producer);
                            queueNumber.clear();
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
                            ex.invokeAll(Collections.singletonList(call),2,TimeUnit.MINUTES);
                        } catch (InterruptedException e) {
                            //e.printStackTrace(); This is redundant
                        }finally{
                            p.shutdown();
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
                String type = config.getPubOrSub();
                String id = config.getId();
                String platform = config.getPlatform();
                Utilizer u = new Utilizer(pId, type, id, platform, j);
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
                double avgCPU = u.getAvgCPU();
                double avgMem = u.getAvgMem();
                double throughput = u.getProducerThroughput();

                System.out.println("Average TEST: " + avgCPU + " " + avgMem);

                finalCPU += avgCPU;
                finalMem += avgMem;
                finalThroughput += throughput;

                pList.clear();
                Producer.setDeleteTopics(true);
                if(Producer.isDeleteTopics()) {
                    System.out.println("Delete Topics True");
                }
            }

            finalAvgCPU = finalCPU / 3;
            finalAvgMem = finalMem / 3;
            finalAvgThroughput = finalThroughput / 3;

            String testResult = "PRODUCER --> Average CPU Utilization: " + finalAvgCPU + " Average Memory Utilization: " + finalAvgMem  + "Average Throughput: " + finalAvgThroughput;

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
                //TODO: consumer'lar nasil handle edilecek onemli onu konusmamiz lazim

                ArrayList<Integer> queueList= new ArrayList<>();
                ArrayList<Integer> brokerList = new ArrayList<>();

                if(config.getId().equals("A")) {
                    for(int q = 0; q<topicNum/2; q++) {
                        queueList.add(q);
                    }
                    for(int b = brokerNum/2; b<brokerNum; b++) {
                        brokerList.add(b);
                    }
                }
                else if(config.getId().equals("B")) {
                    for(int q = topicNum/2; q<topicNum; q++) {
                        queueList.add(q);
                    }
                    for(int b = 0; b<brokerNum/2; b++) {
                        brokerList.add(b);
                    }
                }
                Collections.shuffle(queueList);

                Collections.shuffle(brokerList);

                ArrayList<Integer> queue = new ArrayList<>();

                for(int i=0; i<subNum; i++){
                    /*
                    * INFORMATIONAL: MKDIR removed from consumer
                    * because it is redundant, i will check if it creates problems(no problems)
                    */

                    for(int t = 0; t<((topicNum/2)/subNum); t++) {
                        queue.add(queueList.remove(0));
                    }

                    if(brokerList.size() == 0) {
                        if(config.getId().equals("A")) {
                            if(brokerNum != 1) {
                                for(int b = brokerNum/2; b<brokerNum; b++) {
                                    brokerList.add(b);
                                }
                            }
                            else {
                                brokerList.add(0);
                            }
                        }
                        else if(config.getId().equals("B")) {
                            if(brokerNum != 1) {
                                for(int b = 0; b<brokerNum/2; b++) {
                                    brokerList.add(b);
                                }
                            }
                            else {
                                brokerList.add(0);
                            }
                        }
                        Collections.shuffle(brokerList);
                    }

                    String bIp = bInfo.get(brokerList.remove(0)).getIp();


                    Consumer c = new Consumer(topicNum,("ConsumerFolder"+"-"+i),config.getPlatform(),queue, bIp);

                    for(Integer a1: queue){
                        System.out.println("will consume from : " + a1);
                    }
                    cList.add(c);
                    queue.clear();
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
                            ex.invokeAll(Collections.singletonList(call),2,TimeUnit.MINUTES);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }finally{
                            c.shutdown();
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
                String type = config.getPubOrSub();
                String id = config.getId();
                String platform = config.getPlatform();
                Utilizer u = new Utilizer(pId, type, id,platform, j);
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
                double avgCPU = u.getAvgCPU();
                double avgMem = u.getAvgMem();
                double throughput = u.getConsumerThroughput();

                System.out.println("Average TEST: " + avgCPU + " " + avgMem);

                finalCPU += avgCPU;
                finalMem += avgMem;
                finalThroughput += throughput;
                cList.clear();
            }

            finalAvgCPU = finalCPU / 3;
            finalAvgMem = finalMem / 3;
            finalAvgThroughput = finalThroughput / 3;

            String testResult = "CONSUMER --> Average CPU Utilization: " + finalAvgCPU + " Average Memory Utilization: " + finalAvgMem + "Average Throughput: " + finalAvgThroughput;

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

}
