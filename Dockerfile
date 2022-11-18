# syntax=docker/dockerfile:1
# this file uses the latest release of the version 1 syntax

########################
# 1. stage : prepare jdk
########################

# a somewhat slim version of the offical maven images for java 11 
FROM maven:3.6.3-jdk-11-slim as maven

WORKDIR /mtls-worker

COPY pom.xml pom.xml
COPY src src
COPY run.sh run.sh

RUN chmod +x run.sh # just to be safe

# create jdk
RUN mvn clean compile assembly:single

########################
# 2. stage : execute jdk
########################

# a somewhat slim version of the offical openjdk images for java 11 
FROM openjdk:11-jre-slim

WORKDIR /mtls-worker/ready

COPY --from=maven /mtls-worker/target/mtls-worker-1.0-jar-with-dependencies.jar worker.jar
COPY --from=maven /mtls-worker/run.sh run.sh
COPY .env .env
COPY client_keystore.pkcs12 client_keystore.pkcs12
COPY ca.pem ca.pem

# add ca cert to jvm truststore
RUN keytool -importcert -cacerts -trustcacerts -alias ca -file ca.pem -storepass changeit -noprompt

# execute jdk using the run wrapper which setsenv variabels from .env
CMD ["./run.sh", "java -jar worker.jar"]
