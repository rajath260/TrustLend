package com.trustlend.api.loan;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID lenderId;

    @Column(nullable = false)
    private UUID borrowerId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal principal;

    @Column(nullable = false, precision = 7, scale = 4)
    private BigDecimal apr;

    @Column(nullable = false, length = 32)
    private String interestMethod;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate maturityDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private LoanStatus status;

    @Column(nullable = false)
    private Integer agreementVersion;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Loan() {}

    public Loan(UUID lenderId, UUID borrowerId, BigDecimal principal, BigDecimal apr,
                String interestMethod, LocalDate startDate, LocalDate maturityDate) {
        this.lenderId = lenderId;
        this.borrowerId = borrowerId;
        this.principal = principal;
        this.apr = apr;
        this.interestMethod = interestMethod;
        this.startDate = startDate;
        this.maturityDate = maturityDate;
        this.status = LoanStatus.PENDING_BORROWER_ACCEPTANCE;
        this.agreementVersion = 1;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getLenderId() { return lenderId; }
    public UUID getBorrowerId() { return borrowerId; }
    public BigDecimal getPrincipal() { return principal; }
    public BigDecimal getApr() { return apr; }
    public String getInterestMethod() { return interestMethod; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getMaturityDate() { return maturityDate; }
    public LoanStatus getStatus() { return status; }
    public Integer getAgreementVersion() { return agreementVersion; }
    public Instant getCreatedAt() { return createdAt; }

    public void accept() {
        if (status != LoanStatus.PENDING_BORROWER_ACCEPTANCE) {
            throw new IllegalStateException("Loan is not awaiting borrower acceptance");
        }
        status = LoanStatus.ACCEPTED;
    }

    public void activate() {
        if (status != LoanStatus.ACCEPTED) {
            throw new IllegalStateException("Loan must be accepted before activation");
        }
        status = LoanStatus.ACTIVE;
    }

    public void markPartiallyPaid() {
        if (status != LoanStatus.ACTIVE && status != LoanStatus.DUE && status != LoanStatus.OVERDUE
                && status != LoanStatus.PARTIALLY_PAID) {
            throw new IllegalStateException("Loan cannot receive repayment in its current state");
        }
        status = LoanStatus.PARTIALLY_PAID;
    }

    public void settle() {
        if (status != LoanStatus.ACTIVE && status != LoanStatus.DUE
                && status != LoanStatus.OVERDUE && status != LoanStatus.PARTIALLY_PAID) {
            throw new IllegalStateException("Loan cannot be settled in its current state");
        }
        status = LoanStatus.SETTLED;
    } 
}
