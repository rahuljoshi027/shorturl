FROM gradle:7.4.2-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test

FROM openjdk:17-jdk-alpine
EXPOSE 8080
ADD /build/libs/shorturl-0.0.1-SNAPSHOT.jar shorturl.jar
ENTRYPOINT ["java", "-jar", "shorturl.jar"]