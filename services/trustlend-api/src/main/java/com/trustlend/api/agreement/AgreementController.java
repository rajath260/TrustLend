package com.trustlend.api.agreement;

import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans/{loanId}/agreement")
public class AgreementController {
    private final AgreementService service;
    public AgreementController(AgreementService service) { this.service = service; }

    @GetMapping
    public Agreement get(@PathVariable UUID loanId) { return service.getCurrent(loanId); }

    public record AcceptAgreementRequest(UUID actorId) {}

    @PostMapping("/accept")
    public Agreement accept(@PathVariable UUID loanId, @RequestBody AcceptAgreementRequest request) {
        return service.accept(loanId, request.actorId());
    }
}
