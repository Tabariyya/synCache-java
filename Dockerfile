FROM maven:3.9.9-eclipse-temurin-24 AS builder
WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn clean install

# Use JDK for runtime
FROM openjdk:24-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
COPY src/main/resources/lib/linux/arm/libjavaSynCache.so /usr/lib/libjavaSynCache.so
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
