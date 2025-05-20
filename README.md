📚 JK Tech Document Management & Q&A

Welcome to the JK Tech Document Management & Q&A application! This Spring Boot-powered system lets you upload, process, and search large PDF documents (up to 50MB) with ease. It features secure JWT-based authentication, role-based access (USER, ADMIN), and asynchronous document processing using RabbitMQ and Apache Tika. Whether you're managing documents or querying content, this app has you covered! 🚀
✨ Features

User Management: Register and log in users with USER or ADMIN roles.
Document Upload: Upload PDFs with metadata (name, author, type) for ADMIN users.
Smart Search: Query documents by content or metadata with pagination for USER or ADMIN.
Asynchronous Processing: Save-then-queue workflow with RabbitMQ and Tika for content extraction.
Secure Access: JWT-based authentication and role-based authorization.
Scalable Design: Handles large PDFs and integrates Redis for caching.

🛠️ Prerequisites
Before you start, ensure you have:

☕ Java 17
🛠️ Maven 3.8+
🐳 Docker (for PostgreSQL, RabbitMQ)
🗄️ PostgreSQL 13+ (via Docker)
📬 RabbitMQ 3.9+ (via Docker)
💾 Redis (optional, for caching)

🚀 Getting Started
Follow these steps to set up and run the project locally:

Clone the Repository:
git clone https://github.com/<your-username>/document-management.git
cd document-management


Configure Settings:

Verify src/main/resources/application.yml:spring:
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


Adjust ports or credentials if needed.


Build the Project:
mvn clean install


Run with Docker Compose:

Start services (application, PostgreSQL, RabbitMQ) using docker-compose.yml:docker-compose up -d --build


Check services:
🌐 Application: http://localhost:8080
📬 RabbitMQ UI: http://localhost:15673 (user: guest, pass: guest)
🗄️ PostgreSQL: Port 5432 (user: postgres, pass: password)




Explore APIs:

Open Swagger UI at http://localhost:8080/swagger-ui.html to test endpoints interactively.



📖 API Endpoints
All endpoints are prefixed with /api/. Authentication requires a JWT token (except /auth/login and /auth/register), obtained via /auth/login and passed as Authorization: Bearer <token>.



Endpoint
Method
Role
Description



/auth/register
POST
None
Register a new user with username, password, and role (USER or ADMIN).


/auth/login
POST
None
Authenticate a user and obtain a JWT token.


/documents/upload
POST
ADMIN
Upload a PDF (up to 50MB) with metadata.


/documents/search
GET
USER, ADMIN
Search documents by query with pagination.


/documents
GET
USER, ADMIN
Filter documents by author or type with pagination.


🔐 Authentication

POST /auth/register

Body:{
  "username": "newuser",
  "password": "newpass123",
  "role": "USER"
}


Response:
✅ 200: "User registered successfully"
❌ 400: "Username already exists", "Role must be USER or ADMIN"


Example:curl -X POST http://localhost:8080/api/auth/register \
-H "Content-Type: application/json" \
-d '{"username":"newuser","password":"newpass123","role":"USER"}'




POST /auth/login

Body:{
  "username": "newuser",
  "password": "newpass123"
}


Response:
✅ 200: JWT token (e.g., eyJhbGciOiJIUzI1NiJ9...)
❌ 401: "Invalid username or password"


Example:curl -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{"username":"newuser","password":"newpass123"}'





📂 Document Management

POST /documents/upload

Role: ADMIN
Headers: Authorization: Bearer <token>, Content-Type: multipart/form-data
Body (form-data):
file: PDF file (e.g., sample.pdf)
author: Author name (e.g., John Doe)


Response:
✅ 200:{
  "id": 1,
  "content": null,
  "metadata": {
    "name": "sample.pdf",
    "author": "John Doe",
    "type": "text"
  }
}


❌ 403: Forbidden (non-ADMIN)


Example:curl -X POST http://localhost:8080/api/documents/upload \
-H "Authorization: Bearer <token>" \
-F "file=@sample.pdf" \
-F "author=John Doe"




GET /documents/search

Role: USER or ADMIN
Headers: Authorization: Bearer <token>
Parameters:
query: Search term (e.g., test)
page: Page number (default: 0)
size: Page size (default: 10)


Response:
✅ 200:{
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
  "totalElements": 1
}




Example:curl -X GET "http://localhost:8080/api/documents/search?query=test&page=0&size=10" \
-H "Authorization: Bearer <token>"




GET /documents

Role: USER or ADMIN
Headers: Authorization: Bearer <token>
Parameters:
author: Author name (optional, e.g., John Doe)
type: Document type (optional, e.g., text)
page: Page number (default: 0)
size: Page size (default: 10)


Response: Similar to /documents/search
Example:curl -X GET "http://localhost:8080/api/documents?author=John%20Doe&type=text&page=0&size=10" \
-H "Authorization: Bearer <token>"





🧪 Running Tests
The project includes unit and integration tests using JUnit 5, Mockito, and Testcontainers.

Run Tests:
mvn clean test


Generate Coverage Report:

Uses JaCoCo (configured in pom.xml).

mvn jacoco:report


View report: target/site/jacoco/index.html



📂 Project Structure
document-management/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/
│   │   │   │   ├── main/
│   │   │   │   │   ├── DocumentManager.java
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── Document.java
│   │   │   │   │   │   ├── User.java
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   ├── security/
│   │   │   │   │   │   ├── AuthController.java
│   │   │   │   │   │   ├── AuthService.java
│   │   │   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   │   │   ├── JwtUtil.java
│   │   │   │   │   │   ├── PasswordEncoderConfig.java
│   │   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   ├── service/
│   │   │   │   │   │   ├── DocumentService.java
│   │   │   │   │   ├── DocumentController.java
│   │   │   │   ├── jktech/
│   │   │   │   │   ├── documentmanagement/
│   │   │   │   │   │   ├── repository/
│   │   │   │   │   │   │   ├── DocumentRepository.java
│   │   │   │   │   │   ├── service/
│   │   │   │   │   │   │   ├── TikaService.java
│   │   │   │   │   │   │   ├── RabbitMQSender.java
│   │   │   │   │   │   │   ├── DocumentRetryService.java
│   │   │   │   │   │   ├── model/
│   │   │   │   │   │   │   ├── RabbitMQConfig.java
│   │   │   │   │   │   │   ├── DocumentConsumer.java
│   │   ├── resources/
│   │   │   ├── application.yml
│   ├── test/
│   │   ├── java/
│   │   │   ├── com/
│   │   │   │   ├── main/
│   │   │   │   │   ├── DocumentManagerTests.java
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── DocumentTests.java
│   │   │   │   │   │   ├── UserTests.java
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   ├── UserRepositoryTests.java
│   │   │   │   │   ├── security/
│   │   │   │   │   │   ├── AuthControllerTests.java
│   │   │   │   │   │   ├── AuthServiceTests.java
│   │   │   │   │   │   ├── JwtAuthenticationFilterTests.java
│   │   │   │   │   │   ├── JwtUtilTests.java
│   │   │   │   │   │   ├── PasswordEncoderConfigTests.java
│   │   │   │   │   │   ├── SecurityConfigTests.java
│   │   │   │   │   ├── service/
│   │   │   │   │   │   ├── DocumentServiceTests.java
│   │   │   │   │   ├── DocumentControllerTests.java
│   │   │   │   ├── jktech/
│   │   │   │   │   ├── documentmanagement/
│   │   │   │   │   │   ├── repository/
│   │   │   │   │   │   │   ├── DocumentRepositoryTests.java
│   │   │   │   │   │   ├── service/
│   │   │   │   │   │   │   ├── TikaServiceTests.java
│   │   │   │   │   │   │   ├── RabbitMQSenderTests.java
│   │   │   │   │   │   │   ├── DocumentRetryServiceTests.java
│   │   │   │   │   │   ├── model/
│   │   │   │   │   │   │   ├── RabbitMQConfigTests.java
│   │   │   │   │   │   │   ├── DocumentConsumerTests.java
├── docker-compose.yml
├── pom.xml
├── README.md

🔧 Troubleshooting

Application Fails to Start:
Check logs: docker-compose logs app.
Verify PostgreSQL/RabbitMQ containers: docker ps.


Authentication Issues:
Use /api/auth/debug/token to inspect JWT claims.
Check users and user_roles tables in PostgreSQL.


Upload Errors:
Ensure ADMIN role for user (SELECT * FROM user_roles;).
Verify file size (<50MB) and Content-Type: multipart/form-data.


Search Issues:
Confirm documents exist: SELECT * FROM document;.


RabbitMQ:
Monitor documentQueue at http://localhost:15673.



🤝 Contributing

Fork the repository.
Create a branch: git checkout -b feature-name.
Commit changes: git commit -m "Add feature".
Push: git push origin feature-name.
Submit a pull request.

Ensure tests pass (mvn test) and follow coding standards.
📜 License
MIT License

⭐ Star this repo if you find it useful! Let us know your feedback or issues in the Issues section.
