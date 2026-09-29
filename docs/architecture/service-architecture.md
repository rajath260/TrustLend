# TrustLend Service Boundaries

## Identity
Authentication, sessions, user roles.

## User/KYC
Profile, KYC state, consent, provider abstraction.

## Loan
Loan creation, terms, state machine, agreement versioning.

## Repayment
Schedule, due amounts, overdue calculation, payment allocation.

## Payment
Payment intent, provider abstraction, provider reference, idempotency, reconciliation.

## Settlement
Outstanding calculation, eligibility, settlement statement, closure.

## Audit/Ledger
Immutable business events and financial history.

## Notification
Due-date, acceptance, overdue, and settlement messages.

## Credit Adapter
A boundary for applicable credit-reporting integrations. It does not implement a proprietary credit score.
