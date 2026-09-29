package com.trustlend.api.repayment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans/{loanId}/schedule")
public class RepaymentController {
    private final RepaymentService service;
    public RepaymentController(RepaymentService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<RepaymentSchedule> create(@PathVariable UUID loanId, @RequestParam(defaultValue = "4") int installments) {
        return service.createEqualPrincipalSchedule(loanId, installments);
    }

    @GetMapping
    public List<RepaymentSchedule> get(@PathVariable UUID loanId) {
        return service.getSchedule(loanId);
    }
}
