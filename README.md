# Credit Risk Evaluation Engine

A Spring Boot-based microservice designed for asynchronous loan application processing and automated credit risk evaluation. The system evaluates loan applications based on predefined risk rules and updates their statuses via message brokers.

## 🚀 Technologies Used
- **Backend:** Java 21, Spring Boot 3.2.5
- **Database:** PostgreSQL 15, Spring Data JPA, Hibernate
- **Messaging:** RabbitMQ (Asynchronous Event-Driven Architecture)
- **Containerization:** Docker, Docker Compose
- **API Documentation:** Swagger / OpenAPI
- **Utilities:** Lombok, Gradle

## 🏗️ Architecture & Flow
1. **Loan Submission (`/api/loans`)**: Client submits a loan application. Status is initially set to `PENDING`.
2. **Evaluation Submission (`/api/loans/{id}/submit`)**: The loan is submitted to the evaluation queue (`risk.exchange` -> `risk.queue`).
3. **Risk Analysis**: The `RiskEvaluationService` listens to the message, applies predefined credit rules (based on credit score, DTI ratio, employment type, etc.), determines a `RiskLevel` (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`), and issues a decision (`APPROVE`, `REJECT`, `MANUAL_REVIEW`).
4. **Result Callback**: The result payload is published back via RabbitMQ.
5. **Status Update**: The `RiskEvaluationResultListener` parses the JSON result and updates the database with the final outcome.

## 🔧 Prerequisites
To run the system locally, you need:
- [Docker](https://www.docker.com/) and Docker Compose
- *Optionally:* Java 21 and Gradle (if you want to run it outside Docker)

## 📦 Running the Application

The entire infrastructure (PostgreSQL, RabbitMQ, and the Spring Boot App) is containerized and can be started with a single command:

```bash
# Start all services in detached mode
docker-compose up --build -d
```

To stop the services:
```bash
docker-compose down
```

## 📖 API Documentation (Swagger UI)
Once the application is running, you can access the OpenAPI documentation and interact with the endpoints directly via the Swagger UI:

- **Swagger UI:** `http://localhost:8081/swagger-ui/index.html`

### Key Endpoints
- **`POST /api/loans`** - Create a new loan application.
- **`POST /api/loans/{id}/submit`** - Dispatch a loan to the messaging queue for risk evaluation.
- **`GET /api/loans/{id}`** - Retrieve the details and current status of a loan.
- **`PUT /api/loans/{id}`** - Update loan information.
- **`GET /api/risk/level/{level}`** - Fetch all evaluations for a specific risk level (e.g., `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`).

## ⚙️ Environment Variables
The application uses the following key environment variables (configurable via `.env` or `application.yml`):
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`
- `SPRING_AMQP_DESERIALIZATION_TRUST_ALL` - (Set to `true` to allow Jackson JSON mapping from the message block).
