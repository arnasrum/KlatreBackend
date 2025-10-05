# Klatre Backend

A Spring Boot backend application for managing climbing-related data and sessions.

## Tech Stack

- **Java SDK**: 21
- **Kotlin**: 2.1
- **Framework**: Spring Boot
- **Data Access**: Spring Data JDBC
- **Web**: Spring MVC
- **API Documentation**: OpenAPI/Swagger
- **Build Tool**: Gradle (Kotlin DSL)
- **Containerization**: Docker

## Features

- RESTful API endpoints for climbing data management
- Aspect-Oriented Programming (AOP) support
- OpenAPI documentation
- CORS configuration
- Database integration
- Docker support for containerized deployment

## Environment Variables

The application requires the following environment variables to be configured:

| Variable | Description | Required |
|----------|-------------|----------|
| `JWT_SECRET` | Secret key for JWT token generation and validation | Yes |
| `GOOGLE_CLIENT_ID` | Google OAuth 2.0 client ID for authentication | Yes |
| `GOOGLE_CLIENT_SECRET` | Google OAuth 2.0 client secret for authentication | Yes |
| `APP_UPLOAD_DIR` | Directory path for storing uploaded files | Yes |

### Example Configuration

```bash
export JWT_SECRET="your-secure-jwt-secret-key"
export GOOGLE_CLIENT_ID="your-google-client-id.apps.googleusercontent.com"
export GOOGLE_CLIENT_SECRET="your-google-client-secret"
export APP_UPLOAD_DIR="/path/to/upload/directory"
```

## API Documentation

When running in development mode, the API documentation is available via Swagger UI:

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

