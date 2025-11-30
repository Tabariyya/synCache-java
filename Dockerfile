FROM maven:3.9.9-eclipse-temurin-8 AS builder
RUN apt update && apt install -y gnupg ca-certificates mono-complete

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean install -P test

