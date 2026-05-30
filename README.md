# Credit Risk Evaluation Engine

A microservice-ready backend system for automated credit risk assessment and loan application processing. Built with Java 17, Spring Boot, and asynchronous event-driven architecture using RabbitMQ.

---

## Architecture Overview

```
Loan Application Request
        │
        ▼
  [REST API Layer]
  Spring Boot + JWT
        │
        ▼
  [Risk Engine Service]
  Scoring & Rule Evaluation
        │
        ├─── Low Risk ──────────► Auto-Approval (automated decision)
        │
        ├─── Medium Risk ───────► Queue → [Review Department Service]
        │                                  Human decision required
        └─── High Risk ─────────► Queue → [Senior Credit Officer Service]
                                           Manual evaluation required
        │
        ▼
  [RabbitMQ Event Bus]
  Async communication between services
        │
        ▼
  [PostgreSQL / MySQL]
  Application & decision persistence
```

---

## Features

- **Automated Risk Scoring** — Rule-based engine calculates credit score from applicant data and financial history
- **Tiered Decision Routing** — Low-risk applications are auto-approved; medium and high-risk cases are routed asynchronously to the appropriate department via RabbitMQ
- **JWT Authentication** — Secure endpoints with token-based auth and role-based access control
- **Async Messaging** — RabbitMQ decouples the risk engine from downstream decision services, ensuring fault tolerance and scalability
- **Dockerized Setup** — Full environment reproducible with a single `docker-compose up`
- **Audit Trail** — All decisions (automated and manual) are persisted with timestamps and rationale

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3, Spring Security |
| Messaging | RabbitMQ |
| Database | PostgreSQL, MySQL |
| Auth | JWT (JSON Web Token) |
| Build | Gradle |
| Containerization | Docker, Docker Compose |

---

## Getting Started

### Prerequisites

- Docker & Docker Compose
- Java 17+
- Gradle

### Run with Docker

```bash
git clone https://github.com/vusal466/credit-risk-evaluation-engine.git
cd credit-risk-evaluation-engine
docker-compose up --build
```

The application will start on `http://localhost:8080`

RabbitMQ Management UI: `http://localhost:15672` (guest / guest)

### Run Locally

```bash
./gradlew bootRun
```

---

## API Endpoints

### Authentication

```
POST /api/auth/login         - Obtain JWT token
POST /api/auth/register      - Register new user
```

### Loan Applications

```
POST /api/applications           - Submit new loan application
GET  /api/applications/{id}      - Get application status
GET  /api/applications           - List all applications (admin)
```

### Risk Decisions

```
GET  /api/decisions/{id}         - Get decision for an application
PUT  /api/decisions/{id}/review  - Manual review decision (department officer)
```

---

## Risk Scoring Logic

Applications are evaluated based on the following criteria:

| Factor | Weight |
|---|---|
| Credit score | 35% |
| Debt-to-income ratio | 25% |
| Employment history | 20% |
| Requested loan amount | 20% |

**Decision thresholds:**

| Score Range | Decision |
|---|---|
| 75 – 100 | Auto-approved |
| 50 – 74 | Routed to review department |
| 0 – 49 | Routed to senior credit officer |

---

## Message Queue Design

```
Exchange: credit.risk.exchange (topic)
    │
    ├── Routing Key: risk.medium  →  Queue: medium-risk-queue
    └── Routing Key: risk.high   →  Queue: high-risk-queue
```

Each queue is consumed by the corresponding department service. Messages include the full application payload and computed risk score.

---

## Project Structure

```
src/
└── main/java/com/creditrisk/
    ├── application/        # Loan application handling
    ├── risk/               # Scoring engine & rule evaluation
    ├── messaging/          # RabbitMQ producers & consumers
    ├── security/           # JWT config & filters
    └── config/             # App & queue configuration
```

---

## Future Improvements

- [ ] Integrate ML model for dynamic risk scoring (replacing static rule engine)
- [ ] Add Prometheus + Grafana monitoring
- [ ] Implement retry/dead-letter queue strategy for failed messages
- [ ] Kubernetes deployment manifests

---

## Author

**Vusal Cafarli** — Java Backend Developer
- GitHub: [@vusal466](https://github.com/vusal466)
- LinkedIn: [linkedin.com/in/vusal466](https://linkedin.com/in/vusal466)
- Email: jafarlivoo@gmail.com
