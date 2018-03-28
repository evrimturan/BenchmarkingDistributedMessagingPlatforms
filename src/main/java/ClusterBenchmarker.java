import org.apache.kafka.common.Cluster;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ClusterBenchmarker {

    private class Consumer extends Thread{

        @Override
        public void run(){

        }

        public Consumer(){

        }
    }

    private class Producer extends Thread{

        @Override
        public void run(){

        }

        public Producer(){

        }
    }

    public static void main(String[] args) {

        ClusterBenchmarker init = new ClusterBenchmarker();
        TestConfiguration config = new TestConfiguration(args[0]);
        int brokerNum = config.getBrokerNum();
        int pubNum = config.getPubNum();
        int subNum = config.getSubNum();
        double messageSize = config.getMessageSize();
        int topicNum = config.getTopicNum();
        List<Producer> pList = new ArrayList<Producer>();


        for(int i=0; i<pubNum;i++){
            Producer p = init.createProducer();
            pList.add(p);
            Path path = Paths.get("ProducerFolder"+"-"+i);

            if (!Files.exists(path)) {
                File folder = new File("ProducerFolder"+"-"+i);
                folder.mkdir();
            }
        }

    }

    private Producer createProducer(){
        return new Producer();
    }
}
