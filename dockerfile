FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy wait-for-it.sh script (make sure it's in your project root)
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# Copy the JAR file
COPY target/ecommerce-0.0.1-SNAPSHOT.jar app.jar

# Copy fonts, logos, and Invoices directories
COPY fonts /app/fonts
COPY logos /app/logos
COPY Invoices /app/Invoices

EXPOSE 8080

# Update the entrypoint to wait for Redis before launching the app
ENTRYPOINT ["/wait-for-it.sh", "redis:6379", "--", "sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
