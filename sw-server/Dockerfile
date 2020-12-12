FROM openjdk:15

EXPOSE 80

# Heroku uses the PORT environment variable, it ignores EXPOSE.
# This also allows to specify a custom port when running the container even outside of Heroku.
ENV PORT 80

ARG JAR_FILE=build/libs/sw-server.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Dserver.port=$PORT", "-jar", "/app.jar"]
