#!/bin/bash


sudo -u vagrant -s <<EOF
set -x;
cd /vagrant
/vagrant/start.sh config-vagrant.json
EOF
