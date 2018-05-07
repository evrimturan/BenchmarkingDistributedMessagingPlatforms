import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

public class Utilizer implements Runnable{

    private long pId;
    private String type;
    private String id;
    //private byte[] bArray;
    private boolean stop;
    private String platform;
    private int iteration;
    private String testName;

    private double avgCPU;
    private double avgMem;
    private double producerThroughput;
    private double consumerThroughput;

    private static double finalCPU = 0;
    private static double finalMem = 0;
    private static double finalProducerThroughput = 0;
    private static double finalConsumerThroughput = 0;

    private Socket socket;
    private PrintWriter print;
    private BufferedReader reader;
    private static final String machine_9 = "ubuntu-s-1vcpu-1gb-fra1-09";


    @Override
    public void run() {
        class Test {
            String asdf;
        }
        try {
            if (type.equals("producer")) {
                if (id.equals("A")) {
                    int port = 20000;
                    System.out.println("Trying to connect to " + machine_9 + " port : " + port);
                    socket = new Socket(machine_9, port);
                    print = new PrintWriter(socket.getOutputStream());
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } else if (id.equals("B")) {
                    int port = 20001;
                    System.out.println("Trying to connect to " + machine_9 + " port : " + port);
                    socket = new Socket(machine_9, port);
                    print = new PrintWriter(socket.getOutputStream());
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                }
            } else if (type.equals("consumer")) {
                if (id.equals("A")) {
                    int port = 20002;
                    System.out.println("Trying to connect to " + machine_9 + " port : " + port);
                    socket = new Socket(machine_9, port);
                    print = new PrintWriter(socket.getOutputStream());
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } else if (id.equals("B")) {
                    int port = 20003;
                    System.out.println("Trying to connect to " + machine_9 + " port : " + port);
                    socket = new Socket(machine_9, port);
                    print = new PrintWriter(socket.getOutputStream());
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("Socket could not open.");
        }
        System.out.println("CPUMEM thread running of id :" + id);
        try {
            int count = 0;
            double totalCPU = 0;
            double totalMem = 0;

            print.println("Data atmaya basliyorum.");
            print.flush();

            if (reader.readLine().equals("OK")) {
                print.println(0 + "," + 0);
                print.flush();
            } else {
                System.out.println("Machine 9 did not say OK.");
            }

            while (true) {
                if (count > 3 && count < 117) {
                    Process process = Runtime.getRuntime().exec("ps -p " + pId + " -o %cpu,%mem");
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(process.getInputStream()));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                        builder.append(System.getProperty("line.separator"));
                    }
                    String output = builder.toString();
                    //output = output.substring(output.indexOf(' '),output.length());
                    //System.out.println(output);
                    //output = new String(bArray);
                    String[] split = output.split("\n");
                    String fin = split[1];

                    StringTokenizer tokenizer = new StringTokenizer(fin, " ");
                    double cpuUtil;
                    if (tokenizer.hasMoreTokens()) {
                        cpuUtil = Double.parseDouble(tokenizer.nextToken());
                    } else {
                        cpuUtil = 0.0;
                    }

                    double memUtil;
                    if (tokenizer.hasMoreTokens()) {
                        memUtil = Double.parseDouble(tokenizer.nextToken());
                    } else {
                        memUtil = 0.0;
                    }

                    print.println(cpuUtil + "," + memUtil);
                    print.flush();
                    totalCPU += cpuUtil;
                    totalMem += memUtil;
                    System.out.println("CPU %" + cpuUtil + " MEM %" + memUtil);
                }
                Thread.sleep(1000);
                count++;
                if (stop) break;
            }
            if (type.equals("producer")) {
                print.println(totalCPU + "," + totalMem + "," + Producer.getCounter());
            } else if (type.equals("consumer")) {
                print.println(totalCPU + "," + totalMem + "," + Consumer.getCounter());
            }
            print.flush();
            avgCPU = totalCPU / count;
            avgMem = totalMem / count;
            producerThroughput = Producer.getCounter() / 114;
            consumerThroughput = Consumer.getCounter() / 114;

            finalCPU += avgCPU;
            finalMem += avgMem;
            finalProducerThroughput += producerThroughput;
            finalConsumerThroughput += producerThroughput;

            if (iteration == 2) {
                double finalAvgCPU = finalCPU / 3;
                double finalAvgMem = finalMem / 3;
                double finalAvgProducerThroughput = finalProducerThroughput / 3;
                double finalAvgConsumerThroughput = finalConsumerThroughput / 3;

                if (type.equals("producer")) {
                    print.println(platform + "," + finalAvgCPU + "," + finalAvgMem + "," + finalAvgProducerThroughput);
                } else if (type.equals("consumer")) {
                    print.println(platform + "," + finalAvgCPU + "," + finalAvgMem + "," + finalAvgConsumerThroughput);
                }
                if (type.equals("producer")) {
                    print.println("TEST," + testName + "," + avgCPU + "," + avgMem + "," + producerThroughput);
                } else if (type.equals("consumer")) {
                    print.println("TEST," + testName + "," + avgCPU + "," + avgMem + "," + consumerThroughput);
                }

                Producer.setCounter(0);
                Consumer.setCounter(0);
                print.flush();
                print.println("Data atmayi bitirdim.");
                print.flush();
                System.out.println("CPU thread stopped.");
                socket.close();
                //TODO: HTTP request to website and live statistics most probably JSON
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Utilizer(Long pId, String type, String id, String platform,String testName, int iteration) {
        this.pId = pId;
        this.type = type;
        this.id = id;
        this.stop = false;
        this.platform = platform;
        this.testName = testName;
        this.iteration = iteration;
    }

    void setStop(boolean halt){
        stop = halt;
    }

    double getAvgCPU() {
        return avgCPU;
    }

    double getAvgMem() {
        return avgMem;
    }

    public double getProducerThroughput() {
        return producerThroughput;
    }

    public double getConsumerThroughput() {
        return consumerThroughput;
    }
}