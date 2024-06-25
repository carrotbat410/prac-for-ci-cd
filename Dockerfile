# Use the official openjdk image as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle

# Copy the source code
COPY src src

# Install dependencies and build the application
#RUN ./gradlew build
RUN ./gradlew bootJar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "build/libs/spring-lol-repository-0.0.1-SNAPSHOT.jar"]