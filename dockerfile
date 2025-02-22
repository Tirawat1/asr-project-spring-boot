FROM openjdk:17-jdk-slim

WORKDIR /app
COPY target/asr-backend.jar app.jar
# port
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]