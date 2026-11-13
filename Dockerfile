# Multi-stage Dockerfile for Revyze Spring Boot Backend (Java 21)

# Stage 1: Build & Package
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy Maven descriptor first to leverage Docker layer caching for dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy application source code and compile jar
COPY src/ ./src/
RUN mvn clean package -DskipTests -B

# Stage 2: Minimal Production Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a dedicated non-root application user and group for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Create uploads storage directory with non-root ownership
RUN mkdir -p /app/uploads/materials && chown -R appuser:appgroup /app

# Copy compiled executable JAR from builder stage
COPY --from=builder --chown=appuser:appgroup /build/target/*.jar /app/app.jar

# Render automatically injects $PORT (defaults to 8080 if not supplied)
ENV PORT=8080
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

USER appuser

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT} -jar /app/app.jar"]
