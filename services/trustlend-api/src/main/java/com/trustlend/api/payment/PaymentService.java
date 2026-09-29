package com.trustlend.api.payment;

import com.trustlend.api.loan.Loan;
import com.trustlend.api.loan.LoanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository repository;
    private final LoanService loanService;

    public PaymentService(PaymentRepository repository, LoanService loanService) {
        this.repository = repository;
        this.loanService = loanService;
    }

    @Transactional
    public Payment record(UUID loanId, BigDecimal amount, String idempotencyKey, String providerReference) {
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Payment must be positive");
        if (idempotencyKey == null || idempotencyKey.isBlank()) throw new IllegalArgumentException("Idempotency key is required");
        return repository.findByIdempotencyKey(idempotencyKey).orElseGet(() -> {
            Loan loan = loanService.get(loanId);
            if (loan.getStatus() == com.trustlend.api.loan.LoanStatus.SETTLED)
                throw new IllegalStateException("Settled loan cannot receive a payment");
            return repository.save(new Payment(loan, amount, idempotencyKey, providerReference));
        });
    }

    @Transactional(readOnly = true)
    public java.util.List<Payment> getPayments(UUID loanId) {
        loanService.get(loanId);
        return repository.findByLoanId(loanId);
    }
}
