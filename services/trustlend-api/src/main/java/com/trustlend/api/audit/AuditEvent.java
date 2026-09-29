package com.trustlend.api.audit;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_events")
public class AuditEvent {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID loanId;

    @Column(nullable = false, length = 64)
    private String eventType;

    @Column(nullable = false, length = 64)
    private String actorType;

    @Column(nullable = false, updatable = false)
    private Instant occurredAt;

    @Column(nullable = false, length = 2000)
    private String metadata;

    protected AuditEvent() {}

    public AuditEvent(UUID loanId, String eventType, String actorType, String metadata) {
        this.loanId = loanId;
        this.eventType = eventType;
        this.actorType = actorType;
        this.metadata = metadata;
        this.occurredAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getLoanId() { return loanId; }
    public String getEventType() { return eventType; }
    public String getActorType() { return actorType; }
    public Instant getOccurredAt() { return occurredAt; }
    public String getMetadata() { return metadata; }
}
