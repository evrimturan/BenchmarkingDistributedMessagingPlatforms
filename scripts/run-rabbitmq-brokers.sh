#!/bin/bash

sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-01 "service rabbitmq-server start;service rabbitmq-server status"
echo "Broker 1 finished."
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-02 "service rabbitmq-server start;service rabbitmq-server status"
echo "Broker 2 finished."
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-03 "service rabbitmq-server start;service rabbitmq-server status"
echo "Broker 3 finished."
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-04 "service rabbitmq-server start;service rabbitmq-server status"
echo "Broker 4 finished."
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-05 "service rabbitmq-server start;service rabbitmq-server status"
echo "Broker 5 finished."
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-06 "service rabbitmq-server start;service rabbitmq-server status"
echo "Broker 6 finished."