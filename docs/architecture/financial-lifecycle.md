# TrustLend Financial Lifecycle

This document describes the implemented MVP financial lifecycle. It intentionally separates contractual facts from future regulated integrations.

## Implemented MVP flow

Loan Creation → Immutable Agreement Snapshot → Borrower Acceptance → Repayment Schedule → Payment Reconciliation → Payment Allocation → Outstanding Recalculation → Settlement

## Agreement

Each loan gets an immutable agreement snapshot containing principal, APR, interest method, dates, agreement version, SHA-256 content hash, acceptance actor and acceptance timestamp.

The borrower must accept the current agreement version before a repayment schedule can be created.

## Interest

The MVP supports `NONE` (0% interest) and `SIMPLE` (simple interest on original principal).

Simple interest: Principal × APR × Months / 1200.

Interest is rounded to paise and any rounding remainder is assigned to the final installment. Production terms must be validated against the final legal/regulatory operating model.

## Payments

Payments require an idempotency key. The MVP records a reconciled payment through an abstract provider boundary. A duplicate idempotency key returns the original payment when the loan and amount match. Reusing the key for a different loan or amount is rejected.

## Payment allocation

The MVP allocation policy is: (1) outstanding scheduled interest, (2) outstanding principal, and (3) no implicit overpayment. An overpayment is rejected until an explicit refund/adjustment policy exists.

## Settlement

A loan can be settled only when total obligation equals principal plus scheduled interest, total paid equals allocated interest plus allocated principal, and outstanding equals zero.

Settlement creates a settlement record and immutable audit events.

## Factual repayment record

TrustLend does not calculate a trust score or creditworthiness score.

The repayment-record endpoint reports factual platform activity such as number of loans, settled loans, principal originated, principal repaid and principal outstanding.

Future credit reporting, if legally applicable, belongs behind a regulated-partner / credit-reporting adapter.

## Production gaps

- real KYC provider integration
- real UPI/payment-provider integration
- regulated lending/financial partner integration
- credit-information-company reporting
- production-grade ledger/accounting controls
- refund and payment-reversal workflows
- late-fee/charge policy
- schedule-level payment allocation and delinquency state updates
- Flyway-managed production migrations
- advanced fraud/abuse controls
- production notification providers
- full authentication/authorization and user identity lifecycle
- regulatory/legal approval of product structure and contract terms

The MVP is therefore a technical demonstration of the contractual repayment lifecycle, not a production lending operation.