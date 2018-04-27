import org.apache.kafka.common.protocol.types.Field;

import java.io.BufferedReader;
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

    private double avgCPU;
    private double avgMem;

    private Socket socket;
    private PrintWriter print;
    private BufferedReader reader;
    private static final String machine_9 = "ubuntu-s-1vcpu-1gb-fra1-09";


    @Override
    public void run(){

        try{
            if(type.equals("producer")){
                if(id.equals("A")){
                    int port = 20000;
                    System.out.println("Trying to connect to "+machine_9 +" port : "+ port);
                    socket = new Socket(machine_9,port);
                    print = new PrintWriter(socket.getOutputStream());
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                }else if(id.equals("B")){
                    int port = 20001;
                    System.out.println("Trying to connect to "+machine_9 +" port : "+ port);
                    socket = new Socket(machine_9,port);
                    print = new PrintWriter(socket.getOutputStream());
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                }
            }else if(type.equals("consumer")){
                if(id.equals("A")){
                    int port = 20002;
                    System.out.println("Trying to connect to "+machine_9 +" port : "+ port);
                    socket = new Socket(machine_9,port);
                    print = new PrintWriter(socket.getOutputStream());
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                }else if(id.equals("B")){
                    int port = 20003;
                    System.out.println("Trying to connect to "+machine_9 +" port : "+ port);
                    socket = new Socket(machine_9,port);
                    print = new PrintWriter(socket.getOutputStream());
                }
            }
        }catch(Exception e){
            System.out.println(e.toString());
            System.out.println("Socket could not open.");
        }
        System.out.println("CPUMEM thread running");
        try{
            int count = 0;
            double totalCPU = 0;
            double totalMem = 0;

            print.println("Data atmaya basliyorum.");
            print.flush();

            if(reader.readLine().equals("OK")){
                print.println("CPU Util: " + 0 + ", MEM Util: "+ 0);
                print.flush();
            }else{
                System.out.println("Machine 9 did not say OK.");
            }

            while(true){
                if(count > 3 && count < 117) {
                    Process process = Runtime.getRuntime().exec("ps -p " + pId + " -o %cpu,%mem");
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(process.getInputStream()));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ( (line = reader.readLine()) != null) {
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
                    if(tokenizer.hasMoreTokens()) {
                        cpuUtil = Double.parseDouble(tokenizer.nextToken());
                    }
                    else {
                        cpuUtil = 0.0;
                    }

                    double memUtil;
                    if(tokenizer.hasMoreTokens()) {
                        memUtil = Double.parseDouble(tokenizer.nextToken());
                    }
                    else {
                        memUtil = 0.0;
                    }

                    print.println("CPU Util: " + cpuUtil + ", MEM Util: "+ memUtil);
                    totalCPU+=cpuUtil;
                    totalMem+=memUtil;
                    System.out.println("CPU %" + cpuUtil + " MEM %" + memUtil);
                }
                Thread.sleep(1000);
                count++;
                if(stop)break;
            }
            print.println("Total CPU Util: "+ totalCPU + ", Total MEM Util: "+ totalMem);
            print.flush();
            avgCPU = totalCPU / count;
            avgMem = totalMem / count;
            print.println("Average CPU Util: "+ avgCPU+ ", Average MEM Util: "+ avgMem);
            print.flush();
            print.println("Data atmayi bitirdim.");
            print.flush();
            System.out.println("CPU thread stopped.");
            //TODO: HTTP request to website and live statistics most probably JSON
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    Utilizer(Long pId, String type, String id) {
        this.pId = pId;
        this.type = type;
        this.id = id;
        this.stop = false;
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
}