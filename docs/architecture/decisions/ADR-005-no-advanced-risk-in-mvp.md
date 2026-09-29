# ADR-005: Advanced Risk Controls Are Not an MVP Feature

## Status

Accepted

## Context

TrustLend's initial use case is lending between people who already know each other.

## Decision

Do not build an advanced fraud/risk engine in the MVP.

## Keep in MVP

- Authentication
- Authorization
- Idempotent payment processing
- Payment reconciliation
- Immutable audit events
- Basic input and lifecycle validation

## Defer

- Behavioral risk scoring
- Device fingerprinting
- Network analysis
- ML fraud detection
- Advanced anomaly detection

## Rationale

These controls add infrastructure and operational complexity without proving the core product.
