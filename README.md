# Credit Risk Evaluation Engine

Kredit müraciətlərinin (LOW, MEDIUM, HIGH, CRITICAL səviyyələrə görə) avtomatik və asinxron qaydada qiymətləndirilməsi üçün nəzərdə tutulmuş Spring Boot əsaslı mikroservis sistemidir.

## 🚀 Texnologiya Yığını
- **Backend:** Java 21, Spring Boot
- **Verilənlər Bazası:** PostgreSQL
- **Mesajlaşma (Asinxron xidmət):** RabbitMQ
- **Konteynerləşdirmə:** Docker, Docker Compose

## 📦 Proqramı necə işə salmalı?
Aşağıdakı tək sətirlik komanda ilə bütün arxitekturanı yükləyə və başlatmaq olar:
```bash
docker-compose up --build -d
```

## 📖 API Test (Swagger UI)
Tətbiqi işə vurduqdan sonra bütün Endpointləri birbaşa bu linkdən sınaqdan keçirə bilərsiniz:
👉 `http://localhost:8081/swagger-ui/index.html`
