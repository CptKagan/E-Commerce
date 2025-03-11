# Stage 1: Build the application using Maven
FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app
# Copy only pom.xml first to leverage Docker cache for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B
# Now copy the rest of the source code
COPY src ./src
# Build the project
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copy the built JAR from the previous stage
COPY --from=build /app/target/ecommerce-0.0.1-SNAPSHOT.jar app.jar
# Copy additional required directories from your repository (make sure these folders exist in your repo)
COPY fonts /app/fonts
COPY logos /app/logos
COPY Invoices /app/Invoices

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]