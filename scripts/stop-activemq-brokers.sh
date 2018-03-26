#!/bin/bash

sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-01 "cd apache-activemq-5.15.3/bin/;./activemq stop"
echo "Broker 1 Finished"
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-02 "cd apache-activemq-5.15.3/bin/;./activemq stop"
echo "Broker 2 Finished"
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-03 "cd apache-activemq-5.15.3/bin/;./activemq stop"
echo "Broker 3 Finished"
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-04 "cd apache-activemq-5.15.3/bin/;./activemq stop"
echo "Broker 4 Finished"
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-05 "cd apache-activemq-5.15.3/bin/;./activemq stop"
echo "Broker 5 Finished"
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-06 "cd apache-activemq-5.15.3/bin/;./activemq stop"
echo "Broker 6 Finished"
