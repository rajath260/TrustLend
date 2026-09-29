# Database Migrations

TrustLend uses **Flyway** for the persistent PostgreSQL schema.

## Migration rules

- Migrations live under `services/trustlend-api/src/main/resources/db/migration/`.
- Production/dev PostgreSQL uses Flyway and Hibernate `ddl-auto: validate`.
- Test profile uses H2 with Hibernate `create-drop` and disables Flyway.
- Never edit an already-applied migration. Add a new versioned migration instead.
- Financial schema changes must preserve historical agreement, payment, allocation, audit, and settlement data.

## Current migrations

- `V1__initial_schema.sql` — initial TrustLend MVP financial schema.

## Production direction

Before production launch, migrations should be exercised against a PostgreSQL environment in CI/CD and included in the deployment promotion process. Backup/restore testing must accompany destructive or high-risk schema changes.
