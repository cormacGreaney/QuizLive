# ===== Build stage =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -e -DskipTests package

# ===== Run stage =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Point Spring to the container-only config we created in /config
ENV SPRING_CONFIG_ADDITIONAL_LOCATION=/config/

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
