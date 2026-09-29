# ADR-003: Payment Provider Abstraction

## Status
Accepted

## Decision
Keep provider-specific payment behavior behind a `PaymentProvider` boundary. The loan and repayment domain must not depend on a specific payment network or provider.

The MVP uses `MockPaymentProvider`, which validates the provider reference and returns a normalized provider result. `PaymentService` then owns the domain-side persistence, allocation, idempotency, and audit behavior.

```text
Mobile / API
    |
    v
PaymentService
    |
    +---- PaymentProvider
             |
             +---- MockPaymentProvider (MVP)
             |
             +---- Production provider adapter (future)
```

## Why
- Provider-specific SDKs and webhooks should not leak into loan-domain logic.
- Idempotency remains a TrustLend concern even when a provider is involved.
- Payment allocation and contractual outstanding calculations remain owned by TrustLend.
- A future provider can be introduced without rewriting the repayment and settlement domain.

## Production boundary
A production adapter should translate provider callbacks/events into a normalized payment result and preserve the provider's immutable transaction reference. Webhook signature verification, replay protection, provider status mapping, and reconciliation retries belong at the adapter/integration boundary.

The MVP does not claim to process real UPI or bank payments.
