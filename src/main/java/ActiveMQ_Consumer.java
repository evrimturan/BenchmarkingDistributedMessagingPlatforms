import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;

public class ActiveMQ_Consumer {
    public static void main(String[] args) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://ubuntu-s-1vcpu-1gb-fra1-05:61616");
        try{
            Connection connection = connectionFactory.createConnection("admin","admin");
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue("Evrim");

            // Create a MessageConsumer from the Session to the Topic or Queue
            MessageConsumer consumer = session.createConsumer(destination);

            // Wait for a message
            /*Message message = consumer.receive(100);

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();
                System.out.println("Received: " + text);
            } else {
                System.out.println("Received: " + message);
            }*/

            MessageListener listener = new MessageListener() {
                public void onMessage(Message message) {
                    if (message instanceof TextMessage) {
                        ActiveMQTextMessage textMessage = (ActiveMQTextMessage) message;
                        try {
                            Thread.sleep(3000);
                            String text = textMessage.getText();
                            System.out.println("Received: " + text);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Received: " + message);
                    }
                }
            };
            consumer.setMessageListener(listener);


        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
