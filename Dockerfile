# Use the official openjdk image as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . /app

# Install dependencies and build the application
RUN ./gradlew build

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "build/libs/spring-lol-repository-0.0.1-SNAPSHOT.jar"]