import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;

public class TestConfiguration {
    private int brokerNum;
    private int pubNum;
    private int subNum;
    private long messageSize;
    private int topicNum;
    private boolean persistent;
    private long dataSize;
    private String platform;
    private List<BrokerInfo> bInfo;
    private String type;
    private String pubOrSub;
    private String id;
    private String test;
    private int serverNum;
    private short replicationFactor;

    TestConfiguration(String Filename){
        try {
            PropertiesConfiguration conf = new PropertiesConfiguration(Filename);
            PropertiesConfiguration brokerConf = new PropertiesConfiguration();
            brokerConf.setDelimiterParsingDisabled(true);
            brokerConf.load("broker.config");
            int i = 1;
            bInfo = new ArrayList<>();
            while(brokerConf.containsKey("broker." + i)){
                System.out.println(brokerConf.getString("broker."+i));
                bInfo.add(new BrokerInfo(i, brokerConf.getString("broker." +i).split(",")[0], brokerConf.getString("broker." +i).split(",")[1]));
                i++;
            }
            //binfo.add(new BrokerInfo());
            serverNum = conf.getInt("server.num");
            pubOrSub = conf.getString("type");
            brokerNum = conf.getInt("broker.amount");
            pubNum = conf.getInt("publisher.amount");
            subNum = conf.getInt("subscriber.amount");
            topicNum = conf.getInt("topic.amount");
            persistent = conf.getBoolean("persistent");
            platform = conf.getString("platform.type");
            id = conf.getString("id");
            test = conf.getString("test");
            replicationFactor = conf.getShort("replication.factor");
            //messageSize = conf.getDouble("message.size");
            String strMessage = conf.getString("message.size");
            type = strMessage;
            if(strMessage.contains("KB")){
                strMessage = strMessage.substring(0,strMessage.indexOf("KB"));
                messageSize = Long.parseLong(strMessage) * 1024;
            }else if(strMessage.contains("MB")){
                strMessage = strMessage.substring(0,strMessage.indexOf("MB"));
                messageSize = Long.parseLong(strMessage) * 1024 * 1024;
            }else if(strMessage.contains("GB")){
                strMessage = strMessage.substring(0,strMessage.indexOf("GB"));
                messageSize = Long.parseLong(strMessage) * 1024 * 1024 * 1024;
            }else{
                System.err.println("Message size no identifier !");
                System.exit(1);
            }
            String strData = conf.getString("data.size");
            if(strData.contains("KB")){
                strData = strData.substring(0,strData.indexOf("KB"));
                dataSize = Long.parseLong(strData) * 1024;
            }else if(strData.contains("MB")){
                strData = strData.substring(0,strData.indexOf("MB"));
                dataSize = Long.parseLong(strData) * 1024 * 1024;
            }else if(strData.contains("GB")){
                strData = strData.substring(0,strData.indexOf("GB"));
                dataSize = Long.parseLong(strData) * 1024 * 1024 * 1024;
            }else{
                System.err.println("Data size no identifier !");
                System.exit(1);
            }
            //dataSize = conf.getDouble("data.size");

        } catch (ConfigurationException e) {
            Logger.getLogger(TestConfiguration.class.toString()).log(Level.SEVERE, e.toString());
            System.exit(1);
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

    public long getMessageSize() {
        return messageSize;
    }

    public int getTopicNum() {
        return topicNum;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public long getDataSize(){
        return dataSize;
    }

    public String getPlatform(){
        return platform;
    }

    public List<BrokerInfo> getBInfo(){
        return bInfo;
    }

    public String getType() {return type;}

    public String getPubOrSub() {return pubOrSub;}

    public String getId() {
        return id;
    }

    public String getTest() {
        return test;
    }

    public int getServerNum() { return serverNum; }

    public short getReplicationFactor() {
        return replicationFactor;
    }

    protected class BrokerInfo{
        private int id;
        private String ip;
        private String name;

        public BrokerInfo(int id, String ip, String name){
            this.id = id;
            this.ip= ip;
            this.name= name;
        }

        public int getId(){
            return id;
        }

        public String getIp(){
            return ip;
        }

        public String getName(){
            return name;
        } 
    }
}
