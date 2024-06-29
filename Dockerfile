FROM openjdk:17-jdk

COPY build/libs/lol-0.0.1-SNAPSHOT.jar /app/application.jar

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "-Dspring.profiles.active=prod", "/app/application.jar"]