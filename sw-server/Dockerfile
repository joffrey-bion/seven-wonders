FROM openjdk:17

EXPOSE 80

# Heroku uses the PORT environment variable, it ignores EXPOSE.
# This also allows to specify a custom port when running the container even outside of Heroku.
ENV PORT 80

ARG JAR_FILE=build/libs/sw-server.jar
COPY ${JAR_FILE} app.jar

# We need sh to perform the substitution of $PORT
# We cannot use ENTRYPOINT because Heroku escapes the $ in ENTRYPOINT, so CMD it is
CMD ["sh", "-c", "java -Dserver.port=$PORT -jar /app.jar"]
