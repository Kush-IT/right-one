# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Set the working directory where the pom.xml will be
WORKDIR /app/backend

# Copy the pom.xml to cache dependencies
COPY Backend/Right/pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code
COPY Backend/Right/src ./src

# Build the project
RUN mvn clean package -DskipTests

# Stage 2: Create the production image
FROM eclipse-temurin:17-jre-alpine

# Set up a secure non-root user
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app

# Copy the compiled JAR file from the build stage
COPY --from=build /app/backend/target/*.jar app.jar

# Give proper permissions
RUN chown -R spring:spring /app

USER spring:spring

EXPOSE 8080
ENV JAVA_OPTS=""

# Run the app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
