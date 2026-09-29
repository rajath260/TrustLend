package com.trustlend.api.payment;

import com.trustlend.api.loan.Loan;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_allocations",
       uniqueConstraints = @UniqueConstraint(name = "uk_allocation_payment", columnNames = "payment_id"))
public class PaymentAllocation {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal principalAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal interestAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal feeAmount;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected PaymentAllocation() {}

    public PaymentAllocation(Payment payment, Loan loan, BigDecimal principalAmount,
                             BigDecimal interestAmount, BigDecimal feeAmount) {
        this.payment = payment;
        this.loan = loan;
        this.principalAmount = principalAmount;
        this.interestAmount = interestAmount;
        this.feeAmount = feeAmount;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public Payment getPayment() { return payment; }
    public Loan getLoan() { return loan; }
    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public BigDecimal getInterestAmount() { return interestAmount; }
    public BigDecimal getFeeAmount() { return feeAmount; }
    public Instant getCreatedAt() { return createdAt; }
}
