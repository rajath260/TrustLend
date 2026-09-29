package com.trustlend.api.settlement;

import com.trustlend.api.audit.AuditEventService;
import com.trustlend.api.loan.Loan;
import com.trustlend.api.loan.LoanService;
import com.trustlend.api.payment.PaymentAllocation;
import com.trustlend.api.payment.PaymentAllocationService;
import com.trustlend.api.repayment.RepaymentSchedule;
import com.trustlend.api.repayment.RepaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class SettlementService {
    private final SettlementRepository repository;
    private final LoanService loanService;
    private final PaymentAllocationService allocationService;
    private final RepaymentService repaymentService;
    private final AuditEventService auditEventService;

    public SettlementService(SettlementRepository repository, LoanService loanService,
                             PaymentAllocationService allocationService, RepaymentService repaymentService,
                             AuditEventService auditEventService) {
        this.repository = repository;
        this.loanService = loanService;
        this.allocationService = allocationService;
        this.repaymentService = repaymentService;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public Settlement settle(UUID loanId) {
        Loan loan = loanService.get(loanId);
        if (repository.findByLoanId(loanId).isPresent())
            throw new IllegalStateException("Loan is already settled");

        BigDecimal totalInterest = repaymentService.getSchedule(loanId).stream()
                .map(RepaymentSchedule::getInterestDue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalObligation = loan.getPrincipal().add(totalInterest);

        BigDecimal totalPaid = allocationService.getAllocations(loanId).stream()
                .map(a -> a.getPrincipalAmount().add(a.getInterestAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal outstanding = totalObligation.subtract(totalPaid);
        if (outstanding.signum() < 0)
            throw new IllegalStateException("Loan has an overpayment that requires explicit handling");
        if (outstanding.signum() != 0)
            throw new IllegalStateException("Loan cannot be settled while outstanding is " + outstanding);

        loan.settle();
        Settlement settlement = repository.save(new Settlement(
                loan, loan.getPrincipal(), totalInterest, totalObligation, totalPaid, outstanding));

        auditEventService.record(loanId, "SettlementGenerated", "SYSTEM",
                "totalObligation=" + totalObligation + ";totalPaid=" + totalPaid);
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
