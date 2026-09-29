package com.trustlend.api.loan;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class LoanService {

    private final LoanRepository repository;

    public LoanService(LoanRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Loan create(CreateLoanRequest request) {
        if (request.maturityDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException("Maturity date cannot be before start date");
        }
        if (request.apr().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("APR cannot be negative");
        }
        return repository.save(new Loan(
                request.lenderId(),
                request.borrowerId(),
                request.principal(),
                request.apr(),
                request.interestMethod(),
                request.startDate(),
                request.maturityDate()
        ));
    }

    @Transactional(readOnly = true)
    public Loan get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException(id));
    }

    @Transactional
    public Loan accept(UUID id) {
        Loan loan = get(id);
        loan.accept();
        return repository.save(loan);
    }
}
