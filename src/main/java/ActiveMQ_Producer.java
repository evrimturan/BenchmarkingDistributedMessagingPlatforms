import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class ActiveMQ_Producer {
    public static void main(String[] args) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://159.89.102.49:61616");
        try{
            Connection connection = connectionFactory.createConnection("admin","admin");
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue("Evrim");

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a messages
            for(int i = 0;i<5;i++){
                String text = "Al proje bitti " + i;
                TextMessage message = session.createTextMessage(text);

                // Tell the producer to send the message
                //System.out.println("Sent message: "+ message.hashCode() + " : " + Thread.currentThread().getName());
                producer.send(message);
            }

            // Clean up
            session.close();
            connection.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }
}
