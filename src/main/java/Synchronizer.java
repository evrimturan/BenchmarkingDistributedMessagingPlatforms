import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class Synchronizer {
    private static final String CONSUMER_1 = "ubuntu-s-1vcpu-1gb-fra1-07";
    private static final String CONSUMER_2 = "ubuntu-s-1vcpu-1gb-fra1-08";


    private String id;
    private String type;

    private List<ServerSocket> serverSockets;
    private List<Socket> sockets;

    Synchronizer(String id,String type){
        serverSockets = new ArrayList<>();
        sockets = new ArrayList<>();
        this.id = id;
        this.type = type;

        if(type.equals("consumer")){
            try {
                ServerSocket serverSocket = new ServerSocket(10001);
                ServerSocket serverSocket1 = new ServerSocket(10002);
                serverSockets.add(serverSocket);
                serverSockets.add(serverSocket1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(type.equals("producer")){
            if(id.equals("A")){
                try {
                    Socket socket = new Socket(CONSUMER_1,10001);
                    Socket socket1 = new Socket(CONSUMER_2,10001);
                    sockets.add(socket);
                    sockets.add(socket1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else if(id.equals("B")){
                try {
                    Socket socket = new Socket(CONSUMER_1,10002);
                    Socket socket1 = new Socket(CONSUMER_2,10002);
                    sockets.add(socket);
                    sockets.add(socket1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    void sync(){
        if(type.equals("producer")){
            try {
                PrintWriter printWriter = new PrintWriter(sockets.get(0).getOutputStream());
                PrintWriter printWriter1 = new PrintWriter(sockets.get(1).getOutputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(sockets.get(0).getInputStream()));
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(sockets.get(1).getInputStream()));

                printWriter.println("READY:PRODUCER");
                printWriter1.println("READY:PRODUCER");

                if(reader.readLine().equals("READY:CONSUMER") && reader1.readLine().equals("READY:CONSUMER")){
                    System.out.println("Producer synced with consumer.");
                }

                Thread.sleep(1000);//Give them time to start execution

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }else if(type.equals("consumer")){
            try{
                System.out.printf("Consumer %s is ready to sync.\n",id);
                Socket s = serverSockets.get(0).accept();
                Socket s1 = serverSockets.get(1).accept();

                BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));

                PrintWriter pw = new PrintWriter(s.getOutputStream());
                PrintWriter pw1 = new PrintWriter(s1.getOutputStream());

                if(reader.readLine().equals("READY:PRODUCER") && reader1.readLine().equals("READY:PRODUCER")){
                    System.out.println("Consumer is synced with producer.");
                }

                pw.println("READY:CONSUMER");
                pw1.println("READY:CONSUMER");

                Thread.sleep(1000);//give them time to start execution

            }catch (IOException | InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }
}
