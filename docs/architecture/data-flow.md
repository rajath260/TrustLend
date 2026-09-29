# TrustLend Data Flow

## Loan creation

Lender → API → Loan domain → PostgreSQL → Loan created event

## Borrower acceptance

Borrower → API → Agreement validation → Loan state transition → Audit event

## Payment

Payment boundary → Payment record → Idempotency check → Reconciliation → Allocation

## Settlement

Reconciled payments → Outstanding calculation → Settlement eligibility → Settlement statement → Loan settled → Repayment record
