#!/bin/bash

sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-01 "cd kafka_2.11-1.0.1/;bin/kafka-server-start.sh -daemon config/server.properties"
echo "Broker 1 finished"
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-02 "cd kafka_2.11-1.0.1/;bin/kafka-server-start.sh -daemon config/server.properties"
echo "Broker 2 finished"
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-03 "cd kafka_2.11-1.0.1/;bin/kafka-server-start.sh -daemon config/server.properties"
echo "Broker 3 finished"
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-04 "cd kafka_2.11-1.0.1/;bin/kafka-server-start.sh -daemon config/server.properties"
echo "Broker 4 finished"
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-05 "cd kafka_2.11-1.0.1/;bin/kafka-server-start.sh -daemon config/server.properties"
echo "Broker 5 finished"
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-06 "cd kafka_2.11-1.0.1/;bin/kafka-server-start.sh -daemon config/server.properties"
echo "Broker 6 finished"
