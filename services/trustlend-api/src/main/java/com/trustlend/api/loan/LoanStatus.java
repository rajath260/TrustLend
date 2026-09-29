package com.trustlend.api.loan;

public enum LoanStatus {
    DRAFT,
    PENDING_BORROWER_ACCEPTANCE,
    ACCEPTED,
    ACTIVE,
    PARTIALLY_PAID,
    DUE,
    OVERDUE,
    SETTLED,
    CANCELLED,
    DISPUTED
}
