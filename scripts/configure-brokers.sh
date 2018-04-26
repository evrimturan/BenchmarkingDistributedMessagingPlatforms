#!/bin/bash
sudo-apt-get update
sudo apt-get install unzip
#activemq
wget http://ftp.itu.edu.tr/Mirror/Apache//activemq/5.15.3/apache-activemq-5.15.3-bin.zip
unzip -a apache-activemq-5.15.3-bin.zip

#rabbitmq
echo "deb https://dl.bintray.com/rabbitmq/debian xenial main" | sudo tee /etc/apt/sources.list.d/bintray.rabbitmq.list
sudo apt-get update
wget https://packages.erlang-solutions.com/erlang/esl-erlang/FLAVOUR_1_general/esl-erlang_20.3-1~ubuntu~xenial_amd64.deb
sudo dpkg --install esl-erlang_20.3-1~ubuntu~xenial_amd64.deb
sudo apt-get -f install
sudo dpkg --install esl-erlang_20.3-1~ubuntu~xenial_amd64.deb
wget -O- https://dl.bintray.com/rabbitmq/Keys/rabbitmq-release-signing-key.asc | sudo apt-key add -
sudo apt-get update
sudo apt-get install rabbitmq-server

#kafka
wget http://ftp.itu.edu.tr/Mirror/Apache/kafka/1.0.1/kafka_2.11-1.0.1.tgz
sudo apt-get install openjdk-8-jre
sudo apt-get install openjdk-8-jdk
tar xzvf kafka_2.11-1.0.1.tgz

#hosts file
echo "35.196.67.34 ubuntu-s-1vcpu-1gb-fra1-01" >> /etc/hosts
echo "35.229.33.212 ubuntu-s-1vcpu-1gb-fra1-02" >> /etc/hosts
echo "35.196.143.132 ubuntu-s-1vcpu-1gb-fra1-03" >> /etc/hosts
echo "35.190.161.41 ubuntu-s-1vcpu-1gb-fra1-04" >> /etc/hosts
#asagidakiler degisecek
#echo "159.65.115.195 ubuntu-s-1vcpu-1gb-fra1-05" >> /etc/hosts
#echo "167.99.134.186 ubuntu-s-1vcpu-1gb-fra1-06" >> /etc/hosts
#echo "207.154.218.150 ubuntu-s-1vcpu-1gb-fra1-07" >> /etc/hosts
#echo "159.89.110.187 ubuntu-s-1vcpu-1gb-fra1-08" >> /etc/hosts
echo "DONE"

