# Zairo – Authentication & User Management Service

A Spring Boot **User Management System** built as a skill-assessment task, providing JWT-based authentication with role-based access control (RBAC) for three roles: **ADMIN**, **MANAGER**, and **USER**. It covers entity relationships (User ↔ Role ↔ Task), CRUD user management, task assignment, and a self-service profile/task view for regular users, backed by an H2 database for local testing.

## Pre-Seeded Admin Credentials

On first startup the app seeds an admin account you can log in with immediately:

| Field | Value |
|-------|-------|
| **Email (ID)** | `admin@test.com` |
| **Password** | `Admin@123` |

Use these against `POST /api/auth/login` to get a JWT with the `ADMIN` role. See [Default Seed Data](#default-seed-data) for details.

## Tech Stack

- **Java / Spring Boot**
- **Spring Security** – stateless authentication via a custom JWT filter
- **JJWT (`io.jsonwebtoken`)** – JWT generation and parsing
- **Spring Data JPA** – persistence layer
- **Lombok** – boilerplate reduction (`@Data`, `@RequiredArgsConstructor`, etc.)
- **springdoc-openapi / Swagger** – interactive API docs with bearer-token auth support
- **BCrypt** – password hashing (strength 12)

## Architecture

The codebase follows a layered / lightly domain-driven structure:

```
com.example.zairo
├── authentication
│   ├── api/                     # REST controllers
│   ├── application/
│   │   ├── dto/                 # Request/response DTOs
│   │   └── service/              # Business logic
│   ├── domain/
│   │   ├── exception/            # Custom exceptions + global handler
│   │   └── model/                 # JPA entities (User, Role, Task, Roles enum)
│   └── infrastructure/
│       ├── config/                # Data seeding (DataInitializer)
│       ├── repository/            # Spring Data repositories
│       └── security/              # SecurityConfig, JwtFilter, JwtUtil
└── config
    └── SwaggerConfig.java         # OpenAPI/Swagger bean configuration
```

## Roles & Permissions

| Role      | Description                                              |
|-----------|-----------------------------------------------------------|
| `ADMIN`   | Full user management: create, list, update, delete users; assign roles |
| `MANAGER` | View all users and their tasks; assign tasks to users with the `USER` role |
| `USER`    | View own profile and own assigned tasks |

Roles are stored in the database and attached to users via a many-to-many `user_roles` join table. Authorities are exposed to Spring Security as `ROLE_<NAME>` (e.g. `ROLE_ADMIN`).

## Authentication Flow

1. Client logs in via `POST /api/auth/login` with email/password.
2. `AuthService` authenticates the credentials via Spring's `AuthenticationManager`.
3. On success, `JwtUtil` issues a signed JWT (HS256) containing the user's ID (subject) and role claims.
4. Subsequent requests include the token as `Authorization: Bearer <token>`.
5. `JwtFilter` validates the token on every request and populates the `SecurityContext` with the user's ID and granted authorities.
6. Endpoint-level access is enforced with `@PreAuthorize("hasRole('...')")` on controllers.

## API Endpoints

### Auth (public)
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/login` | Authenticate and receive a JWT |

### Admin (`ROLE_ADMIN`)
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/admin/users` | Create a new user with one or more roles |
| GET | `/api/admin/users` | List all users |
| PUT | `/api/admin/users/{id}` | Update a user's name/email |
| DELETE | `/api/admin/users/{id}` | Delete a user |
| PUT | `/api/admin/users/{id}/roles?roleName=...` | Assign an additional role to a user |

### Manager (`ROLE_MANAGER`)
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/manager/users` | List all users with their roles and tasks |
| POST | `/api/manager/tasks` | Assign a task to a user (must have `USER` role) |

### User (authenticated)
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/users/me` | Get the current user's profile |
| GET | `/api/users/me/tasks` | Get the current user's assigned tasks |

## Error Handling

A global exception handler (`GlobalExceptionHandler`) returns consistent JSON error bodies (`status`, `message`, `timestamp`) for:

- `404` – user not found
- `409` – duplicate email / database constraint violations
- `400` – validation errors (`@Valid` / constraint violations)
- `401` – bad credentials or missing/invalid/expired JWT
- `403` – insufficient permissions

## Configuration

The application is configured via `application.properties`. It runs on a **Neon PostgreSQL** database rather than the default H2, with Hibernate set to auto-update the schema:

```properties
# Application Config
spring.application.name=zairo
server.port=8085

# Neon PostgreSQL Database Configuration
spring.datasource.url=jdbc:postgresql://<YOUR_NEON_HOST>/neondb?sslmode=require
spring.datasource.username=neondb_owner
spring.datasource.password=<YOUR_NEON_PASSWORD>
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=true

# JWT Configuration
application.security.jwt.secret-key=<YOUR_256_BIT_BASE64_SECRET>
application.security.jwt.expiration=86400000

# Springdoc / Swagger UI Configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

Notes:
- `spring.datasource.url` / `username` / `password` — get these from your [Neon](https://neon.tech) project dashboard (connection string details).
- `application.security.jwt.secret-key` must be a valid **Base64-encoded** string of at least 256 bits, suitable for HMAC-SHA256 signing (e.g. generate one with `openssl rand -base64 32`).
- `application.security.jwt.expiration` is in milliseconds — `86400000` = 24 hours.
- `spring.jpa.hibernate.ddl-auto=update` will auto-create/update tables on startup; switch to `validate` or use migrations (e.g. Flyway/Liquibase) for production.
- Never commit real Neon credentials or JWT secrets — use environment variables or a `.env`/secrets manager and keep `application.properties` placeholders in version control.

## Default Seed Data

On startup, `DataInitializer` seeds the database (only if empty) with:

- Roles: `ADMIN`, `MANAGER`, `USER`
- A default admin account:
  - **Email:** `admin@test.com`
  - **Password:** `Admin@123`

> ⚠️ Change or remove these default credentials before deploying to any non-local environment.

## API Documentation

Swagger UI is available (unauthenticated) at:

```
/swagger-ui/**
/v3/api-docs/**
```

It is pre-configured with a `bearerAuth` HTTP/JWT security scheme, so you can authorize requests directly from the Swagger UI using a token obtained from `/api/auth/login`.

## Running Locally

1. Create a Neon PostgreSQL project and copy its host, username, and password into `application.properties` (see [Configuration](#configuration)).
2. Generate a Base64 JWT secret and set `application.security.jwt.secret-key`.
3. Build and run the Spring Boot application (it starts on port `8085` by default).
4. Log in with the seeded admin account (`admin@test.com` / `Admin@123`) to obtain a JWT.
5. Use the token to call admin/manager/user endpoints, or explore them via Swagger UI at `/swagger-ui.html`.

## Assessment Requirements Mapping

This project was built to satisfy the **Spring Boot Skill Assessment Task** brief. Mapping each requirement to its implementation:

| # | Requirement | Where it's implemented |
|---|-------------|--------------------------|
| 1 | Spring Boot app with Web, JPA, Security, JWT, H2/lightweight DB | Standard Spring Boot setup; `jjwt` for JWT; JPA entities + repositories; H2 (or any configured relational DB) |
| 2 | `User` entity (id, name, email, password, roles) and `Role` entity (id, name); many-to-many relation; hashed passwords | `User`/`Role` entities under `domain.model`; `@ManyToMany` via `user_roles` join table; `BCryptPasswordEncoder` (strength 12) in `SecurityConfig` |
| 3 | Admin APIs (CRUD users, assign roles) | `AdminController` / `AdminService` → `/api/admin/users/**` |
| 3 | Manager APIs (view users + tasks, assign tasks) | `ManagerController` / `ManagerService` → `/api/manager/**` |
| 3 | User APIs (own profile + own tasks) | `UserController` / `UserService` → `/api/users/me`, `/api/users/me/tasks` |
| 4 | JWT auth on all APIs, role-gated access | `JwtFilter` + `JwtUtil` for token issuing/validation; `@PreAuthorize("hasRole(...)")` on each controller; `SecurityConfig` wires the filter chain |
| 5 | Relational DB persistence, pre-populated sample data | Spring Data JPA repositories; `DataInitializer` seeds `ADMIN`/`MANAGER`/`USER` roles and a default admin user on startup |
| 6 | Error handling (duplicate users, invalid/expired JWT, unauthorized access) + validation (email format, password strength) | `GlobalExceptionHandler` (404/409/400/401/403 responses); Bean Validation annotations (`@Email`, `@NotBlank`, `@Pattern` for password strength) on request DTOs |

> Note: sample data currently seeds one default admin user via `DataInitializer`. Add additional seed users (e.g. one Manager and one User) there if the assessment expects multiple pre-populated accounts across all three roles.
