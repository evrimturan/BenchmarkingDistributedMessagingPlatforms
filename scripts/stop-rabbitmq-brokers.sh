#!/bin/bash

sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-01 "service rabbitmq-server stop;service rabbitmq-server status"
echo "Broker 1 Finished."
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-02 "service rabbitmq-server stop;service rabbitmq-server status"
echo "Broker 2 Finished."
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-03 "service rabbitmq-server stop;service rabbitmq-server status"
echo "Broker 3 Finished."
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-04 "service rabbitmq-server stop;service rabbitmq-server status"
echo "Broker 4 Finished."
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-05 "service rabbitmq-server stop;service rabbitmq-server status"
echo "Broker 5 Finished."
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-06 "service rabbitmq-server stop;service rabbitmq-server status"
echo "Broker 6 Finished."