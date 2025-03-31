# Start from a Maven base image with JDK
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set the working directory
WORKDIR /app

# Copy pom and mvnw files first (to leverage Docker layer caching)
COPY pom.xml mvnw* ./
COPY .mvn .mvn

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src src

# Build the project
RUN ./mvnw package -DskipTests

# Use a slim JDK image for running the app
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
