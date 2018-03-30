#!/bin/bash

hostName="root@ubuntu-s-1vcpu-1gb-fra1-0"

for brokerNumber in $(eval echo "{1..$2}")
do
    sshpass -p "$1" ssh "$hostName$brokerNumber" "service rabbitmq-server start;service rabbitmq-server status"
    echo "Broker $brokerNumber Finished"
    sleep 1
done

