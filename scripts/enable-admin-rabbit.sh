#!/bin/bash

hostName="mustafa@ubuntu-s-1vcpu-1gb-fra1-0"

for brokerNumber in $(eval echo "{1..$1}")
do
    ssh "$hostName$brokerNumber" "sudo rabbitmq-plugins enable rabbitmq_management"
    echo "Broker $brokerNumber Finished"
    sleep 1
done

sudo rabbitmqctl add_user admin admin
sudo rabbitmqctl set_user_tags admin administrator
sudo rabbitmqctl set_permissions -p / admin ".*" ".*" ".*"