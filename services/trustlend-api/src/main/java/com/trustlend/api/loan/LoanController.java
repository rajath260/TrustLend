package com.trustlend.api.loan;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {

    private final LoanService service;

    public LoanController(LoanService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Loan create(@Valid @RequestBody CreateLoanRequest request) {
        return service.create(request);
    }

    @GetMapping("/{loanId}")
    public Loan get(@PathVariable UUID loanId) {
        return service.get(loanId);
    }

    @PostMapping("/{loanId}/accept")
    public Loan accept(@PathVariable UUID loanId) {
        return service.accept(loanId);
    }
}
