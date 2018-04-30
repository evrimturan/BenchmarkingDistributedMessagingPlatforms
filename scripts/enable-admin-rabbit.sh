#!/bin/bash

hostName="mustafa@ubuntu-s-1vcpu-1gb-fra1-0"

for brokerNumber in $(eval echo "{1..$1}")
do
    ssh "$hostName$brokerNumber" "sudo rabbitmq-plugins enable rabbitmq_management"
    echo "Broker $brokerNumber Finished"
    sleep 1
done
