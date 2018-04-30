#!/bin/bash

COOKIE=$( ssh mustafa@ubuntu-s-1vcpu-1gb-fra1-01 "sudo cat /var/lib/rabbitmq/.erlang.cookie" )
echo "Cookie is : $COOKIE"

ssh mustafa@ubuntu-s-1vcpu-1gb-fra1-04 "sudo sh -c 'mv /var/lib/rabbitmq/.erlang.cookie /var/lib/rabbitmq/.temp.erlang.cookie;printf "$COOKIE" > /var/lib/rabbitmq/.erlang.cookie;chmod 400 /var/lib/rabbitmq/.erlang.cookie;chown rabbitmq /var/lib/rabbitmq/.erlang.cookie;chgrp rabbitmq /var/lib/rabbitmq/.erlang.cookie;service rabbitmq-server restart;rabbitmqctl stop_app;rabbitmqctl join_cluster rabbit@ubuntu-s-1vcpu-1gb-fra1-04;rabbitmqctl start_app'"
echo "Brokers 4 - 3 clustered"
sleep 1

ssh mustafa@ubuntu-s-1vcpu-1gb-fra1-03 "sudo sh -c 'mv /var/lib/rabbitmq/.erlang.cookie /var/lib/rabbitmq/.temp.erlang.cookie;printf "$COOKIE" > /var/lib/rabbitmq/.erlang.cookie;chmod 400 /var/lib/rabbitmq/.erlang.cookie;chown rabbitmq /var/lib/rabbitmq/.erlang.cookie;chgrp rabbitmq /var/lib/rabbitmq/.erlang.cookie;service rabbitmq-server restart;rabbitmqctl stop_app;rabbitmqctl join_cluster rabbit@ubuntu-s-1vcpu-1gb-fra1-04;rabbitmqctl start_app'"
echo "Brokers 4 - 3 clustered"
sleep 1

ssh mustafa@ubuntu-s-1vcpu-1gb-fra1-02 "sudo sh -c 'mv /var/lib/rabbitmq/.erlang.cookie /var/lib/rabbitmq/.temp.erlang.cookie;printf "$COOKIE" > /var/lib/rabbitmq/.erlang.cookie;chmod 400 /var/lib/rabbitmq/.erlang.cookie;chown rabbitmq /var/lib/rabbitmq/.erlang.cookie;chgrp rabbitmq /var/lib/rabbitmq/.erlang.cookie;service rabbitmq-server restart;rabbitmqctl stop_app;rabbitmqctl join_cluster rabbit@ubuntu-s-1vcpu-1gb-fra1-04;rabbitmqctl start_app'"
echo "Brokers 3 - 2 clustered"
sleep 1

ssh mustafa@ubuntu-s-1vcpu-1gb-fra1-01 "sudo sh -c 'mv /var/lib/rabbitmq/.erlang.cookie /var/lib/rabbitmq/.temp.erlang.cookie;printf "$COOKIE" > /var/lib/rabbitmq/.erlang.cookie;chmod 400 /var/lib/rabbitmq/.erlang.cookie;chown rabbitmq /var/lib/rabbitmq/.erlang.cookie;chgrp rabbitmq /var/lib/rabbitmq/.erlang.cookie;service rabbitmq-server restart;rabbitmqctl stop_app;rabbitmqctl join_cluster rabbit@ubuntu-s-1vcpu-1gb-fra1-04;rabbitmqctl start_app'"
echo "Brokers 2 - 1 clustered"
sleep 1