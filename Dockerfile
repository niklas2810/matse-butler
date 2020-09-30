FROM openjdk:14-jdk-slim
COPY ./jars/*-shaded.jar /app/application.jar
WORKDIR /app

ENTRYPOINT ["java", "-jar", "/app/application.jar"]