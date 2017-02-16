#!/usr/bin/env bash

set -e

# Get this script directory (to find yml from any directory)
export DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Stop
docker-compose -f $DIR/docker-compose.yml stop

docker-compose -f $DIR/docker-compose.yml up -d vehicle-service customer-service rental-service