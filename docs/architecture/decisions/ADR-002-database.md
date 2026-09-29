# ADR-002: PostgreSQL

## Status

Accepted

## Decision

Use PostgreSQL as the system-of-record database.

## Rationale

- Relational consistency fits loans, schedules, payments and settlement
- Transaction semantics are important
- Strong indexing/query support
- Good local-to-Azure migration path

## Consequence

Financial state and audit records must be modelled carefully; mutable summary fields should not be the sole source of truth.
