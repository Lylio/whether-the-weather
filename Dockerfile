# Build stage
FROM maven:3.9-eclipse-temurin-19 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:19-jre
WORKDIR /app

COPY --from=build /app/target/*-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]