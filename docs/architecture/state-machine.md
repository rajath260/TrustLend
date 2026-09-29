# Loan State Machine

```text
DRAFT
  |
  v
PENDING_BORROWER_ACCEPTANCE
  |
  v
ACCEPTED
  |
  v
ACTIVE
  |
  +--> PARTIALLY_PAID
  |        |
  |        v
  +----> DUE
           |
           v
        OVERDUE
           |
           v
        SETTLED

Other terminal/exception states:
CANCELLED
DISPUTED
```

Transitions must be enforced in the domain layer rather than being arbitrary database status updates.
