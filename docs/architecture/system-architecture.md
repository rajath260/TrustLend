# TrustLend System Architecture

## MVP

```text
┌──────────────────────────────┐
│ React Native Mobile App      │
└──────────────┬───────────────┘
               │ HTTPS
               ▼
┌──────────────────────────────┐
│ TrustLend API                │
│ Modular Spring Boot          │
│                              │
│ Identity                     │
│ Loans & Agreements           │
│ Repayment                    │
│ Payments                     │
│ Settlement                   │
│ Audit                        │
└──────────────┬───────────────┘
               │
               ▼
        ┌─────────────┐
        │ PostgreSQL  │
        └─────────────┘
               │
       Mock / adapters
       for KYC/payment
```

## Production direction

```text
                  Internet
                     |
                     v
            Azure App Gateway/WAF
                     |
                     v
                   AKS
       ┌────────────┼──────────────┐
       v            v              v
   Identity       Loan          Payment
       |            |              |
       +------------+--------------+
                    |
                 Repayment
                    |
                Settlement
                    |
             Ledger / Audit
                    |
       +------------+-------------+
       |            |             |
   PostgreSQL     Redis      Service Bus

External boundaries:
KYC Adapter
Payment Adapter
Credit Reporting Adapter
Notification Adapter
```

## Design principle

Start with modular boundaries in one deployable backend. Extract services later when operational or scaling needs justify the added complexity.
