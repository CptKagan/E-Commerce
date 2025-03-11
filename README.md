# E-Commerce

Spring Boot ile geliştirilen bir e-ticaret API’si. JWT tabanlı kimlik doğrulama, rol bazlı yetkilendirme, ürün yönetimi ve Stripe dummy payment entegrasyonu içerir.

## Kullanılan Teknolojiler 🚀

- **Spring Boot** – Backend geliştirme
- **Spring Security & JWT** – Kimlik doğrulama ve yetkilendirme
- **PostgreSQL** – Veritabanı yönetimi
- **Stripe API** – Dummy payment entegrasyonu
- **Swagger (SpringDoc OpenAPI)** – API dokümantasyonu
- **iText PDF** – Dinamik PDF oluşturma

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