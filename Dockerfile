FROM openjdk:17 AS build
COPY src /project/src
COPY gradle /project/gradle
COPY build.gradle.kts gradle.properties settings.gradle.kts gradlew /project
WORKDIR /project
RUN ./gradlew uberJar --no-daemon

FROM openjdk:17
COPY --from=build /project/build/libs/*-uber.jar /app.jar
EXPOSE 8080:8080
CMD ["java", "-jar", "/app.jar"]
