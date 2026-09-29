package com.trustlend.api.settlement;

import com.trustlend.api.audit.AuditEventService;
import com.trustlend.api.loan.Loan;
import com.trustlend.api.loan.LoanService;
import com.trustlend.api.payment.PaymentAllocation;
import com.trustlend.api.payment.PaymentAllocationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class SettlementService {
    private final SettlementRepository repository;
    private final LoanService loanService;
    private final PaymentAllocationService allocationService;
    private final AuditEventService auditEventService;

    public SettlementService(SettlementRepository repository, LoanService loanService,
                             PaymentAllocationService allocationService,
                             AuditEventService auditEventService) {
        this.repository = repository;
        this.loanService = loanService;
        this.allocationService = allocationService;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public Settlement settle(UUID loanId) {
        Loan loan = loanService.get(loanId);
        if (repository.findByLoanId(loanId).isPresent())
            throw new IllegalStateException("Loan is already settled");

        BigDecimal totalPaid = allocationService.getAllocations(loanId).stream()
                .map(PaymentAllocation::getPrincipalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal outstanding = loan.getPrincipal().subtract(totalPaid);
        if (outstanding.signum() < 0)
            throw new IllegalStateException("Loan has an overpayment that requires explicit handling");

        if (outstanding.signum() != 0)
            throw new IllegalStateException("Loan cannot be settled while outstanding is " + outstanding);

        loan.settle();
        Settlement settlement = repository.save(
                new Settlement(loan, loan.getPrincipal(), totalPaid, outstanding));

        auditEventService.record(loanId, "SettlementGenerated", "SYSTEM",
                "totalPaid=" + totalPaid + ";outstanding=" + outstanding);
        auditEventService.record(loanId, "LoanSettled", "SYSTEM",
                "settlementId=" + settlement.getId());
        return settlement;
    }

    @Transactional(readOnly = true)
    public Settlement get(UUID loanId) {
        return repository.findByLoanId(loanId)
                .orElseThrow(() -> new IllegalStateException("Settlement not found for loan " + loanId));
    }
}
