# Stage 1: Build
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Gradle wrapper and build files first (for layer caching)
COPY gradlew gradlew
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies (cached unless build files change)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src/ src/

# Build the application (skip tests for faster builds)
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java ${SPRING_DATA_MONGODB_URI:+\"-Dspring.data.mongodb.uri=$SPRING_DATA_MONGODB_URI\"} ${CORS_ALLOWED_ORIGINS:+\"-Dapp.cors.allowed-origins=$CORS_ALLOWED_ORIGINS\"} -jar app.jar"]
# ENTRYPOINT ["java", "-jar", "app.jar"]