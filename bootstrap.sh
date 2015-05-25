#!/bin/bash
set -x; #echo on

#Ideally would use Ansible to do this stuff instead of a shell script. But the Ansible Controller-server can't be windows, so direct provisioning using vagrant doesn't work.
# Possible workaround here boots VM then runs ansible locally to complete the rest of the setup: https://groups.google.com/forum/#!topic/vagrant-up/3fNhoow7mTE

export DEBIAN_FRONTEND=noninteractive
apt-get update;
apt-get install -q -y htop git jq nodejs nodejs-legacy npm openjdk-7-jdk maven;

#Set the options that would normally be provided during the interactive installation of mysql / phpmyadmin.
sudo debconf-set-selections <<< 'mysql-server-5.5 mysql-server/root_password password 7VzYjyDMpEXHjs'
sudo debconf-set-selections <<< 'mysql-server-5.5 mysql-server/root_password_again password 7VzYjyDMpEXHjs'

sudo debconf-set-selections <<< 'phpmyadmin phpmyadmin/dbconfig-install boolean false'
sudo debconf-set-selections <<< 'phpmyadmin phpmyadmin/reconfigure-webserver multiselect apache2'

sudo debconf-set-selections <<< 'phpmyadmin phpmyadmin/app-password-confirm password 7VzYjyDMpEXHjs'
sudo debconf-set-selections <<< 'phpmyadmin phpmyadmin/mysql/admin-pass password 7VzYjyDMpEXHjs'
sudo debconf-set-selections <<< 'phpmyadmin phpmyadmin/password-confirm password 7VzYjyDMpEXHjs'
sudo debconf-set-selections <<< 'phpmyadmin phpmyadmin/setup-password password 7VzYjyDMpEXHjs'
sudo debconf-set-selections <<< 'phpmyadmin phpmyadmin/database-type select mysql'
sudo debconf-set-selections <<< 'phpmyadmin phpmyadmin/mysql/app-pass password 7VzYjyDMpEXHjs'

sudo debconf-set-selections <<< 'dbconfig-common dbconfig-common/mysql/app-pass password 7VzYjyDMpEXHjs'
sudo debconf-set-selections <<< 'dbconfig-common dbconfig-common/mysql/app-pass password'
sudo debconf-set-selections <<< 'dbconfig-common dbconfig-common/password-confirm password 7VzYjyDMpEXHjs'
sudo debconf-set-selections <<< 'dbconfig-common dbconfig-common/app-password-confirm password 7VzYjyDMpEXHjs'
sudo debconf-set-selections <<< 'dbconfig-common dbconfig-common/app-password-confirm password 7VzYjyDMpEXHjs'
sudo debconf-set-selections <<< 'dbconfig-common dbconfig-common/password-confirm password 7VzYjyDMpEXHjs'

cd /vagrant
apt-get install -q -y mysql-server;
mysqlUser=`cat config-vagrant.json | jq -r '.mysqlUser'`
mysqlPassword=`cat config-vagrant.json | jq -r '.mysqlPass'`
mysql -u root -p7VzYjyDMpEXHjs -e "CREATE USER '$mysqlUser'@'%' IDENTIFIED BY '$mysqlPassword'"
mysql -u root -p7VzYjyDMpEXHjs -e "GRANT ALL PRIVILEGES ON * . * TO '$mysqlUser'@'%';"
mysql -u root -p7VzYjyDMpEXHjs -e "CREATE DATABASE pattern_activedata"
mysql -u root -p7VzYjyDMpEXHjs -e "CREATE DATABASE pattern_inputdata"
mysql -u root -p7VzYjyDMpEXHjs -e "CREATE DATABASE pattern_testdata"
mysql -u root -p7VzYjyDMpEXHjs -e "CREATE DATABASE pattern_metamodel"

#Need apache and php5 for phpmyadmin.
apt-get install -q -y apache2;
apt-get install -q -y php5;
apt-get install -q -y phpmyadmin;


#Redis used by worker to accept jobs to work on.
apt-get install -q -y redis-server;
apt-get install -q -y redis-tools;
redisPassword=`cat config-vagrant.json | jq -r '.redisPassword'`
echo "requirepass $redisPassword" >> /etc/redis/redis.conf
service redis-server restart;

apt-get install -q -y graphviz;

cd /vagrant;
mvn install -DskipTests;

