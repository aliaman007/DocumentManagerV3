JK Tech Document Management and Q&A
Introduction
The JK Tech Document Management and Q&A application is a Spring Boot-based system for uploading, processing, and searching large PDF documents (up to 50MB). It supports JWT-based authentication, role-based authorization (USER, ADMIN), and a save-then-queue workflow using RabbitMQ for asynchronous document processing with Apache Tika. Users can register, log in, upload documents (ADMIN only), and search or filter documents (USER or ADMIN). The application uses PostgreSQL for persistent storage and Redis for caching.
Features

User Management: Register and log in users with roles (USER, ADMIN).
Document Upload: Upload PDFs with metadata (name, author, type).
Document Processing: Extract content and metadata using Apache Tika, queued via RabbitMQ.
Search and Filter: Query documents by content or metadata, with pagination.
Security: JWT-based authentication and role-based access control.
Scalability: Supports large PDFs (50MB) and asynchronous processing.

Prerequisites

Java: 17
Maven: 3.8+
Docker: Latest version (for PostgreSQL, RabbitMQ)
PostgreSQL: 13+ (via Docker)
RabbitMQ: 3.9+ (via Docker)
Redis: Optional, for caching (configured in application.yml)

Setup Instructions
1. Clone the Repository
git clone https://github.com/<your-username>/document-management.git
cd document-management

2. Configure Environment

Ensure Docker is running.
Update src/main/resources/application.yml if needed (e.g., change ports, credentials):spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/docmanagement
    username: postgres
    password: password
  rabbitmq:
    host: rabbitmq
    port: 5672
    username: guest
    password: guest
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 50MB
jwt:
  secret: 4eX9k2mP5vN7jQ8rT3wY6zA0bC9dF1gH



3. Build the Project
mvn clean install

4. Run with Docker Compose

Use the provided docker-compose.yml to start the application, PostgreSQL, and RabbitMQ:docker-compose up -d --build


Verify services:
Application: http://localhost:8080
RabbitMQ UI: http://localhost:15673 (username: guest, password: guest)
PostgreSQL: Port 5432 (username: postgres, password: password)



5. Access Swagger UI

Open http://localhost:8080/swagger-ui.html to explore and test endpoints interactively.

Endpoints
All endpoints are prefixed with /api/. Authentication requires a JWT token (except for /auth/login and /auth/register), obtained via /auth/login and passed in the Authorization header as Bearer <token>.
Authentication

POST /auth/register
Description: Register a new user with credentials and role.
Request Body:{
  "username": "newuser",
  "password": "newpass123",
  "role": "USER"
}


Roles: USER or ADMIN.
Response:
200: "User registered successfully"
400: "Username already exists", "Role must be USER or ADMIN", etc.




POST /auth/login
Description: Authenticate a user and obtain a JWT token.
Request Body:{
  "username": "newuser",
  "password": "newpass123"
}


Response:
200: JWT token (e.g., eyJhbGciOiJIUzI1NiJ9...)
401: "Invalid username or password"





Document Management

POST /documents/upload
Description: Upload a PDF document (up to 50MB) with metadata.
Role: ADMIN
Headers: Authorization: Bearer <token>, Content-Type: multipart/form-data
Request Body (form-data):
file: PDF file (e.g., sample.pdf).
author: Author name (e.g., "John Doe").


Response:
200:{
  "id": 1,
  "content": null,
  "metadata": {
    "name": "sample.pdf",
    "author": "John Doe",
    "type": "text"
  }
}


403: Forbidden (non-ADMIN user).




GET /documents/search
Description: Search documents by query, with pagination.
Role: USER or ADMIN
Headers: Authorization: Bearer <token>
Parameters:
query: Search term (e.g., test).
page: Page number (default: 0).
size: Page size (default: 10).


Example: /documents/search?query=test&page=0&size=10
Response:
200: Paginated results:{
  "content": [
    {
      "id": 1,
      "content": "Extracted content...",
      "metadata": {
        "name": "sample.pdf",
        "author": "John Doe",
        "type": "text"
      }
    }
  ],
  "pageable": {...},
  "totalPages": 1,
  "totalElements": 1,
  ...
}






GET /documents
Description: Filter documents by author or type, with pagination.
Role: USER or ADMIN
Headers: Authorization: Bearer <token>
Parameters:
author: Author name (optional, e.g., John Doe).
type: Document type (optional, e.g., text).
page: Page number (default: 0).
size: Page size (default: 10).


Example: /documents?author=John%20Doe&type=text&page=0&size=10
Response: Similar to /documents/search.



Project Structure

src/main/java/com/main/:
DocumentManager.java: Main application class.
model/: Document, User entities.
repository/: UserRepository.
security/: Authentication and JWT handling (AuthController, AuthService, JwtUtil, etc.).
service/: Document processing logic.
DocumentController.java: Document endpoints.


src/main/java/com/jktech/documentmanagement/:
repository/: DocumentRepository.
service/: Tika, RabbitMQ services.
model/: RabbitMQ configuration, consumer.


src/main/resources/:
application.yml: Configuration.


docker-compose.yml: Defines services.
pom.xml: Dependencies.

Dependencies

Spring Boot 3.2.5 (spring-boot-starter-web, spring-boot-starter-data-jpa, spring-boot-starter-security, etc.)
PostgreSQL (postgresql)
RabbitMQ (spring-boot-starter-amqp)
Apache Tika (tika-core, tika-parsers-standard-package)
JWT (jjwt:0.9.1)
JAXB (javax.xml.bind:jaxb-api, jakarta.xml.bind:jakarta.xml.bind-api)
Swagger (springdoc-openapi-starter-webmvc-ui)

Running Tests
mvn test

Troubleshooting

Logs: Check docker-compose logs app for errors.
Database: Verify PostgreSQL (psql -U postgres -d docmanagement).
RabbitMQ: Access http://localhost:15673 to monitor documentQueue.
Authentication: Use /api/auth/debug/token to inspect JWT claims.

Contributing
Fork the repository, create a branch, and submit a pull request with changes. Ensure tests pass and follow coding standards.
License
MIT License
