#!/usr/bin/env bash

echo "================================================"
echo "================================================"
echo "Springboot DynamoDB Example Test Start."
echo "================================================"
echo "================================================"

docker-compose up -d
sleep 10;
cd ../../../..
./mvnw.cmd clean test
cd src/main/docker/test
docker-compose down