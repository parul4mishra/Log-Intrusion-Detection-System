# Multi-stage build for optimized image size

# Stage 1: Build the application
FROM gradle:7.6-jdk11 AS build
WORKDIR /app

# Copy gradle files first for better caching
COPY gradle gradle
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle .
COPY settings.gradle .

#Make gradlew executable
RUN chmod +x ./gradlew

#Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src src

# Build the application
RUN ./gradlew bootJar --no-daemon

# Stage 2: Create the runtime image
FROM eclipse-temurin:11-jre
WORKDIR /app

# Create a non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring

# Copy the built jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Create logs directory for mounted volume
RUN mkdir -p /logs && chown -R spring:spring /app /logs

# Switch to non-root user
USER spring

# Expose the application port
EXPOSE 8080


# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
