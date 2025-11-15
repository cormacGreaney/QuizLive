# ===== Build stage =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy quiz-shared first
COPY ../quiz-shared /app/quiz-shared
WORKDIR /app/quiz-shared
RUN mvn clean install -DskipTests

# Copy RTS service
COPY ../rts-service /app/rts-service
WORKDIR /app/rts-service

# Pre-download dependencies
RUN mvn -q -DskipTests dependency:go-offline

# Build RTS
RUN mvn -q -DskipTests package

# ===== Run stage =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy built jar
COPY --from=build /app/rts-service/target/*.jar app.jar

# JVM options
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

EXPOSE 8083
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
