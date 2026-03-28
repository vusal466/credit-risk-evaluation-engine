FROM gradle:8-jdk21 AS build
WORKDIR /app
COPY build.gradle .
COPY settings.gradle .
RUN gradle dependencies --no-daemon
COPY src ./src
RUN gradle bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/app.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]