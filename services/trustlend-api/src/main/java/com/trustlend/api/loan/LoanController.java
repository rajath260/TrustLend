package com.trustlend.api.loan;

import com.trustlend.api.agreement.AgreementService;
import com.trustlend.api.audit.AuditEventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {
    private final LoanService service;
    private final AgreementService agreementService;
    private final AuditEventService auditEventService;

    public LoanController(LoanService service, AgreementService agreementService,
                          AuditEventService auditEventService) {
        this.service = service;
        this.agreementService = agreementService;
        this.auditEventService = auditEventService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Loan create(@Valid @RequestBody CreateLoanRequest request) {
        Loan loan = service.create(request);
        agreementService.createInitial(loan);
        auditEventService.record(loan.getId(), "LoanCreated", "LENDER",
                "principal=" + loan.getPrincipal() + ";apr=" + loan.getApr());
        return loan;
    }

    @GetMapping("/{loanId}")
    public Loan get(@PathVariable UUID loanId) {
        return service.get(loanId);
    }

}
