#!/bin/bash

bin/zookeeper-server-start.sh config/zookeeper.properties &

sleep 10

bin/kafka-server-start.sh config/server.properties &

bin/kafka-server-start.sh config/server-1.properties &

bin/kafka-server-start.sh config/server-2.properties &
