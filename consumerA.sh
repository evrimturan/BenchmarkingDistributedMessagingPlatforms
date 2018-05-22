#!/bin/bash

mv activemqtests/messageTest2._activemq.config activemqtests/messageTest2_activemq.config >> /dev/null 2>&1

sed -i -e 's/128MB/2MB/g' activemqtests/messageTest6_activemq.config
sed -i -e 's/512MB/4MB/g' activemqtests/messageTest7_activemq.config

rm activemqtests/messageTest8_activemq.config >> /dev/null 2>&1

sed -i -e 's/128MB/2MB/g' rabbitmqtests/messageTest6_rabbitmq.config
sed -i -e 's/512MB/4MB/g' rabbitmqtests/messageTest7_rabbitmq.config

rm rabbitmqtests/messageTest8_rabbitmq.config >> /dev/null 2>&1

sed -i -e 's/128MB/2MB/g' kafkatests/messageTest6_kafka.config
sed -i -e 's/512MB/4MB/g' kafkatests/messageTest7_kafka.config

rm kafkatests/messageTest8_kafka.config >> /dev/null 2>&1

for i in {1..7}
do
        cp activemqtests/messageTest"$i"_activemq.config activemqtests/nonPersistentMessageTest"$i"_activemq.config
        sed -i -e 's/persistent=true/persistent=false/g' activemqtests/nonPersistentMessageTest"$i"_activemq.config

        cp rabbitmqtests/messageTest"$i"_rabbitmq.config rabbitmqtests/nonPersistentMessageTest"$i"_rabbitmq.config
        sed -i -e 's/persistent=true/persistent=false/g' rabbitmqtests/nonPersistentMessageTest"$i"_rabbitmq.config
done

