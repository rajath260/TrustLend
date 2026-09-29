package com.trustlend.api.repayment;

import com.trustlend.api.audit.AuditEventService;
import com.trustlend.api.loan.Loan;
import com.trustlend.api.loan.LoanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class RepaymentService {
    private final RepaymentScheduleRepository repository;
    private final LoanService loanService;
    private final AuditEventService auditEventService;

    public RepaymentService(RepaymentScheduleRepository repository, LoanService loanService,
                            AuditEventService auditEventService) {
        this.repository = repository;
        this.loanService = loanService;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public List<RepaymentSchedule> createEqualPrincipalSchedule(UUID loanId, int installments) {
        if (installments <= 0) throw new IllegalArgumentException("Installments must be positive");

        Loan loan = loanService.get(loanId);
        if (loan.getStatus() != com.trustlend.api.loan.LoanStatus.ACCEPTED)
            throw new IllegalStateException("Loan must be accepted before creating its schedule");

        String method = loan.getInterestMethod().trim().toUpperCase();
        if (!method.equals("NONE") && !method.equals("SIMPLE")) {
            throw new IllegalArgumentException("MVP supports interestMethod NONE or SIMPLE");
        }

        BigDecimal principalPerInstallment =
                loan.getPrincipal().divide(BigDecimal.valueOf(installments), 2, RoundingMode.DOWN);
        BigDecimal principalRemainder = loan.getPrincipal()
                .subtract(principalPerInstallment.multiply(BigDecimal.valueOf(installments)));

        long months = Math.max(1, ChronoUnit.MONTHS.between(loan.getStartDate(), loan.getMaturityDate()));
        BigDecimal totalInterest = method.equals("NONE")
                ? BigDecimal.ZERO
                : loan.getPrincipal()
                    .multiply(loan.getApr())
                    .multiply(BigDecimal.valueOf(months))
                    .divide(BigDecimal.valueOf(1200), 2, RoundingMode.HALF_UP);

        BigDecimal interestPerInstallment =
                totalInterest.divide(BigDecimal.valueOf(installments), 2, RoundingMode.DOWN);
        BigDecimal interestRemainder =
                totalInterest.subtract(interestPerInstallment.multiply(BigDecimal.valueOf(installments)));

        repository.deleteAll(repository.findByLoanIdOrderByDueDate(loanId));

        for (int i = 1; i <= installments; i++) {
            BigDecimal principal = principalPerInstallment;
            BigDecimal interest = interestPerInstallment;
            if (i == installments) {
                principal = principal.add(principalRemainder);
                interest = interest.add(interestRemainder);
            }
            LocalDate dueDate = loan.getStartDate().plusMonths(i);
            repository.save(new RepaymentSchedule(loan, dueDate, principal, interest));
        }

        loan.activate();
        auditEventService.record(loanId, "ScheduleCreated", "SYSTEM",
                "installments=" + installments + ";totalInterest=" + totalInterest);
        return repository.findByLoanIdOrderByDueDate(loanId);
    }

    @Transactional(readOnly = true)
    public List<RepaymentSchedule> getSchedule(UUID loanId) {
        loanService.get(loanId);
        return repository.findByLoanIdOrderByDueDate(loanId);
    }
}
