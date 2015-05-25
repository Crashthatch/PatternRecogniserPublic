#!/bin/bash
set -x; #echo on

if [ "$(id -u)" != "0" ]; then
	echo "Sorry, you are not root."
	exit 1
fi

#resque-web -K
#killall resque-web

#Start services required (but don't bother to restart them if already running).
service apache2 start
service redis-server start
service mysql start

cd /vagrant
redisPassword=`cat config-vagrant.json | jq -r '.redisPassword'`
redisHost=`cat config-vagrant.json | jq -r '.redisHost'`
redisPort=`cat config-vagrant.json | jq -r '.redisPort'`
resque-web -r "redis://redis:$redisPassword@$redisHost:$redisPort"
