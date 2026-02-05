# AGENTS.md

Best practices for this project and for building a Java Spring REST API.

## Project Practices (Observed)
- Use Spring Boot 3.x with Java 21 as defined in `pom.xml`.
- Prefer constructor injection (no field injection).
- Centralize error handling with `@RestControllerAdvice` and consistent `ApiError` payloads.
- Use DTOs for request/response models and validate with `jakarta.validation` annotations.
- Use JWT-based auth with a dedicated filter and utility class.
- Secure endpoints via `SecurityFilterChain`, explicitly permitting public routes.
- Use Liquibase for schema migrations; avoid Hibernate auto-creating tables.
- Keep sensitive settings (JWT secret, DB credentials) in config files per profile.
- Document endpoints with OpenAPI/Swagger annotations.

## General Spring REST API Best Practices
### API Design
- Keep endpoints resource-oriented and consistent (nouns, plural resources, predictable paths).
- Use appropriate HTTP methods and status codes; return 201 on create, 204 on delete, etc.
- Version APIs at the base path (e.g., `/api/v1`).
- Return consistent error shapes and include request path and timestamp.
- Validate request payloads and fail fast with clear error messages.

### Security
- Store secrets outside of source control (env vars or a secrets manager).
- Use strong password hashing (BCrypt/Argon2) and never log passwords.
- Enforce authentication/authorization for protected routes with clear rules.
- Limit JWT token lifetime and rotate signing keys when needed.
- Add CORS and rate limiting where applicable.

### Persistence
- Use migrations (Liquibase/Flyway) for all schema changes; keep them immutable.
- Keep entity fields aligned with column types and constraints.
- Prefer transactions in service layer for multi-step writes.
- Avoid N+1 issues; use fetch joins or DTO projections when necessary.

### Code Structure
- Keep controllers thin; put business logic in services.
- Use repositories only for data access and queries.
- Separate DTOs from entities; do not expose entities directly in API responses.
- Favor immutability for DTOs where possible; avoid public setters if not needed.

### Reliability & Observability
- Add structured logging with useful context (request id, user id).
- Avoid logging secrets or PII.
- Add health endpoints and readiness checks.
- Add integration tests for auth flows and critical endpoints.

### Performance & Scalability
- Keep response payloads minimal.
- Use pagination for list endpoints.
- Set timeouts on outbound calls.
- Cache where appropriate; invalidate cache on writes.

### Tooling & Hygiene
- Keep dependencies up to date and pinned to known good versions.
- Use consistent formatting and static analysis (e.g., Checkstyle/SpotBugs).
- Document how to run the app and required env vars in `README`.
