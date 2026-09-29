package com.trustlend.api.audit;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans/{loanId}/audit-events")
public class AuditEventController {
    private final AuditEventService service;
    public AuditEventController(AuditEventService service) { this.service = service; }

    @GetMapping
    public List<AuditEvent> get(@PathVariable UUID loanId) {
        return service.getForLoan(loanId);
    }
}
