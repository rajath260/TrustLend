package com.trustlend.api.agreement;

import com.trustlend.api.api.AgreementResponse;

import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans/{loanId}/agreement")
public class AgreementController {
    private final AgreementService service;
    public AgreementController(AgreementService service) { this.service = service; }

    @GetMapping
    public AgreementResponse get(@PathVariable UUID loanId) { return AgreementResponse.from(service.getCurrent(loanId)); }

    public record AcceptAgreementRequest(UUID actorId) {}

    @PostMapping("/accept")
    public AgreementResponse accept(@PathVariable UUID loanId, @RequestBody AcceptAgreementRequest request) {
        return AgreementResponse.from(service.accept(loanId, request.actorId()));
    }
}
