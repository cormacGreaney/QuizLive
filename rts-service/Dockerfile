# ===== Build stage =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the rts-service pom and quiz-shared pom
COPY rts-service/pom.xml ./pom.xml
COPY quiz-shared/pom.xml ./quiz-shared/pom.xml

# Copy the source code
COPY quiz-shared/src ./quiz-shared/src
COPY rts-service/src ./src

# Build quiz-shared first
RUN mvn -f quiz-shared/pom.xml -q -DskipTests install

# Build RTS
RUN mvn -q -DskipTests package

# ===== Run stage =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy built jar
COPY --from=build /app/target/*.jar app.jar

# JVM options
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

EXPOSE 8083
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]