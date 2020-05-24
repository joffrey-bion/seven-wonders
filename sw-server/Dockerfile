FROM openjdk:11-jre

EXPOSE 8090

ARG JAR_FILE=build/libs/sw-server.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Dserver.port=8090", "-jar", "/app.jar"]
