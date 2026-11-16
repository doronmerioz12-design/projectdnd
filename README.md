# Project DnD

![Project Logo](docs/logo.png)

A **Spring Boot** based backend for a simple Dungeons & Dragons (DnD) style game service. The application provides REST APIs for managing users, games, characters, and combat actions. It demonstrates clean architecture, layered design, and **global exception handling** that returns consistent JSON error responses.

---

## 📖 Table of Contents

- [About the Project](#about-the-project)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Error Handling](#error-handling)
- [Testing](#testing)
- [Build & Deployment](#build--deployment)
- [Contributing](#contributing)
- [License](#license)

---

## About the Project

`projectdnd` is a **RESTful service** that powers a tabletop‑style role‑playing game. It is intentionally lightweight to serve as a reference implementation for:

- Structured **exception handling** with `@RestControllerAdvice`.
- Validation of incoming payloads using Spring's `@Valid` and custom error responses.
- Clean separation of concerns (controllers, services, repositories, and models).
- Easy integration with front‑end clients (React, Angular, mobile apps, etc.).

---

## Key Features

- **User Management** – Register, login, and profile handling.
- **Game Sessions** – Create, join, and manage game sessions.
- **Character Creation** – Define characters with stats, equipment, and abilities.
- **Combat Engine** – Simple turn‑based combat with dice rolls.
- **Global Exception Handling** – All uncaught exceptions are transformed into a uniform JSON payload, including:
  - Generic `Exception` → HTTP 500.
  - `RuntimeException` (game‑logic errors) → HTTP 400 with a clear *Bad Request / Game Logic Rule* message.
  - Validation errors → HTTP 400 with detailed field‑error mapping.
- **Spring Data JPA** – Persistence backed by an in‑memory H2 database (configurable to MySQL/PostgreSQL).
- **Swagger UI** – Interactive API documentation at `/swagger-ui.html`.

---

## Tech Stack

| Category | Technology |
|----------|--------------|
| Language | Java 21 |
| Framework | Spring Boot 3.3 |
| Build Tool | Maven |
| Database | H2 (dev), PostgreSQL (prod) |
| API Docs | Springdoc OpenAPI (Swagger) |
| Testing | JUnit 5, Spring Test |

---

## Prerequisites

- **JDK 21** or newer.
- **Maven 3.9+**.
- (Optional) Docker for containerised deployment.

---

## Getting Started

```bash
# Clone the repository
git clone https://github.com/example/projectdnd.git
cd projectdnd

# Build the project
mvn clean install
```

---

## Running the Application

```bash
# Run with Maven (development profile)
mvn spring-boot:run
```

The service will start on `http://localhost:8080`. Swagger UI can be accessed at `http://localhost:8080/swagger-ui.html`.

---

## API Documentation

The project uses **Springdoc OpenAPI** to generate interactive documentation. All endpoints are described with request/response schemas, example payloads, and possible error responses.

---

## Error Handling

All errors are presented as a **JSON object** with the following shape:

```json
{
  "timestamp": "2026-10-26T14:30:00",
  "status": 400,
  "error": "Bad Request / Game Logic Rule",
  "message": "Not enough mana!",
  "path": "/api/game/attack"
}
```

- **`RuntimeException`** – Used for domain‑specific rule violations (e.g., insufficient resources). Returns **400** with a custom `error` field.
- **`Exception`** – Unexpected server errors. Returns **500**.
- **Validation failures** – Spring's `MethodArgumentNotValidException` is overridden to return **400** with a `fieldErrors` map detailing each offending field.

---

## Testing

```bash
# Run unit and integration tests
mvn test
```

The test suite includes controller tests, service layer unit tests, and repository integration tests against the H2 database.

### JUnit 5 Test Suite & Architecture

This project features a comprehensive automated test suite built with **JUnit 5 (Jupiter)**, **JUnit Platform Suite**, **Mockito**, and **Spring Boot Test (`MockMvc`)**:

- **`AllTestsSuite`** – A JUnit Platform `@Suite` (`@SelectClasses`) that aggregates and runs `DiceServiceTest`, `GameServiceTest`, and `GameControllerTest` together as a unified test suite.
- **`CustomReportWatcher`** – A custom JUnit 5 extension implementing `TestWatcher` and `BeforeEachCallback` that numbers each test run sequentially and prints formatted, color-coded console reports detailing the **STATUS** (`PASSED`/`FAILED`), **INPUT**, **EXPECTED**, and **ACTUAL** results.
- **`DiceServiceTest`** – Uses JUnit 5 `@RepeatedTest(50)` to validate `d20` roll bounds (`1–20`) across 50 iterations, and `@ParameterizedTest` with `@ValueSource` to test custom polyhedral dice (`d4`, `d6`, `d8`, `d10`, `d12`, `d20`, and `d100` percentile rolls).
- **`GameServiceTest`** – Unit tests powered by `@ExtendWith(MockitoExtension.class)` (`@Mock`, `@InjectMocks`, `@Captor`) that verify spell casting mana deduction, `RuntimeException` handling when mana is insufficient, character stat restoration during a long rest, and database error propagation without calling external AI services.
- **`GameControllerTest`** – Web-layer slice tests using `@WebMvcTest(GameController.class)`, `MockMvc`, and `@WithMockUser` to test REST endpoints (`/api/game/cast-spell`, `/api/game/rest`), JSON response payloads, HTTP 400 error formatting via `GlobalExceptionHandler`, and HTTP 401/403 security enforcement for unauthenticated requests.

---

## Build & Deployment

### Docker

```Dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/projectdnd-*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
```

```bash
# Build the Docker image
Docker build -t projectdnd:latest .
# Run the container
Docker run -p 8080:8080 projectdnd:latest
```

### Kubernetes (example)

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: projectdnd
spec:
  replicas: 2
  selector:
    matchLabels:
      app: projectdnd
  template:
    metadata:
      labels:
        app: projectdnd
    spec:
      containers:
        - name: projectdnd
          image: projectdnd:latest
          ports:
            - containerPort: 8080
```

---

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/awesome-feature`).
3. Write tests for any new functionality.
4. Submit a pull request with a clear description of the changes.

---

## License

This project is licensed under the **MIT License** – see the [LICENSE](LICENSE) file for details.

---

*Happy coding!*

**Author:** doron merioz
