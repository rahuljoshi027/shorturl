FROM gradle:7.4.2-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test

FROM openjdk:17-oracle
EXPOSE 8081
COPY --from=build /home/gradle/src/build/libs/redirectshorturl-0.0.1-SNAPSHOT.jar redirecturl.jar
ENTRYPOINT ["java", "-jar", "redirecturl.jar"]