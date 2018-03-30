#!/bin/bash

hostName="root@ubuntu-s-1vcpu-1gb-fra1-0"

for brokerNumber in $(eval echo "{1..$2}")
do
    if [ $brokerNumber -eq 1 ];
    then
        echo "Broker 1 Zookeeper Kafka"
        sshpass -p "$1" ssh "$hostName$brokerNumber" "cd kafka_2.11-1.0.1/;bin/zookeeper-server-start.sh -daemon config/zookeeper.properties;bin/kafka-server-start.sh -daemon config/server.properties"
    else
        echo "Brokers Kafka"
        sshpass -p "$1" ssh "$hostName$brokerNumber" "cd kafka_2.11-1.0.1/;bin/kafka-server-start.sh -daemon config/server.properties"
    fi
    echo "Broker $brokerNumber Finished"
done
