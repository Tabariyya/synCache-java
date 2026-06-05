FROM maven:3.9.9-eclipse-temurin-8 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

ARG BROKER_TOKEN
ENV BROKER_TOKEN=${BROKER_TOKEN}

RUN mvn clean install -DskipTests
RUN mvn clean test

