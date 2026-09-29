CREATE TABLE loans (
    id UUID PRIMARY KEY,
    lender_id UUID NOT NULL,
    borrower_id UUID NOT NULL,
    principal NUMERIC(19,2) NOT NULL,
    apr NUMERIC(7,4) NOT NULL,
    interest_method VARCHAR(32) NOT NULL,
    start_date DATE NOT NULL,
    maturity_date DATE NOT NULL,
    status VARCHAR(40) NOT NULL,
    agreement_version INTEGER NOT NULL,
    product_policy_version VARCHAR(32) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE loan_agreements (
    id UUID PRIMARY KEY,
    loan_id UUID NOT NULL,
    version INTEGER NOT NULL,
    content_hash VARCHAR(64) NOT NULL,
    terms_snapshot VARCHAR(4000) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    accepted_at TIMESTAMP WITH TIME ZONE,
    accepted_by UUID,
    CONSTRAINT fk_agreement_loan FOREIGN KEY (loan_id) REFERENCES loans(id),
    CONSTRAINT uk_agreement_loan_version UNIQUE (loan_id, version)
);

CREATE TABLE repayment_schedules (
    id UUID PRIMARY KEY,
    loan_id UUID NOT NULL,
    due_date DATE NOT NULL,
    principal_due NUMERIC(19,2) NOT NULL,
    interest_due NUMERIC(19,2) NOT NULL,
    total_due NUMERIC(19,2) NOT NULL,
    principal_paid NUMERIC(19,2) NOT NULL,
    interest_paid NUMERIC(19,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_schedule_loan FOREIGN KEY (loan_id) REFERENCES loans(id)
);

CREATE TABLE payments (
    id UUID PRIMARY KEY,
    loan_id UUID NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    idempotency_key VARCHAR(255) NOT NULL,
    provider_reference VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_payment_loan FOREIGN KEY (loan_id) REFERENCES loans(id),
    CONSTRAINT uk_payment_idempotency UNIQUE (idempotency_key),
    CONSTRAINT uk_payment_provider_reference UNIQUE (provider_reference)
);

CREATE TABLE payment_allocations (
    id UUID PRIMARY KEY,
    payment_id UUID NOT NULL,
    loan_id UUID NOT NULL,
    principal_amount NUMERIC(19,2) NOT NULL,
    interest_amount NUMERIC(19,2) NOT NULL,
    fee_amount NUMERIC(19,2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_allocation_payment FOREIGN KEY (payment_id) REFERENCES payments(id),
    CONSTRAINT fk_allocation_loan FOREIGN KEY (loan_id) REFERENCES loans(id),
    CONSTRAINT uk_allocation_payment UNIQUE (payment_id)
);

CREATE TABLE settlements (
    id UUID PRIMARY KEY,
    loan_id UUID NOT NULL,
    original_principal NUMERIC(19,2) NOT NULL,
    total_interest NUMERIC(19,2) NOT NULL,
    total_obligation NUMERIC(19,2) NOT NULL,
    total_paid NUMERIC(19,2) NOT NULL,
    outstanding NUMERIC(19,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    settlement_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_settlement_loan FOREIGN KEY (loan_id) REFERENCES loans(id),
    CONSTRAINT uk_settlement_loan UNIQUE (loan_id)
);

CREATE TABLE audit_events (
    id UUID PRIMARY KEY,
    loan_id UUID NOT NULL,
    event_type VARCHAR(64) NOT NULL,
    actor_type VARCHAR(64) NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    metadata VARCHAR(2000) NOT NULL
);

CREATE INDEX idx_agreements_loan ON loan_agreements(loan_id);
CREATE INDEX idx_schedules_loan_due_date ON repayment_schedules(loan_id, due_date);
CREATE INDEX idx_payments_loan ON payments(loan_id);
CREATE INDEX idx_allocations_loan ON payment_allocations(loan_id);
CREATE INDEX idx_audit_events_loan_occurred_at ON audit_events(loan_id, occurred_at);
