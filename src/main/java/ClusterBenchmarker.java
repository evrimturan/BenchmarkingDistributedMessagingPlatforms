import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.*;

@SuppressWarnings("ALL")
public class ClusterBenchmarker {
    static boolean isPersistent;

    public static void main(String[] args) {

        TestConfiguration config = new TestConfiguration(args[0]);

        isPersistent = config.isPersistent();
        int brokerNum = config.getBrokerNum();
        int pubNum = config.getPubNum();
        int subNum = config.getSubNum();
        long messageSize = config.getMessageSize();
        long dataSize = config.getDataSize();
        int topicNum = config.getTopicNum();
        int serverNumber = config.getServerNum();
        List<TestConfiguration.BrokerInfo> bInfo = config.getBInfo();

        List<Producer> pList = new ArrayList<>();
        List<Consumer> cList = new ArrayList<>();

        double finalCPU = 0;
        double finalMem = 0;
        double finalThroughput = 0;

        double finalAvgCPU;
        double finalAvgMem;
        double finalAvgThroughput;

        if(config.getPubOrSub().equals("producer")){
            Synchronizer synchronizer;
            if(config.getTest().contains("zigzag")){
                synchronizer = null;
            }else{
                synchronizer = new Synchronizer(config.getId(),"producer");
            }

            for(int j = 0;j<3;j++){

                if(config.getTest().contains("zigzag")) {
                    if(config.getTest().equals("zigzag1")) {
                        Producer producer;
                        Consumer consumer;

                        String brokerIP = null;
                        ArrayList<Integer> queueNumber = new ArrayList<>();


                        if(config.getServerNum() == 0) {
                            queueNumber.add(0);
                            brokerIP = bInfo.get(0).getIp();
                            producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), true);
                            pList.add(producer);
                            queueNumber.clear();

                            queueNumber.add(0);
                            brokerIP = bInfo.get(0).getIp();
                            consumer = new Consumer(topicNum,("ConsumerFolder"+"-"+ 0),config.getPlatform(),queueNumber, brokerIP);
                            cList.add(consumer);
                            queueNumber.clear();
                        }

                    }

                    else if(config.getTest().equals("zigzag2")) {
                        Producer producer;
                        Consumer consumer;

                        String brokerIP = null;
                        ArrayList<Integer> queueNumber = new ArrayList<>();


                        if(config.getServerNum() == 0) {
                            queueNumber.add(0);
                            brokerIP = bInfo.get(0).getIp();
                            producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), true);
                            pList.add(producer);
                            queueNumber.clear();

                            queueNumber.add(1);
                            brokerIP = bInfo.get(1).getIp();
                            consumer = new Consumer(topicNum,("ConsumerFolder"+"-"+ 0),config.getPlatform(),queueNumber, brokerIP);
                            cList.add(consumer);
                            queueNumber.clear();
                        }

                        else if(config.getServerNum() == 1) {
                            queueNumber.add(0);
                            brokerIP = bInfo.get(0).getIp();
                            consumer = new Consumer(topicNum,("ConsumerFolder"+"-"+ 0),config.getPlatform(),queueNumber, brokerIP);
                            cList.add(consumer);
                            queueNumber.clear();

                            queueNumber.add(1);
                            brokerIP = bInfo.get(1).getIp();
                            producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), true);
                            pList.add(producer);
                            queueNumber.clear();
                        }

                    }

                    else if(config.getTest().equals("zigzag3")) {
                        //TODO queue numarası ver, sonra clear yap. hepsine yap
                        Producer producer;
                        Consumer consumer;

                        String brokerIP = null;
                        ArrayList<Integer> queueNumber = new ArrayList<>();

                        if(config.getServerNum() == 0) {
                            queueNumber.add(0);
                            brokerIP = bInfo.get(0).getIp();
                            producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), true);
                            pList.add(producer);
                            queueNumber.clear();

                            queueNumber.add(3);
                            brokerIP = bInfo.get(3).getIp();
                            consumer = new Consumer(topicNum,("ConsumerFolder"+"-"+ 0),config.getPlatform(),queueNumber, brokerIP);
                            cList.add(consumer);
                            queueNumber.clear();
                        }

                        else if(config.getServerNum() == 1) {
                            queueNumber.add(0);
                            brokerIP = bInfo.get(0).getIp();
                            consumer = new Consumer(topicNum,("ConsumerFolder"+"-"+ 0),config.getPlatform(),queueNumber, brokerIP);
                            cList.add(consumer);
                            queueNumber.clear();

                            queueNumber.add(1);
                            brokerIP = bInfo.get(1).getIp();
                            producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), true);
                            pList.add(producer);
                            queueNumber.clear();
                        }

                        else if(config.getServerNum() == 2) {
                            queueNumber.add(1);
                            brokerIP = bInfo.get(1).getIp();
                            consumer = new Consumer(topicNum,("ConsumerFolder"+"-"+ 0),config.getPlatform(),queueNumber, brokerIP);
                            cList.add(consumer);
                            queueNumber.clear();

                            queueNumber.add(2);
                            brokerIP = bInfo.get(2).getIp();
                            producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), true);
                            pList.add(producer);
                            queueNumber.clear();
                        }

                        else if(config.getServerNum() == 3) {
                            queueNumber.add(2);
                            brokerIP = bInfo.get(2).getIp();
                            consumer = new Consumer(topicNum,("ConsumerFolder"+"-"+ 0),config.getPlatform(),queueNumber, brokerIP);
                            cList.add(consumer);
                            queueNumber.clear();


                            queueNumber.add(3);
                            brokerIP = bInfo.get(3).getIp();
                            producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), queueNumber, brokerIP, config.getType(), config.getId(), config.getServerNum(), true);
                            pList.add(producer);
                            queueNumber.clear();
                        }

                    }
                }

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


                                if(config.getPlatform().equals("rabbitmq")) {
                                    if(config.getId().equals("A")) {
                                        producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), Arrays.asList(0), bInfo.get(0).getIp(), config.getType(), config.getId(), config.getServerNum(), false);
                                        pList.add(producer);
                                        producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), Arrays.asList(0), bInfo.get(0).getIp(), config.getType(), config.getId(), config.getServerNum(), false);
                                        pList.add(producer);
                                        producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), Arrays.asList(1), bInfo.get(1).getIp(), config.getType(), config.getId(), config.getServerNum(), false);
                                        pList.add(producer);
                                        producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), Arrays.asList(1), bInfo.get(1).getIp(), config.getType(), config.getId(), config.getServerNum(), false);
                                        pList.add(producer);
                                    }
                                    else if(config.getId().equals("B")){
                                        producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), Arrays.asList(2), bInfo.get(2).getIp(), config.getType(), config.getId(), config.getServerNum(), false);
                                        pList.add(producer);
                                        producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), Arrays.asList(2), bInfo.get(2).getIp(), config.getType(), config.getId(), config.getServerNum(), false);
                                        pList.add(producer);
                                        producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), Arrays.asList(3), bInfo.get(3).getIp(), config.getType(), config.getId(), config.getServerNum(), false);
                                        pList.add(producer);
                                        producer = new Producer(messageSize, dataSize / pubNum, topicNum, ("ProducerFolder-" + 0), config.getPlatform(), Arrays.asList(3), bInfo.get(3).getIp(), config.getType(), config.getId(), config.getServerNum(), false);
                                        pList.add(producer);
                                    }
                                }

                                else {
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
                System.out.println("Su anda burdayim 0");
                ScheduledExecutorService ex;
                if(config.getTest().contains("zigzag")){
                    ex = Executors.newScheduledThreadPool(2);
                }else{
                    ex = Executors.newScheduledThreadPool(pList.size());
                }
                List<Thread> threadList = new ArrayList<>();
                System.out.println("Buraya girdiler. 1");

                if(!config.getTest().contains("zigzag")) {
                    synchronizer.sync();
                }

                System.out.println("buradayim 2");
                System.out.println("Clist size : " + cList.size() + " Plist size: "+pList.size());
                for(Consumer c : cList){
                    Callable<Void> call = () -> {
                        System.out.println("Running Consumer AGAIN");
                        c.run();
                        return null;
                    };
                    Thread temp = new Thread(() -> {
                        try {
                            ex.invokeAll(Collections.singletonList(call),2,TimeUnit.MINUTES);
                        } catch (InterruptedException e) {
                            //e.printStackTrace(); This is redundant
                        } c.shutdown();
                    });
                    threadList.add(temp);
                    temp.start();
                    System.out.println("--------------------------------");
                }
                System.out.println("Consumer bitti");
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
                String testName = args[0].replace(".config",".test");
                testName = testName.split("/")[1];
                System.out.printf("NAME OF TEST:"+testName);
                Utilizer u = new Utilizer(pId, type, id,platform,testName,j,serverNumber);
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

                for(Producer p: pList){
                    p.deleteQueues();
                }

                if(!config.getTest().contains("zigzag")) {
                    synchronizer.sync();
                }

                if(!config.getTest().contains("zigzag")){
                    pList.clear();
                }else{
                    pList.clear();
                    cList.clear();
                }
                Producer.setDeleteTopics(true);
                if(Producer.isDeleteTopics()) {
                    System.out.println("Delete Topics True");
                }

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
            System.exit(0);

        }else if(config.getPubOrSub().equals("consumer")){
            Synchronizer synchronizer = new Synchronizer(config.getId(),"consumer");
            for(int j = 0;j<3;j++){
                //TODO: consumer'lar nasil handle edilecek onemli onu konusmamiz lazim

                if(config.getPlatform().equals("rabbitmq") && topicNum == 4 && config.getTest().contains("topic")) {
                    Consumer c = null;
                    if(config.getId().equals("A")) {
                        c = new Consumer(topicNum,("ConsumerFolder"+"-"+0),config.getPlatform(),Arrays.asList(0), bInfo.get(2).getIp());
                        cList.add(c);
                        c = new Consumer(topicNum,("ConsumerFolder"+"-"+0),config.getPlatform(),Arrays.asList(0), bInfo.get(2).getIp());
                        cList.add(c);
                        c = new Consumer(topicNum,("ConsumerFolder"+"-"+0),config.getPlatform(),Arrays.asList(1), bInfo.get(3).getIp());
                        cList.add(c);
                        c = new Consumer(topicNum,("ConsumerFolder"+"-"+0),config.getPlatform(),Arrays.asList(1), bInfo.get(3).getIp());
                        cList.add(c);
                    }
                    else if(config.getId().equals("B")){
                        c = new Consumer(topicNum,("ConsumerFolder"+"-"+0),config.getPlatform(),Arrays.asList(2), bInfo.get(0).getIp());
                        cList.add(c);
                        c = new Consumer(topicNum,("ConsumerFolder"+"-"+0),config.getPlatform(),Arrays.asList(2), bInfo.get(0).getIp());
                        cList.add(c);
                        c = new Consumer(topicNum,("ConsumerFolder"+"-"+0),config.getPlatform(),Arrays.asList(3), bInfo.get(1).getIp());
                        cList.add(c);
                        c = new Consumer(topicNum,("ConsumerFolder"+"-"+0),config.getPlatform(),Arrays.asList(3), bInfo.get(1).getIp());
                        cList.add(c);
                    }
                }

                else {
                    ArrayList<Integer> queueList = new ArrayList<>();
                    ArrayList<Integer> brokerList = new ArrayList<>();

                    if (config.getId().equals("A")) {
                        for (int q = 0; q < topicNum / 2; q++) {
                            queueList.add(q);
                        }
                        for (int b = brokerNum / 2; b < brokerNum; b++) {
                            brokerList.add(b);
                        }
                    } else if (config.getId().equals("B")) {
                        for (int q = topicNum / 2; q < topicNum; q++) {
                            queueList.add(q);
                        }
                        for (int b = 0; b < brokerNum / 2; b++) {
                            brokerList.add(b);
                        }
                    }
                    if (queueList.isEmpty()) {
                        queueList.add(0);
                    }
                    Collections.shuffle(queueList);

                    Collections.shuffle(brokerList);

                    System.out.println("QUEUE LIST : ");
                    queueList.forEach(System.out::println);
                    ArrayList<Integer> queue = new ArrayList<>();
                    if (topicNum / 2 < subNum) {
                        List<Integer> dup = new ArrayList<>();
                        if ((int) (topicNum / 2) == 0) {
                            for (int k = 0; k < 1; k++) {
                                dup.addAll(queueList);
                            }
                        } else {
                            for (int k = 0; k < subNum / (topicNum / 2); k++) {
                                dup.addAll(queueList);
                            }
                        }
                        queueList.addAll(dup);
                    }
                    System.out.println("new Qqueue list");
                    queueList.forEach(System.out::println);
                    for (int i = 0; i < subNum; i++) {
                        /*
                         * INFORMATIONAL: MKDIR removed from consumer
                         * because it is redundant, i will check if it creates problems(no problems)
                         */
                        int iteration = (topicNum / 2) / subNum;
                        if (iteration == 0) {
                            iteration++;
                        }
                        for (int t = 0; t < iteration; t++) {
                            queue.add(queueList.remove(0));
                        }

                        if (brokerList.size() == 0) {
                            if (config.getId().equals("A")) {
                                if (brokerNum != 1) {
                                    for (int b = brokerNum / 2; b < brokerNum; b++) {
                                        brokerList.add(b);
                                    }
                                } else {
                                    brokerList.add(0);
                                }
                            } else if (config.getId().equals("B")) {
                                if (brokerNum != 1) {
                                    for (int b = 0; b < brokerNum / 2; b++) {
                                        brokerList.add(b);
                                    }
                                } else {
                                    brokerList.add(0);
                                }
                            }
                            Collections.shuffle(brokerList);
                        }

                        String bIp = bInfo.get(brokerList.remove(0)).getIp();

                        System.out.println("Queue Size: " + queue.size());


                        Consumer c = new Consumer(topicNum, ("ConsumerFolder" + "-" + i), config.getPlatform(), queue, bIp);

                        for (Integer a1 : queue) {
                            System.out.println("will consume from : " + a1);
                        }
                        cList.add(c);
                        queue.clear();
                    }
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
                    String testName = args[0].replace(".config",".test");
                    testName = testName.split("/")[1];
                    System.out.printf("NAME OF TEST:"+testName);
                    Utilizer u = new Utilizer(pId, type, id,platform,testName,j,serverNumber);
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


                    synchronizer.sync();


                    cList.clear();

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

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

            System.exit(0);
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
