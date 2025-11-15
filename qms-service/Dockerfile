# ===== Build stage =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the pom files
COPY qms-service/pom.xml ./qms-service/pom.xml
COPY quiz-shared/pom.xml ./quiz-shared/pom.xml

# Copy source code
COPY quiz-shared/src ./quiz-shared/src
COPY qms-service/src ./qms-service/src

# Build quiz-shared first
RUN mvn -f quiz-shared/pom.xml clean install -DskipTests

# Build QMS
RUN mvn -f qms-service/pom.xml clean package -DskipTests

# ===== Run stage =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/qms-service/target/*.jar app.jar

# JVM options
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

EXPOSE 8082

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
