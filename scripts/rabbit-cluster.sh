#!/bin/bash

COOKIE=$( sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-01 "cat /var/lib/rabbitmq/.erlang.cookie | tr -d '\n'" )
echo "Cookie is : $COOKIE"

sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-02 "mv /var/lib/rabbitmq/.erlang.cookie /var/lib/rabbitmq/.temp.erlang.cookie;echo "$COOKIE" > /var/lib/rabbitmq/.erlang.cookie;chmod 400 /var/lib/rabbitmq/.erlang.cookie;chown rabbitmq /var/lib/rabbitmq/.erlang.cookie;rabbitmqctl stop_app; rabbitmqctl join_cluster rabbit@ubuntu-s-1vcpu-1gb-fra1-01;rabbitmqctl start_app"
echo "Brokers 1 - 2 clustered"

sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-03 "mv /var/lib/rabbitmq/.erlang.cookie /var/lib/rabbitmq/.temp.erlang.cookie;echo "$COOKIE" > /var/lib/rabbitmq/.erlang.cookie;chmod 400 /var/lib/rabbitmq/.erlang.cookie;chown rabbitmq /var/lib/rabbitmq/.erlang.cookie;rabbitmqctl stop_app; rabbitmqctl join_cluster rabbit@ubuntu-s-1vcpu-1gb-fra1-02;rabbitmqctl start_app"
echo "Brokers 2 - 3 clustered"

sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-04 "mv /var/lib/rabbitmq/.erlang.cookie /var/lib/rabbitmq/.temp.erlang.cookie;echo "$COOKIE" > /var/lib/rabbitmq/.erlang.cookie;chmod 400 /var/lib/rabbitmq/.erlang.cookie;chown rabbitmq /var/lib/rabbitmq/.erlang.cookie;rabbitmqctl stop_app; rabbitmqctl join_cluster rabbit@ubuntu-s-1vcpu-1gb-fra1-03;rabbitmqctl start_app"
echo "Brokers 3 - 4 clustered"

sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-05 "mv /var/lib/rabbitmq/.erlang.cookie /var/lib/rabbitmq/.temp.erlang.cookie;echo "$COOKIE" > /var/lib/rabbitmq/.erlang.cookie;chmod 400 /var/lib/rabbitmq/.erlang.cookie;chown rabbitmq /var/lib/rabbitmq/.erlang.cookie;rabbitmqctl stop_app; rabbitmqctl join_cluster rabbit@ubuntu-s-1vcpu-1gb-fra1-04;rabbitmqctl start_app"
echo "Brokers 4 - 5 clustered"

sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-06 "mv /var/lib/rabbitmq/.erlang.cookie /var/lib/rabbitmq/.temp.erlang.cookie;echo "$COOKIE" > /var/lib/rabbitmq/.erlang.cookie;chmod 400 /var/lib/rabbitmq/.erlang.cookie;chown rabbitmq /var/lib/rabbitmq/.erlang.cookie;rabbitmqctl stop_app; rabbitmqctl join_cluster rabbit@ubuntu-s-1vcpu-1gb-fra1-05;rabbitmqctl start_app"
echo "Brokers 5 - 6 clustered"

