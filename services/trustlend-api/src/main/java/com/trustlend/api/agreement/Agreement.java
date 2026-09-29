package com.trustlend.api.agreement;

import com.trustlend.api.loan.Loan;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "loan_agreements",
       uniqueConstraints = @UniqueConstraint(name = "uk_agreement_loan_version", columnNames = {"loan_id", "version"}))
public class Agreement {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false, length = 64)
    private String contentHash;

    @Column(nullable = false, length = 4000)
    private String termsSnapshot;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column
    private Instant acceptedAt;

    @Column
    private UUID acceptedBy;

    protected Agreement() {}

    public Agreement(Loan loan, Integer version, String contentHash, String termsSnapshot) {
        this.loan = loan;
        this.version = version;
        this.contentHash = contentHash;
        this.termsSnapshot = termsSnapshot;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public Loan getLoan() { return loan; }
    public Integer getVersion() { return version; }
    public String getContentHash() { return contentHash; }
    public String getTermsSnapshot() { return termsSnapshot; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getAcceptedAt() { return acceptedAt; }
    public UUID getAcceptedBy() { return acceptedBy; }

    public void accept(UUID actorId) {
        if (acceptedAt != null) throw new IllegalStateException("Agreement version is already accepted");
        this.acceptedAt = Instant.now();
        this.acceptedBy = actorId;
    }
}
