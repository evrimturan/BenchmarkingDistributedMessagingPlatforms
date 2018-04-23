import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

class Synchronizer {
    private static final String CONSUMER_1 = "ubuntu-s-1vcpu-1gb-fra1-07";
    private static final String CONSUMER_2 = "ubuntu-s-1vcpu-1gb-fra1-08";

    private String id;
    private String type;

    private PrintWriter printProducer, printProducer1, printConsumer, printConsumer1;
    private BufferedReader readerProducer, readerProducer1, readerConsumer, readerConsumer1;
    private ServerSocket serverSocket,serverSocket1;
    private Socket socket,socket1;

    Synchronizer(String id,String type){
        this.id = id;
        this.type = type;

        if(type.equals("consumer")){
            try {
                serverSocket = new ServerSocket(10001);
                serverSocket1 = new ServerSocket(10002);

                Socket s = serverSocket.accept();
                Socket s1 = serverSocket1.accept();

                System.out.println("Consumer "+ id +" accepted connections.");

                readerConsumer = new BufferedReader(new InputStreamReader(s.getInputStream()));
                readerConsumer1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));

                printConsumer = new PrintWriter(s.getOutputStream());
                printConsumer1 = new PrintWriter(s1.getOutputStream());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(type.equals("producer")){
            if(id.equals("A")){
                try {
                    socket = new Socket(CONSUMER_1,10001);
                    socket1 = new Socket(CONSUMER_2,10001);

                    printProducer = new PrintWriter(socket.getOutputStream());
                    printProducer1 = new PrintWriter(socket1.getOutputStream());

                    readerProducer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    readerProducer1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));

                    System.out.println("Producer A connected to both consumers.");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else if(id.equals("B")){
                try {
                    socket = new Socket(CONSUMER_1,10002);
                    socket1 = new Socket(CONSUMER_2,10002);

                    printProducer = new PrintWriter(socket.getOutputStream());
                    printProducer1 = new PrintWriter(socket1.getOutputStream());

                    readerProducer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    readerProducer1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));

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
                printProducer.println("READY:PRODUCER");
                printProducer1.println("READY:PRODUCER");

                System.out.println("Producer sent ready message to consumer.");

                String first = readerProducer.readLine();
                String second = readerProducer1.readLine();

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

                String first = readerConsumer.readLine();
                String second = readerConsumer1.readLine();

                System.out.println("Read first : "+first+"\nSecond : "+second);

                if(first.equals("READY:PRODUCER")){
                    if(second.equals("READY:PRODUCER")){
                        System.out.println("Consumer "+id+" synced with producer.");
                    }
                }

                printConsumer.println("READY:CONSUMER");
                printConsumer1.println("READY:CONSUMER");

                Thread.sleep(1000);//give them time to start execution

            }catch (IOException | InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }

    void closeConnection(){
        if(type.equals("producer")){
            try {
                socket.close();
                socket1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(type.equals("consumer")){
            try {
                serverSocket.close();
                serverSocket1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
