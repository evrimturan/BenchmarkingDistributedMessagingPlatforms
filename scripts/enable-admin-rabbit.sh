#!/bin/bash

hostName="root@ubuntu-s-1vcpu-1gb-fra1-0"

for brokerNumber in $(eval echo "{1..$2}")
do
    sshpass -p "$1" ssh "$hostName$brokerNumber" "rabbitmq-plugins enable rabbitmq_management"
    echo "Broker $brokerNumber Finished"
    sleep 1
done
