# TrustLend Security Model

## MVP

- Secure password/token handling
- Authentication and authorization
- Input validation
- Idempotent payment processing
- No secrets committed to Git
- No real KYC documents or production credentials in the repository
- Audit important state transitions

## Production

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

## Open-source rule

Use synthetic test data only. Never commit real Aadhaar, PAN, bank, payment credentials, KYC documents, or customer records.
