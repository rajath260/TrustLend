# TrustLend Data Model

## Core entities

### User
- user_id
- role/context
- profile fields
- created_at
- updated_at

### Loan
- loan_id
- lender_id
- borrower_id
- principal
- APR
- interest_method
- start_date
- maturity_date
- status
- agreement_version
- product_policy_version
- created_at

### Loan Agreement
- agreement_id
- loan_id
- version
- terms_snapshot
- accepted_by_lender_at
- accepted_by_borrower_at
- status
- created_at

### Repayment Schedule
- schedule_id
- loan_id
- due_date
- principal_due
- interest_due
- total_due
- status

### Payment
- payment_id
- loan_id
- amount
- payment_date
- provider_reference
- idempotency_key
- status

### Payment Allocation
- allocation_id
- payment_id
- principal_amount
- interest_amount
- fee_amount

### Settlement
- settlement_id
- loan_id
- principal_total
- interest_total
- charges_total
- amount_paid
- amount_outstanding
- settlement_date
- status

### Audit Event
- event_id
- loan_id
- event_type
- actor
- timestamp
- metadata

## Financial rule

Money values are represented using integer minor units or precise decimal values. Floating point is not used as the source of truth.
