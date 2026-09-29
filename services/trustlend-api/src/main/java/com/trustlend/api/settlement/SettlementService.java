package com.trustlend.api.settlement;

import com.trustlend.api.loan.Loan;
import com.trustlend.api.loan.LoanService;
import com.trustlend.api.payment.Payment;
import com.trustlend.api.payment.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class SettlementService {
    private final SettlementRepository repository;
    private final LoanService loanService;
    private final PaymentService paymentService;

    public SettlementService(SettlementRepository repository, LoanService loanService, PaymentService paymentService) {
        this.repository = repository;
        this.loanService = loanService;
        this.paymentService = paymentService;
    }

    @Transactional
    public Settlement settle(UUID loanId) {
        Loan loan = loanService.get(loanId);
        if (repository.findByLoanId(loanId).isPresent())
            throw new IllegalStateException("Loan is already settled");

        BigDecimal totalPaid = paymentService.getPayments(loanId).stream()
                .filter(p -> p.getStatus() == com.trustlend.api.payment.PaymentStatus.RECONCILED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal outstanding = loan.getPrincipal().subtract(totalPaid);
        if (outstanding.signum() < 0) outstanding = BigDecimal.ZERO;

        if (outstanding.signum() != 0)
            throw new IllegalStateException("Loan cannot be settled while outstanding is " + outstanding);

        loan.settle();
        return repository.save(new Settlement(loan, loan.getPrincipal(), totalPaid, outstanding));
    }

    @Transactional(readOnly = true)
    public Settlement get(UUID loanId) {
        return repository.findByLoanId(loanId)
                .orElseThrow(() -> new IllegalStateException("Settlement not found for loan " + loanId));
    }
}
