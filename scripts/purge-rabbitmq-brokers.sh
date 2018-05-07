#!/bin/bash

ssh mustafa@ubuntu-s-1vcpu-1gb-fra1-01 "sudo apt-get -y purge rabbitmq-server;sudo apt-get -y install rabbitmq-server"
ssh mustafa@ubuntu-s-1vcpu-1gb-fra1-02 "sudo apt-get -y purge rabbitmq-server;sudo apt-get -y install rabbitmq-server"
ssh mustafa@ubuntu-s-1vcpu-1gb-fra1-03 "sudo apt-get -y purge rabbitmq-server;sudo apt-get -y install rabbitmq-server"
ssh mustafa@ubuntu-s-1vcpu-1gb-fra1-04 "sudo apt-get -y purge rabbitmq-server;sudo apt-get -y install rabbitmq-server"
#sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-05 "sudo apt-get purge rabbitmq-server;sudo apt-get -y install rabbitmq-server"
#sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-06 "apt-get purge rabbitmq-server;apt-get -y install rabbitmq-server"