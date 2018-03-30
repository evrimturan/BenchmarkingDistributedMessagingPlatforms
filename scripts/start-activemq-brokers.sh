#!/bin/bash

hostName="root@ubuntu-s-1vcpu-1gb-fra1-0"

for brokerNumber in $(eval echo "{1..$2}")
do
    sshpass -p "$1" ssh "$hostName$brokerNumber" "cd apache-activemq-5.15.3/bin/;chmod +x activemq;./activemq start"
    echo "Broker $brokerNumber Finished"
done

