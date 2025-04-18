# E-Commerce

Spring Boot ile geliştirilen bir e-ticaret API’si. JWT tabanlı kimlik doğrulama, rol bazlı yetkilendirme, ürün yönetimi ve Stripe dummy payment entegrasyonu içerir.

## Kullanılan Teknolojiler 🚀

- **Spring Boot** – Backend geliştirme
- **Spring Security & JWT** – Kimlik doğrulama ve yetkilendirme
- **PostgreSQL** – Veritabanı yönetimi
- **Stripe API** – Dummy payment entegrasyonu
- **Swagger (SpringDoc OpenAPI)** – API dokümantasyonu
- **iText PDF** – Dinamik PDF oluşturma
- **Docker** – Containerization
- **Redis** - Caching

## Docker ile Çalıştırma
### Yöntem 1

- Proje kök dizinine aşağıdaki docker-compose.yml dosyasını ekleyin:
```bash
version: '3.8'

services:
  postgres:
    image: postgres:13
    container_name: ecommerce-postgres
    environment:
      POSTGRES_DB: ecommerce
      POSTGRES_USER: your_username
      POSTGRES_PASSWORD: your_password
    ports:
      - "5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 5s
      timeout: 5s
      retries: 5
    networks:
      - ecommerce-network

    redis:
    image: redis:latest
    container_name: ecommerce-redis
    restart: always
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 5s
      timeout: 5s
      retries: 5
    networks:
      - ecommerce-network

  ecommerce-app:
    build: .
    container_name: ecommerce-app
    environment:
      DB_URL_ECOMMERCE: jdbc:postgresql://postgres:5432/ecommerce
      DB_USERNAME: your_username
      DB_PASSWORD: your_password
      STRIPE_SECRET_KEY: your_stripe_secret_key
      ECOMMERCE_JWT_SECRET: your_jwt_secret
      ECOMMERCE_EMAIL: your_email@example.com
      ECOMMERCE_EMAIL_PASSWORD: your_email_password
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATA_REDIS_PORT: 6379
    ports:
      - "8080:8080"
    depends_on:
      redis:
        condition: service_healthy
      postgres:
        condition: service_healthy
    networks:
      - ecommerce-network

networks:
  ecommerce-network:
```

- Projeyi çalıştırmak için terminalde:
```bash
docker-compose up --build
```

### Yöntem 2 (Manuel Docker Komutları)

- Docker network oluşturun:
```bash
docker network create ecommerce-network
```

- PostgreSQL container'ını başlatın:
```bash
docker run -d \
  --name ecommerce-postgres \
  --network ecommerce-network \
  -e POSTGRES_DB=ecommerce \
  -e POSTGRES_USER=your_username \
  -e POSTGRES_PASSWORD=your_password \
  -p 5432:5432 \
  postgres:13
```

- Spring Boot uygulaması için Docker image oluşturun
```bash
docker build -t ecommerce-app .
```

- Container'ı çalıştırın
```bash
docker run -d \
  --name ecommerce-app \
  --network ecommerce-network \
  -p 8080:8080 \
  -e DB_URL_ECOMMERCE=jdbc:postgresql://ecommerce-postgres:5432/ecommerce \
  -e DB_USERNAME=your_username \
  -e DB_PASSWORD=your_password \
  -e STRIPE_SECRET_KEY=your_stripe_secret_key \
  -e ECOMMERCE_JWT_SECRET=your_jwt_secret \
  -e ECOMMERCE_EMAIL=your_email@example.com \
  -e ECOMMERCE_EMAIL_PASSWORD=your_email_password \
  e-commerce-app
```

## Kurulum 🛠️
### Projeyi klonlayın:
```bash
git clone https://github.com/CptKagan/E-Commerce.git
cd E-Commerce
```

### Gerekli Bağımlılıkları Yükleyin
```bash
maven clean install
```

### Veritabanı Ayarları

- **application.properties** dosyanızda PostgreSQL bilgilerinizi güncelleyin.

### Projeyi Çalıştırın
```bash
mvn spring-boot:run
```

## API Kullanımı

Tüm API endpointleri için Swagger dökümantasyonunu kullanabilirsiniz:
```bash
http://localhost:8080/swagger-ui.html
```