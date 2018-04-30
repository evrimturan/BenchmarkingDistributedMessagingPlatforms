#!/bin/bashr

COOKIE=$( ssh evrimturan@ubuntu-s-1vcpu-1gb-fra1-01 "sudo cat /var/lib/rabbitmq/.erlang.cookie" )
echo "Cookie is : $COOKIE"

#sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-05 "mv /var/lib/rabbitmq/.erlang.cookie /var/lib/rabbitmq/.temp.erlang.cookie;printf "$COOKIE" > /var/lib/rabbitmq/.erlang.cookie;chmod 400 /var/lib/rabbitmq/.erlang.cookie;chown rabbitmq /var/lib/rabbitmq/.erlang.cookie;chgrp rabbitmq /var/lib/rabbitmq/.erlang.cookie;service rabbitmq-server restart;rabbitmqctl stop_app;rabbitmqctl join_cluster rabbit@ubuntu-s-1vcpu-1gb-fra1-06;rabbitmqctl start_app"
#echo "Brokers 5 - 6 clustered"
#sleep 1

#sshpass -p "$1" ssh root@ubuntu-s-1vcpu-1gb-fra1-04 "mv /var/lib/rabbitmq/.erlang.cookie /var/lib/rabbitmq/.temp.erlang.cookie;printf "$COOKIE" > /var/lib/rabbitmq/.erlang.cookie;chmod 400 /var/lib/rabbitmq/.erlang.cookie;chown rabbitmq /var/lib/rabbitmq/.erlang.cookie;chgrp rabbitmq /var/lib/rabbitmq/.erlang.cookie;service rabbitmq-server restart;rabbitmqctl stop_app;rabbitmqctl join_cluster rabbit@ubuntu-s-1vcpu-1gb-fra1-05;rabbitmqctl start_app"
#echo "Brokers 4 - 5 clustered"
#sleep 1

ssh evrimturan@ubuntu-s-1vcpu-1gb-fra1-03 "sudo mv /var/lib/rabbitmq/.erlang.cookie /var/lib/rabbitmq/.temp.erlang.cookie;sudo printf "$COOKIE" > /var/lib/rabbitmq/.erlang.cookie;sudo chmod 400 /var/lib/rabbitmq/.erlang.cookie;sudo chown rabbitmq /var/lib/rabbitmq/.erlang.cookie; sudo chgrp rabbitmq /var/lib/rabbitmq/.erlang.cookie;sudo service rabbitmq-server restart;sudo rabbitmqctl stop_app;rabbitmqctl join_cluster rabbit@ubuntu-s-1vcpu-1gb-fra1-04;sudo rabbitmqctl start_app"
echo "Brokers 4 - 3 clustered"
sleep 1

ssh evrimturan@ubuntu-s-1vcpu-1gb-fra1-02 "sudo mv /var/lib/rabbitmq/.erlang.cookie /var/lib/rabbitmq/.temp.erlang.cookie;sudo printf "$COOKIE" > /var/lib/rabbitmq/.erlang.cookie;sudo chmod 400 /var/lib/rabbitmq/.erlang.cookie;sudo chown rabbitmq /var/lib/rabbitmq/.erlang.cookie; sudo chgrp rabbitmq /var/lib/rabbitmq/.erlang.cookie;sudo service rabbitmq-server restart;sudo rabbitmqctl stop_app;rabbitmqctl join_cluster rabbit@ubuntu-s-1vcpu-1gb-fra1-04;sudo rabbitmqctl start_app"
echo "Brokers 3 - 2 clustered"
sleep 1

ssh evrimturan@ubuntu-s-1vcpu-1gb-fra1-01 "sudo mv /var/lib/rabbitmq/.erlang.cookie /var/lib/rabbitmq/.temp.erlang.cookie;sudo printf "$COOKIE" > /var/lib/rabbitmq/.erlang.cookie;sudo chmod 400 /var/lib/rabbitmq/.erlang.cookie;sudo chown rabbitmq /var/lib/rabbitmq/.erlang.cookie; sudo chgrp rabbitmq /var/lib/rabbitmq/.erlang.cookie;sudo service rabbitmq-server restart;sudo rabbitmqctl stop_app;rabbitmqctl join_cluster rabbit@ubuntu-s-1vcpu-1gb-fra1-04;sudo rabbitmqctl start_app"
echo "Brokers 2 - 1 clustered"
sleep 1