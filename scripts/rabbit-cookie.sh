#!/bin/bash

COOKIE=$( ssh root@ubuntu-s-1vcpu-1gb-fra1-01 "cat /var/lib/rabbitmq/.erlang.cookie" )
echo "$COOKIE"

ssh mustafa@ubuntu-s-1vcpu-1gb-fra1-02 "printf $COOKIE > /var/lib/rabbitmq/.erlang.cookie;service rabbitmq-server restart"
ssh mustafa@ubuntu-s-1vcpu-1gb-fra1-03 "printf $COOKIE > /var/lib/rabbitmq/.erlang.cookie;service rabbitmq-server restart"
ssh mustafa@ubuntu-s-1vcpu-1gb-fra1-04 "printf $COOKIE > /var/lib/rabbitmq/.erlang.cookie;service rabbitmq-server restart"
#sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-05 "printf $COOKIE > /var/lib/rabbitmq/.erlang.cookie;service rabbitmq-server restart"
#sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-06 "printf $COOKIE > /var/lib/rabbitmq/.erlang.cookie;service rabbitmq-server restart"
