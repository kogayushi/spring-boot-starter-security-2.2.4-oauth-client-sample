#!/bin/bash
#
# docker-entrypoint.sh
#
set -e

PROJ_DIR=/app
cd ${PROJ_DIR}

# setup hosts
netstat -nr | grep '^0\.0\.0\.0' | awk '{print $2" dockerhost"}' >> /etc/hosts

# Build application
./gradlew clean build

# Run the application.
java -jar ./build/libs/*.jar

