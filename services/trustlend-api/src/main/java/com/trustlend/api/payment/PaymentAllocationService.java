package com.trustlend.api.payment;

import com.trustlend.api.loan.Loan;
import com.trustlend.api.repayment.RepaymentSchedule;
import com.trustlend.api.repayment.RepaymentScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentAllocationService {
    private final PaymentAllocationRepository repository;
    private final RepaymentScheduleRepository scheduleRepository;

    public PaymentAllocationService(PaymentAllocationRepository repository,
                                    RepaymentScheduleRepository scheduleRepository) {
        this.repository = repository;
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public PaymentAllocation allocateContractualOrder(Payment payment) {
        if (payment.getStatus() != PaymentStatus.RECONCILED) {
            throw new IllegalStateException("Only reconciled payments can be allocated");
        }
        var existing = repository.findByPaymentId(payment.getId());
        if (existing.isPresent()) return existing.get();

        Loan loan = payment.getLoan();
        BigDecimal remainingPayment = payment.getAmount();
        BigDecimal totalInterest = BigDecimal.ZERO;
        BigDecimal totalPrincipal = BigDecimal.ZERO;

        for (RepaymentSchedule schedule : scheduleRepository.findByLoanIdOrderByDueDate(loan.getId())) {
            if (remainingPayment.signum() == 0) break;

            BigDecimal interestOpen = schedule.getInterestDue()
                    .subtract(schedule.getInterestPaid()).max(BigDecimal.ZERO);
            BigDecimal principalOpen = schedule.getPrincipalDue()
                    .subtract(schedule.getPrincipalPaid()).max(BigDecimal.ZERO);

            BigDecimal interest = remainingPayment.min(interestOpen);
            remainingPayment = remainingPayment.subtract(interest);

            BigDecimal principal = remainingPayment.min(principalOpen);
            remainingPayment = remainingPayment.subtract(principal);

            if (interest.signum() > 0 || principal.signum() > 0) {
                schedule.applyPayment(principal, interest);
                scheduleRepository.save(schedule);
                totalInterest = totalInterest.add(interest);
                totalPrincipal = totalPrincipal.add(principal);
            }
        }

        if (remainingPayment.signum() > 0) {
            throw new IllegalStateException("Payment exceeds remaining contractual obligation");
        }

        return repository.save(new PaymentAllocation(
                payment, loan, totalPrincipal, totalInterest, BigDecimal.ZERO));
    }

    @Transactional(readOnly = true)
    public List<PaymentAllocation> getAllocations(UUID loanId) {
        return repository.findByLoanId(loanId);
    }
}
