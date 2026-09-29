package com.trustlend.api.payment;

import com.trustlend.api.loan.Loan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentAllocationService {
    private final PaymentAllocationRepository repository;

    public PaymentAllocationService(PaymentAllocationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PaymentAllocation allocatePrincipalOnly(Payment payment) {
        if (payment.getStatus() != PaymentStatus.RECONCILED) {
            throw new IllegalStateException("Only reconciled payments can be allocated");
        }
        if (repository.findByPaymentId(payment.getId()).isPresent()) {
            return repository.findByPaymentId(payment.getId()).orElseThrow();
        }

        Loan loan = payment.getLoan();
        BigDecimal alreadyAllocated = repository.findByLoanId(loan.getId()).stream()
                .map(PaymentAllocation::getPrincipalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remainingPrincipal = loan.getPrincipal().subtract(alreadyAllocated);

        if (payment.getAmount().compareTo(remainingPrincipal) > 0) {
            throw new IllegalStateException("Payment exceeds remaining principal");
        }

        return repository.save(new PaymentAllocation(
                payment, loan, payment.getAmount(), BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Transactional(readOnly = true)
    public List<PaymentAllocation> getAllocations(UUID loanId) {
        return repository.findByLoanId(loanId);
    }
}
