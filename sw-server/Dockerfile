FROM openjdk:11-jre

EXPOSE 80

ARG JAR_FILE=build/libs/sw-server.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Dserver.port=80", "-jar", "/app.jar"]
