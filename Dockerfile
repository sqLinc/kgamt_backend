FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY src src
COPY build.gradle.kts build.gradle.kts
COPY settings.gradle.kts settings.gradle.kts
RUN gradle bootJar --no-daemon

FROM amazoncorretto:21
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]