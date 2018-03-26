#!/bin/bash

COOKIE=$( sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-01 "cat /var/lib/rabbitmq/.erlang.cookie" )
echo "Cookie is : $COOKIE"

sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-02 "mv /var/lib/rabbitmq/.erlang.cookie /var/lib/rabbitmq/.temp.erlang.cookie;printf "$COOKIE" > /var/lib/rabbitmq/.erlang.cookie;chmod 400 /var/lib/rabbitmq/.erlang.cookie;chown rabbitmq /var/lib/rabbitmq/.erlang.cookie;chgrp rabbitmq /var/lib/rabbitmq/.erlang.cookie;service rabbitmq-server restart;rabbitmqctl stop_app; rabbitmqctl join_cluster rabbit@ubuntu-s-1vcpu-1gb-fra1-01;rabbitmqctl start_app"
echo "Brokers 1 - 2 clustered"
sleep 3

sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-03 "mv /var/lib/rabbitmq/.erlang.cookie /var/lib/rabbitmq/.temp.erlang.cookie;printf "$COOKIE" > /var/lib/rabbitmq/.erlang.cookie;chmod 400 /var/lib/rabbitmq/.erlang.cookie;chown rabbitmq /var/lib/rabbitmq/.erlang.cookie;chgrp rabbitmq /var/lib/rabbitmq/.erlang.cookie;service rabbitmq-server restart;rabbitmqctl stop_app;rabbitmqctl join_cluster rabbit@ubuntu-s-1vcpu-1gb-fra1-01;rabbitmqctl start_app"
echo "Brokers 1 - 3 clustered"
sleep 3

sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-04 "mv /var/lib/rabbitmq/.erlang.cookie /var/lib/rabbitmq/.temp.erlang.cookie;printf "$COOKIE" > /var/lib/rabbitmq/.erlang.cookie;chmod 400 /var/lib/rabbitmq/.erlang.cookie;chown rabbitmq /var/lib/rabbitmq/.erlang.cookie;chgrp rabbitmq /var/lib/rabbitmq/.erlang.cookie;service rabbitmq-server restart;rabbitmqctl stop_app;rabbitmqctl join_cluster rabbit@ubuntu-s-1vcpu-1gb-fra1-01;rabbitmqctl start_app"
echo "Brokers 1 - 4 clustered"
sleep 3

sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-05 "mv /var/lib/rabbitmq/.erlang.cookie /var/lib/rabbitmq/.temp.erlang.cookie;printf "$COOKIE" > /var/lib/rabbitmq/.erlang.cookie;chmod 400 /var/lib/rabbitmq/.erlang.cookie;chown rabbitmq /var/lib/rabbitmq/.erlang.cookie;chgrp rabbitmq /var/lib/rabbitmq/.erlang.cookie;service rabbitmq-server restart;rabbitmqctl stop_app;rabbitmqctl join_cluster rabbit@ubuntu-s-1vcpu-1gb-fra1-01;rabbitmqctl start_app"
echo "Brokers 1 - 5 clustered"
sleep 3

sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-06 "mv /var/lib/rabbitmq/.erlang.cookie /var/lib/rabbitmq/.temp.erlang.cookie;printf "$COOKIE" > /var/lib/rabbitmq/.erlang.cookie;chmod 400 /var/lib/rabbitmq/.erlang.cookie;chown rabbitmq /var/lib/rabbitmq/.erlang.cookie;chgrp rabbitmq /var/lib/rabbitmq/.erlang.cookie;service rabbitmq-server restart;rabbitmqctl stop_app;rabbitmqctl join_cluster rabbit@ubuntu-s-1vcpu-1gb-fra1-01;rabbitmqctl start_app"
echo "Brokers 1 - 6 clustered"
sleep 3
