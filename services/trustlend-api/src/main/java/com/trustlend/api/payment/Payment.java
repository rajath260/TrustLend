package com.trustlend.api.payment;

import com.trustlend.api.loan.Loan;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments", uniqueConstraints = @UniqueConstraint(name = "uk_payment_idempotency", columnNames = "idempotencyKey"))
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal amount;
    @Column(nullable = false, unique = true) private String idempotencyKey;
    @Column(nullable = false) private String providerReference;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private PaymentStatus status;
    @Column(nullable = false, updatable = false) private Instant createdAt;

    protected Payment() {}

    public Payment(Loan loan, BigDecimal amount, String idempotencyKey, String providerReference) {
        this.loan = loan;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
        this.providerReference = providerReference;
        this.status = PaymentStatus.RECONCILED;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public Loan getLoan() { return loan; }
    public BigDecimal getAmount() { return amount; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getProviderReference() { return providerReference; }
    public PaymentStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
