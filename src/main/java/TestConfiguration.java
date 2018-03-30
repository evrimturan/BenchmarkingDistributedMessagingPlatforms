import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TestConfiguration {
    private int brokerNum;
    private int pubNum;
    private int subNum;
    private double messageSize;
    private int topicNum;
    private boolean persistent;
    private double dataSize;
    private Configuration conf;
    private String platform;

    public TestConfiguration(String Filename){
        try {
            conf = new PropertiesConfiguration(Filename);
            brokerNum = conf.getInt("broker.amount");
            pubNum = conf.getInt("publisher.amount");
            subNum = conf.getInt("subscriber.amount");
            topicNum = conf.getInt("topic.amount");
            persistent = conf.getBoolean("persistent");
            platform = conf.getString("platform.type");
            messageSize = conf.getDouble("message.size");
            dataSize = conf.getDouble("data.size");

        } catch (ConfigurationException e) {
            Logger.getLogger(TestConfiguration.class.toString()).log(Level.SEVERE, e.toString());
            System.exit(1);
        } catch(ConversionException ex){
            messageSize = (double)conf.getInt("message.size");
            dataSize = (double)conf.getInt("data.size");
        }
    }

    public int getBrokerNum() {
        return brokerNum;
    }

    public int getPubNum() {
        return pubNum;
    }

    public int getSubNum() {
        return subNum;
    }

    public double getMessageSize() {
        return messageSize;
    }

    public int getTopicNum() {
        return topicNum;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public double getDataSize(){
        return dataSize;
    }

    public String getPlatform(){
        return platform;
    }

}
