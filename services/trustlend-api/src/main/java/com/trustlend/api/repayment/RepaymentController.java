package com.trustlend.api.repayment;

import org.springframework.http.HttpStatus;
import com.trustlend.api.api.RepaymentScheduleResponse;
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
    public List<RepaymentScheduleResponse> create(@PathVariable UUID loanId, @RequestParam(defaultValue = "4") int installments) {
        return service.createEqualPrincipalSchedule(loanId, installments).stream().map(RepaymentScheduleResponse::from).toList();
    }

    @GetMapping
    public List<RepaymentScheduleResponse> get(@PathVariable UUID loanId) {
        return service.getSchedule(loanId).stream().map(RepaymentScheduleResponse::from).toList();
    }
}
