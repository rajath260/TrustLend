package com.trustlend.api.settlement;

import org.springframework.web.bind.annotation.*;
import com.trustlend.api.api.SettlementResponse;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans/{loanId}/settlement")
public class SettlementController {
    private final SettlementService service;
    public SettlementController(SettlementService service) { this.service = service; }

    @PostMapping
    public SettlementResponse settle(@PathVariable UUID loanId) {
        return SettlementResponse.from(service.settle(loanId));
    }

    @GetMapping
    public SettlementResponse get(@PathVariable UUID loanId) {
        return SettlementResponse.from(service.get(loanId));
    }
}
