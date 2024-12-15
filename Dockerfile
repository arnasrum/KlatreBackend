FROM alpine/java:21-jdk

WORKDIR /backend

COPY ./src ./src
COPY ./gradle ./gradle
COPY build.gradle.kts .
COPY gradlew .
COPY gradlew.bat .
COPY settings.gradle.kts .

EXPOSE 8080

#CMD ["ls"]
CMD ["./gradlew", "bootRun"]