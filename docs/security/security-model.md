# TrustLend Security Model

## MVP

- Secure password/token handling
- Authentication and authorization
- Input validation
- Idempotent payment processing
- No secrets committed to Git
- No real KYC documents or production credentials in the repository
- Audit important state transitions
- Centralize loan participant authorization through `LoanAuthorizationService`
- Represent the acting user with `ActorIdentity`

## Current development identity boundary

The MVP currently accepts an actor UUID in the agreement-acceptance request because there is no identity provider configured in the open-source development environment.

This UUID is an **identity claim for development/testing, not authentication**. The backend still performs ownership/participant checks against the loan.

Production must replace the request-supplied actor source with an authenticated identity established by a trusted identity provider. Controllers and domain services should consume the authenticated actor identity rather than trusting arbitrary client-supplied user IDs.

Recommended production flow:

```text
Mobile
  |
  | OAuth/OIDC access token
  v
API / Application Gateway
  |
  v
Authenticated actor context
  |
  v
LoanAuthorizationService
  |
  v
Loan / Agreement / Payment domain
```

The production implementation should validate token signature, issuer, audience, expiry and relevant claims before constructing the actor identity.

## Production

- Managed identity provider / OIDC
- Azure Key Vault
- Workload Identity
- TLS
- Least-privilege RBAC
- Network Policies
- PII minimization and masking
- Encryption at rest/in transit
- Rate limiting
- Dependency/container security scanning
- Secure observability
- Audit access to sensitive records
- Token and session lifecycle controls

## Open-source rule

Use synthetic test data only. Never commit real Aadhaar, PAN, bank, payment credentials, KYC documents, access tokens, or customer records.

## Important boundary

Authentication answers **who is the actor**.

Authorization answers **whether that actor may perform the requested action**.

TrustLend must keep these concerns separate so that changing the identity provider does not require rewriting loan-domain authorization rules.
