#!/usr/bin/env bash

# img=$(mktemp)

# if [ -z "$img" ]; then
#     echo "Failed to make temp file"
#     exit 1
# fi

fqdn=40.85.160.98

rsync -avh --progress docker-compose.yml secrets "oscar@$fqdn:recycle-server/"

ssh -fNL localhost:2374:/var/run/docker.sock "oscar@$fqdn" &
ssh_pid=$1

sleep 1

export DOCKER_HOST=localhost:2374

./build.sh

ssh "oscar@$fqdn" "cd ~/recycle-server && pwd && docker-compose up -d"


