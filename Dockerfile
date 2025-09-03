FROM maven:3.9.9-eclipse-temurin-24 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install
