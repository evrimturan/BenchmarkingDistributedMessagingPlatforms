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
                    System.out.println("Producer A connected to both consumers.");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else if(id.equals("B")){
                try {
                    Socket socket = new Socket(CONSUMER_1,10002);
                    Socket socket1 = new Socket(CONSUMER_2,10002);
                    sockets.add(socket);
                    sockets.add(socket1);
                    System.out.println("Producer B connected to both consumers.");
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

                System.out.println("Producer sent ready message to consumer.");

                String first = reader.readLine();
                String second = reader1.readLine();

                System.out.println("Read first : "+first+"\nSecond : "+second);

                if(first.equals("READY:CONSUMER")){
                    if(second.equals("READY:CONSUMER")){
                        System.out.println("Producer "+id+" synced with consumer.");
                    }
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

                System.out.println("Consumer "+ id +" accepted connections.");

                BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));

                PrintWriter pw = new PrintWriter(s.getOutputStream());
                PrintWriter pw1 = new PrintWriter(s1.getOutputStream());

                String first = reader.readLine();
                String second = reader1.readLine();

                System.out.println("Read first : "+first+"\nSecond : "+second);

                if(first.equals("READY:PRODUCER")){
                    if(second.equals("READY:Producer")){
                        System.out.println("Consumer "+id+" synced with producer.");
                    }
                }

                pw.println("READY:CONSUMER");
                pw1.println("READY:CONSUMER");

                Thread.sleep(1000);//give them time to start execution
                s.close();
                s1.close();

            }catch (IOException | InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }
}
