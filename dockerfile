FROM openjdk:17-jdk-slim

WORKDIR /app

# JAR dosyasını kopyala
COPY target/ecommerce-0.0.1-SNAPSHOT.jar app.jar

# Font ve logo klasörlerini kopyala
COPY fonts /app/fonts
COPY logos /app/logos
COPY Invoices /app/Invoices

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]