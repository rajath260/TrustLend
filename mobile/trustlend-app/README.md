# TrustLend Mobile App

React Native + TypeScript MVP shell.

## Current flow

- Home
- Create Loan
- Agreement creation through the backend
- Loan details
- Borrower agreement acceptance
- Backend API error handling

The current shell uses deterministic demo lender/borrower UUIDs. This is intentional for the MVP development flow; production authentication will replace them.

## API configuration

Set the Expo environment variable:

```text
EXPO_PUBLIC_API_URL=http://localhost:8080
```

For a physical device, replace `localhost` with a reachable development machine/API address.

## Current repayment flow

- Repayment schedule creation
- Installment-level outstanding/status display
- Mock payment recording
- Backend payment allocation
- Settlement generation
- Settlement statement

## Planned next screens

- Agreement Review
- Repayment Record

## Product boundary

TrustLend does not calculate a trust score or make a proprietary creditworthiness decision. It records factual repayment activity.
