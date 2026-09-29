# ADR-001: Backend — Java + Spring Boot

## Status

Accepted

## Context

TrustLend is a financial-domain application with explicit state transitions, transactional workflows, precise money handling, and strong testability requirements.

## Decision

Use Java + Spring Boot for the MVP backend.

## Rationale

- Strong typing
- Mature transaction support
- Clear domain modelling
- Broad enterprise ecosystem
- Good fit for an eventual regulated/enterprise integration environment

## Consequence

The MVP can remain a modular monolith while preserving boundaries for later service extraction.
