# API (Thymeleaf UI is inside the jar)
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw.cmd ./
COPY src src
RUN mvn -B package -DskipTests -q

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=postgres
RUN mkdir -p uploads/profile-images uploads/recipe-images
COPY --from=build /app/target/KodoKeliasLasProject-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx384m", "-jar", "app.jar"]
