#!/bin/bash

hostName="root@ubuntu-s-1vcpu-1gb-fra1-0"

for brokerNumber in $(eval echo "{$2..1}")
do
    if [ $brokerNumber -eq 1 ];
    then
        sshpass -p "$1" ssh "$hostName$brokerNumber" "cd kafka_2.11-1.0.1/;bin/kafka-server-stop.sh;bin/zookeeper-server-stop.sh"
    else
        sshpass -p "$1" ssh "$hostName$brokerNumber" "cd kafka_2.11-1.0.1/;bin/kafka-server-stop.sh"
    fi
    echo "Broker $brokerNumber Finished"
done