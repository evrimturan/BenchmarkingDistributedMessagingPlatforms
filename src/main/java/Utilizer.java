import java.io.ByteArrayOutputStream;

public class Utilizer implements Runnable{

    private long pId;
    private byte[] bArray;
    private Process process = null;
    private String output;
    private String[] split;
    private String[] split2;
    private double cpuUtil;
    private double memUtil;
    private Boolean stop;

    @Override
    public void run(){

        try{
            while(true){
                process = Runtime.getRuntime().exec("ps -p " + pId + " -o %cpu,%mem");
                bArray = ((ByteArrayOutputStream) process.getOutputStream()).toByteArray();
                output = new String(bArray);
                split = output.split("\n"); 
                split2 = split[1].split(" ");
                cpuUtil = Double.parseDouble(split2[0]);
                memUtil = Double.parseDouble(split2[1]);
                System.out.println("CPU %" + cpuUtil + " MEM %" + memUtil);
                Thread.sleep(1000);
                if(stop)break;
            }
            System.out.println("CPU thread stopped.");
        }catch(Exception e)
        {

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