# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
# Using JRE instead of JDK saves memory on Render's free tier
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Using a wildcard safely grabs the jar regardless of what pom.xml names it
COPY --from=build /app/target/*.jar app.jar

# IMPORTANT: Ensure your application.properties matches this port
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]