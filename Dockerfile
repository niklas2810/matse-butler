FROM openjdk:14-jdk-slim
COPY ./app.jar /app/application.jar
WORKDIR /app

ENTRYPOINT ["java", "-jar", "/app/application.jar"]