#!/bin/bash

echo "Linux Tuning"
printf '*       soft    nofile  65535 \n*       hard    nofile  65535' | sudo tee /etc/security/limits.conf
# more ports for testing
sudo sysctl -w net.ipv4.ip_local_port_range="1025 65535"
# increase the maximum number of possible open file descriptors:
echo 300000 | sudo tee /proc/sys/fs/nr_open
echo 300000 | sudo tee /proc/sys/fs/file-max

sudo sh -c "printf 'UseLogin yes' >> /etc/ssh/sshd_config"

exit 0
