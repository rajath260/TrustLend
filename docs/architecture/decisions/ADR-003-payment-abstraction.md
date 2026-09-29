# ADR-003: Payment Provider Abstraction

## Status
Accepted

## Decision
Keep provider-specific payment logic behind a PaymentProvider boundary.

## Rationale
The MVP can use a mock provider while the core loan domain remains independent of a specific production payment provider.

```text
Loan / Repayment Domain
        |
        v
PaymentProvider
  +-- MockPaymentProvider
  +-- ProductionProvider (future)
```
