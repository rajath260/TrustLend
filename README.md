# TrustLend

TrustLend is an open-source project for formalizing trusted-person lending relationships.

> **Lend with clarity. Repay with accountability.**

## Product idea

TrustLend is not an easy-loan marketplace and does not try to replace banks, payment rails, or credit bureaus.

The core use case is a lender who has already decided to lend to someone they know. TrustLend adds structure around that relationship:

**Agreement → Acceptance → Payment → Repayment → Reconciliation → Settlement → Record**

## MVP

The initial MVP focuses on the core financial workflow:

- User authentication and basic profiles
- Loan creation
- Borrower acceptance/rejection
- Versioned digital loan agreement
- Interest-free and controlled interest-bearing terms
- Repayment schedule
- Payment abstraction with mock/provider adapter
- Payment reconciliation and idempotency
- Partial payments
- Outstanding balance calculation
- Settlement and settlement statement
- Immutable audit events
- Factual TrustLend repayment record
- Basic notifications

### Explicitly out of MVP

TrustLend does **not** initially build:

- A CIBIL-like score
- Proprietary creditworthiness scoring
- Advanced fraud/risk engine
- Device fingerprinting
- Network/circular-lending analytics
- ML fraud detection
- Direct credit-bureau integration

These are future production concerns, subject to the final legal, regulatory, and operating model.

## Repository structure

```text
trustlend/
├── docs/
│   ├── product/
│   ├── architecture/
│   ├── api/
│   ├── database/
│   ├── security/
│   └── compliance/
├── services/
│   └── trustlend-api/
├── mobile/
│   └── trustlend-app/
├── database/
│   ├── migrations/
│   └── seed/
├── infrastructure/
│   ├── terraform/
│   ├── kubernetes/
│   └── helm/
├── tests/
├── .github/
│   └── workflows/
├── docker-compose.yml
├── .env.example
├── CONTRIBUTING.md
├── LICENSE
└── README.md
```

## Architecture direction

MVP:
```text
React Native
    |
    v
TrustLend API (modular Spring Boot)
    |
    +-- Identity
    +-- Loans & Agreements
    +-- Repayment
    +-- Payments
    +-- Settlement
    +-- Audit
    |
    v
PostgreSQL
    |
    +-- Mock / adapter boundaries
```

Production direction:
```text
React Native
    |
Azure Application Gateway / WAF
    |
AKS
    +-- Identity
    +-- User/KYC
    +-- Loan
    +-- Repayment
    +-- Payment
    +-- Settlement/Ledger
    +-- Notification
    +-- Credit Adapter
    |
Azure PostgreSQL / Redis / Service Bus / Key Vault / Monitoring
```

## Financial integrity principles

- Never use floating-point values as the source of truth for money.
- Use integer minor units or precise decimal types.
- Use idempotency for payment processing.
- Derive settlement from reconciled payment events.
- Never allow users to edit financial history.
- Keep immutable audit events.
- Version agreements rather than silently overwriting accepted terms.

## Regulatory boundary

The repository documents product and engineering assumptions separately from legal conclusions.

Any real production lending, KYC, payment, credit-reporting, recovery, or data-processing model must be validated against the applicable Indian legal and regulatory framework and the responsibilities of any regulated partner.

**This repository is a software project and is not legal or financial advice.**

## Status

Current stage: **Product foundation / MVP implementation**

The project is intentionally being built in layers:

1. Product definition
2. Domain model
3. API contracts
4. Backend MVP
5. Mobile MVP
6. Tests and CI
7. Containerization
8. Azure/AKS production evolution
9. External integrations after validation
