package com.trustlend.api.settlement;

import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans/{loanId}/settlement")
public class SettlementController {
    private final SettlementService service;
    public SettlementController(SettlementService service) { this.service = service; }

    @PostMapping
    public Settlement settle(@PathVariable UUID loanId) {
        return service.settle(loanId);
    }

    @GetMapping
    public Settlement get(@PathVariable UUID loanId) {
        return service.get(loanId);
    }
}
