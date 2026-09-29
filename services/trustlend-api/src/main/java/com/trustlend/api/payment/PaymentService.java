package com.trustlend.api.payment;

import com.trustlend.api.audit.AuditEventService;
import com.trustlend.api.loan.Loan;
import com.trustlend.api.loan.LoanService;
import com.trustlend.api.loan.LoanStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository repository;
    private final LoanService loanService;
    private final PaymentAllocationService allocationService;
    private final AuditEventService auditEventService;

    public PaymentService(PaymentRepository repository, LoanService loanService,
                          PaymentAllocationService allocationService,
                          AuditEventService auditEventService) {
        this.repository = repository;
        this.loanService = loanService;
        this.allocationService = allocationService;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public Payment record(UUID loanId, BigDecimal amount, String idempotencyKey, String providerReference) {
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Payment must be positive");
        if (idempotencyKey == null || idempotencyKey.isBlank()) throw new IllegalArgumentException("Idempotency key is required");

        var existing = repository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            Payment payment = existing.get();
            if (!payment.getLoan().getId().equals(loanId) || payment.getAmount().compareTo(amount) != 0) {
                throw new IllegalArgumentException("Idempotency key was already used for a different payment");
            }
            return payment;
        }

        Loan loan = loanService.get(loanId);
        if (loan.getStatus() == LoanStatus.SETTLED)
            throw new IllegalStateException("Settled loan cannot receive a payment");

        Payment payment = repository.save(new Payment(loan, amount, idempotencyKey, providerReference));
        allocationService.allocateContractualOrder(payment);
        loan.markPartiallyPaid();
        auditEventService.record(loanId, "PaymentReconciled", "SYSTEM",
                "paymentId=" + payment.getId() + ";amount=" + amount);
        auditEventService.record(loanId, "PaymentAllocated", "SYSTEM",
                "paymentId=" + payment.getId() + ";amount=" + amount);
        return payment;
    }

    @Transactional(readOnly = true)
    public List<Payment> getPayments(UUID loanId) {
        loanService.get(loanId);
        return repository.findByLoanId(loanId);
    }
}
