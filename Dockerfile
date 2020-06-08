FROM openjdk:11-jdk-slim

ENV LC_ALL C.UTF-8
ENV LANG C.UTF-8

# Install dependencies
RUN apt-get update && apt-get install -y net-tools \
  && apt-get clean \
  && rm -rf /var/lib/apt/lists/*

# Create the application direcotry
RUN set -ex && mkdir -p /app
WORKDIR /app

# Copy entrypoint.sh
COPY ./docker-entrypoint.sh /app/docker-entrypoint.sh

EXPOSE 80

