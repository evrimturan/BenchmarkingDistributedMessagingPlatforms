import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilizer implements Runnable{

    private long pId;
    private byte[] bArray;
    private Process process = null;
    private double cpuUtil;
    private double memUtil;
    private Boolean stop;

    @Override
    public void run(){
        System.out.println("CPUMEM thread running");
        try{
            while(true){
                process = Runtime.getRuntime().exec("ps -p " + pId + " -o %cpu,%mem");
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

                if(tokenizer.hasMoreTokens()) {
                    cpuUtil = Double.parseDouble(tokenizer.nextToken());
                }
                else {
                    cpuUtil = 0.0;
                }

                if(tokenizer.hasMoreTokens()) {
                    memUtil = Double.parseDouble(tokenizer.nextToken());
                }
                else {
                    memUtil = 0.0;
                }




                System.out.println("CPU %" + cpuUtil + " MEM %" + memUtil);
                Thread.sleep(1000);
                if(stop)break;
            }
            System.out.println("CPU thread stopped.");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public Utilizer(Long pId)
    {
        this.pId = pId;
        this.stop = false;
    }

    public void setStop(Boolean halt){
        stop = halt;
    }
}