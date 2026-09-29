package com.trustlend.api.settlement;

import com.trustlend.api.loan.Loan;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "settlements")
public class Settlement {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false, unique = true)
    private Loan loan;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal originalPrincipal;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal totalInterest;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal totalObligation;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal totalPaid;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal outstanding;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private SettlementStatus status;
    @Column(nullable = false, updatable = false) private Instant settlementDate;

    protected Settlement() {}

    public Settlement(Loan loan, BigDecimal originalPrincipal, BigDecimal totalInterest,
                      BigDecimal totalObligation, BigDecimal totalPaid, BigDecimal outstanding) {
        this.loan = loan;
        this.originalPrincipal = originalPrincipal;
        this.totalInterest = totalInterest;
        this.totalObligation = totalObligation;
        this.totalPaid = totalPaid;
        this.outstanding = outstanding;
        this.status = SettlementStatus.SETTLED;
        this.settlementDate = Instant.now();
    }

    public UUID getId() { return id; }
    public Loan getLoan() { return loan; }
    public BigDecimal getOriginalPrincipal() { return originalPrincipal; }
    public BigDecimal getTotalInterest() { return totalInterest; }
    public BigDecimal getTotalObligation() { return totalObligation; }
    public BigDecimal getTotalPaid() { return totalPaid; }
    public BigDecimal getOutstanding() { return outstanding; }
    public SettlementStatus getStatus() { return status; }
    public Instant getSettlementDate() { return settlementDate; }
}
