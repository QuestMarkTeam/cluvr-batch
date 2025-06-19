# Use an official Gradle image to build the project with JDK 17
FROM gradle:8.4.0-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle bootJar

# Use OpenJDK 17 image to run the application
FROM openjdk:17
WORKDIR /app
COPY --from=builder /app/build/libs/cluvr-batch-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]