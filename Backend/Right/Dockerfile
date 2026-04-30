# Use Java 17
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy jar file
COPY target/Right-0.0.1-SNAPSHOT.jar app.jar

# Run application
ENTRYPOINT ["java","-jar","/app/app.jar"]