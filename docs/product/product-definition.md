# TrustLend Product Definition

## Vision

TrustLend formalizes lending relationships that already exist between people who know each other.

It does not encourage borrowing from friends instead of banks or regulated lenders. It provides a structured digital layer around an already-decided lending relationship.

## Core value

Today an informal loan may be represented by:

- a UPI or bank transfer
- a WhatsApp conversation
- a verbal repayment promise

TrustLend brings these into one lifecycle:

**Agreement → Acceptance → Payment → Repayment → Reconciliation → Settlement → History**

## Product boundary

### TrustLend owns

- Agreement creation and versioning
- Lender and borrower acceptance
- Repayment schedules
- Payment tracking
- Reconciliation
- Outstanding calculation
- Settlement
- Settlement statement
- Audit trail
- Platform-specific repayment history

### TrustLend does not recreate

- Banking infrastructure
- UPI rails
- KYC infrastructure
- Credit bureaus
- Credit scores
- Independent creditworthiness decisions
- Regulated lending infrastructure without the required operating model

## CIBIL / credit ecosystem position

TrustLend does not create another credit score.

A TrustLend repayment record should describe factual TrustLend activity, for example:

- 5 TrustLend agreements completed
- ₹75,000 principal repaid
- ₹0 currently outstanding
- 18/18 scheduled repayments completed

It should not claim:

- “This person is trustworthy.”
- “This person will repay.”
- “Trust score = 92/100.”

Any applicable credit reporting belongs behind an explicit adapter boundary and must follow the final eligible/regulatory reporting process.

## Core user journey

1. Lender creates loan.
2. Borrower receives the proposed agreement.
3. Borrower reviews terms.
4. Borrower accepts or rejects.
5. Disbursement/payment is recorded through the payment boundary.
6. Repayment schedule becomes active.
7. Payments are recorded and reconciled.
8. Outstanding amount is recalculated.
9. Loan is settled at zero contractual outstanding.
10. Settlement statement and factual history are generated.

## Product modes

### Interest-free

APR = 0%.

Borrower repays principal according to the agreed schedule.

### Interest-bearing

The product may allow a lender-selected rate within a configurable product policy.

The policy is a product control and must not be presented as an RBI-mandated cap unless the applicable rules explicitly establish that requirement.

The agreement must clearly state the rate, calculation method, schedule, and total payable before borrower acceptance.

## Settlement

Settlement is a first-class domain.

**Total Outstanding = Principal Outstanding + Interest Outstanding + Permitted Charges − Valid Allocated Payments**

A loan becomes settled only after authoritative payment reconciliation and allocation establish zero contractual outstanding.

## Product principle

TrustLend does not decide whether two people should trust each other.

The lender has already made that decision.

TrustLend makes the resulting financial agreement clearer, trackable, reconcilable, and correctly settled.
