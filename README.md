# Users Service

## Purpose
Central authority for portfolio user records, demonstrating how identity-adjacent data can be modeled independently from Keycloak while still honoring enterprise governance requirements. It exists so other services can trust profile data, status flags, and validation rules without coupling to the authentication server.

## Technology Focus
- Spring Boot 3.2 with Data JPA, Validation, and Scheduler to manage CRUD flows plus background cleanup of inactive users.
- MySQL schema dedicated to user attributes, reinforcing database-per-service boundaries.
- OAuth2 Resource Server security to ensure every endpoint enforces JWT scopes issued by Keycloak.
- Cross-service readiness through clearly defined DTOs that other microservices consume via REST clients.
