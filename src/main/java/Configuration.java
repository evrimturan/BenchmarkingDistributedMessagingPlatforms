
public class Configuration {
    private int brokerNum;
    private int pubNum;
    private int subNum;
    private double messageSize;
    private int topicNum;
    private boolean persistent;

    public Configuration(String Filename){
        
    }

    public int getBrokerNum() {
        return brokerNum;
    }

    public void setBrokerNum(int brokerNum) {
        this.brokerNum = brokerNum;
    }

    public int getPubNum() {
        return pubNum;
    }

    public void setPubNum(int pubNum) {
        this.pubNum = pubNum;
    }

    public int getSubNum() {
        return subNum;
    }

    public void setSubNum(int subNum) {
        this.subNum = subNum;
    }

    public double getMessageSize() {
        return messageSize;
    }

    public void setMessageSize(double messageSize) {
        this.messageSize = messageSize;
    }

    public int getTopicNum() {
        return topicNum;
    }

    public void setTopicNum(int topicNum) {
        this.topicNum = topicNum;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }
}
