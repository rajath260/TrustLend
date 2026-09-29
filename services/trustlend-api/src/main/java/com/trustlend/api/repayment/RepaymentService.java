package com.trustlend.api.repayment;

import com.trustlend.api.loan.Loan;
import com.trustlend.api.loan.LoanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class RepaymentService {
    private final RepaymentScheduleRepository repository;
    private final LoanService loanService;

    public RepaymentService(RepaymentScheduleRepository repository, LoanService loanService) {
        this.repository = repository;
        this.loanService = loanService;
    }

    @Transactional
    public List<RepaymentSchedule> createEqualPrincipalSchedule(UUID loanId, int installments) {
        if (installments <= 0) throw new IllegalArgumentException("Installments must be positive");
        Loan loan = loanService.get(loanId);
        if (loan.getStatus() != com.trustlend.api.loan.LoanStatus.ACCEPTED)
            throw new IllegalStateException("Loan must be accepted before creating its schedule");

        BigDecimal principalPerInstallment =
                loan.getPrincipal().divide(BigDecimal.valueOf(installments), 2, java.math.RoundingMode.DOWN);
        BigDecimal remainder = loan.getPrincipal()
                .subtract(principalPerInstallment.multiply(BigDecimal.valueOf(installments)));

        repository.deleteAll(repository.findByLoanIdOrderByDueDate(loanId));

        for (int i = 1; i <= installments; i++) {
            BigDecimal principal = principalPerInstallment;
            if (i == installments) principal = principal.add(remainder);
            LocalDate dueDate = loan.getStartDate().plusMonths(i);
            repository.save(new RepaymentSchedule(loan, dueDate, principal, BigDecimal.ZERO));
        }
        loan.activate();
        return repository.findByLoanIdOrderByDueDate(loanId);
    }

    @Transactional(readOnly = true)
    public List<RepaymentSchedule> getSchedule(UUID loanId) {
        loanService.get(loanId);
        return repository.findByLoanIdOrderByDueDate(loanId);
    }
}
