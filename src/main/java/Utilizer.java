import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Utilizer implements Runnable{

    private long pId;
    //private byte[] bArray;
    private boolean stop;

    private double avgCPU;
    private double avgMem;

    @Override
    public void run(){
        System.out.println("CPUMEM thread running");
        try{
            int count = 0;
            double totalCPU = 0;
            double totalMem = 0;
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

                    totalCPU+=cpuUtil;
                    totalMem+=memUtil;
                    System.out.println("CPU %" + cpuUtil + " MEM %" + memUtil);
                }
                Thread.sleep(1000);
                count++;
                if(stop)break;
            }
            avgCPU = totalCPU / count;
            avgMem = totalMem / count;
            System.out.println("CPU thread stopped.");
            //TODO: HTTP request to website and live statistics most probably JSON
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    Utilizer(Long pId) {
        this.pId = pId;
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