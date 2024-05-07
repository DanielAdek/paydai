# Use the official maven image as a base image
FROM maven:3.8.5-openjdk-17 as build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven executable to the container's path
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Use the official OpenJDK image as a base image
FROM openjdk:17.0.1-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the build stage to the current directory
COPY --from=build /app/target/api-0.0-1-SNAPSHOT.jar paydai-api.jar

# Expose the port that your application will run on
EXPOSE 8080

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "paydai-api.jar"]
