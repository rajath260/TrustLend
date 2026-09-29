# TrustLend MVP Scope

## Goal

Prove one complete flow from a lender creating a loan to the loan becoming settled.

## Included

### Identity
- Registration/login
- Basic user profile
- Authentication
- Lender/borrower context

### Loan
- Create loan
- View loan
- Accept/reject
- Loan lifecycle state machine

### Agreement
- Structured terms
- Version number
- Accepted timestamp
- Immutable accepted version

### Repayment
- Schedule generation
- Due amounts
- Partial payments
- Overdue state
- Outstanding calculation

### Payment
- Mock/provider abstraction
- Provider reference
- Idempotency key
- Reconciliation state

### Settlement
- Settlement eligibility
- Settlement statement
- Final outstanding = zero
- Loan settled event

### Audit
- Immutable domain events
- Actor
- Timestamp
- Event metadata

### Repayment record
- Factual TrustLend history
- No trust score
- No creditworthiness claim

## Excluded

- Advanced fraud engine
- ML risk scoring
- Device fingerprinting
- Circular-lending graph analysis
- Direct CIC integration
- Real KYC provider
- Production UPI integration
- Automated collection/recovery decisions
- Multi-region disaster recovery

## MVP success criterion

A test user must be able to:

1. Create a ₹20,000 agreement.
2. Have another user accept it.
3. Record four ₹5,000 repayments.
4. Reconcile each payment exactly once.
5. Reach ₹0 outstanding.
6. Generate a settlement statement.
7. See the completed agreement in repayment history.
