#!/bin/bash

sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-01 "apt-get purge rabbitmq-server;apt-get -y install rabbitmq-server"
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-02 "apt-get purge rabbitmq-server;apt-get -y install rabbitmq-server"
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-03 "apt-get purge rabbitmq-server;apt-get -y install rabbitmq-server"
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-04 "apt-get purge rabbitmq-server;apt-get -y install rabbitmq-server"
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-05 "apt-get purge rabbitmq-server;apt-get -y install rabbitmq-server"
sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-06 "apt-get purge rabbitmq-server;apt-get -y install rabbitmq-server"