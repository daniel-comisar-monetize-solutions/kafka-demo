#!/bin/bash

sbt -Dlog4j.configuration=file:config/log4j.properties "runMain org.apache.zookeeper.server.quorum.QuorumPeerMain config/zookeeper.properties" &

sleep 30

sbt -Dlog4j.configuration=file:config/log4j.properties "runMain kafka.Kafka config/server.properties" &

sbt -Dlog4j.configuration=file:config/log4j.properties "runMain kafka.Kafka config/server-1.properties" &

sbt -Dlog4j.configuration=file:config/log4j.properties "runMain kafka.Kafka config/server-2.properties" &
