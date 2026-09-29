package com.trustlend.api.audit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class AuditEventService {
    private final AuditEventRepository repository;

    public AuditEventService(AuditEventRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AuditEvent record(UUID loanId, String eventType, String actorType, String metadata) {
        return repository.save(new AuditEvent(loanId, eventType, actorType, metadata));
    }

    @Transactional(readOnly = true)
    public List<AuditEvent> getForLoan(UUID loanId) {
        return repository.findByLoanIdOrderByOccurredAtAsc(loanId);
    }
}
