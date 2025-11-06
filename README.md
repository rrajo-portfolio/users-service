# Users Service

Manages portfolio users with validation rules, scheduled maintenance, and JWT-protected APIs backed by MySQL.

## Commands

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

## Environment

- `SPRING_DATASOURCE_URL`
- `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI`
