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

        BigDecimal scheduledInterest = scheduleRepository.findByLoanIdOrderByDueDate(loan.getId()).stream()
                .map(RepaymentSchedule::getInterestDue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal allocatedInterest = repository.findByLoanId(loan.getId()).stream()
                .map(PaymentAllocation::getInterestAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal allocatedPrincipal = repository.findByLoanId(loan.getId()).stream()
                .map(PaymentAllocation::getPrincipalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remainingInterest = scheduledInterest.subtract(allocatedInterest).max(BigDecimal.ZERO);
        BigDecimal remainingPrincipal = loan.getPrincipal().subtract(allocatedPrincipal).max(BigDecimal.ZERO);

        BigDecimal interest = payment.getAmount().min(remainingInterest);
        BigDecimal principal = payment.getAmount().subtract(interest);

        if (principal.compareTo(remainingPrincipal) > 0) {
            throw new IllegalStateException("Payment exceeds remaining contractual obligation");
        }

        PaymentAllocation allocation = repository.save(new PaymentAllocation(
                payment, loan, principal, interest, BigDecimal.ZERO));

        applyToSchedule(loan.getId(), interest, principal);
        return allocation;
    }

    private void applyToSchedule(UUID loanId, BigDecimal interestAmount, BigDecimal principalAmount) {
        BigDecimal remainingInterest = interestAmount;
        BigDecimal remainingPrincipal = principalAmount;

        for (RepaymentSchedule schedule : scheduleRepository.findByLoanIdOrderByDueDate(loanId)) {
            BigDecimal interestOpen = schedule.getInterestDue().subtract(schedule.getInterestPaid()).max(BigDecimal.ZERO);
            BigDecimal principalOpen = schedule.getPrincipalDue().subtract(schedule.getPrincipalPaid()).max(BigDecimal.ZERO);

            BigDecimal interest = remainingInterest.min(interestOpen);
            remainingInterest = remainingInterest.subtract(interest);

            BigDecimal principal = remainingPrincipal.min(principalOpen);
            remainingPrincipal = remainingPrincipal.subtract(principal);

            if (interest.signum() > 0 || principal.signum() > 0) {
                schedule.applyPayment(principal, interest);
                scheduleRepository.save(schedule);
            }

            if (remainingInterest.signum() == 0 && remainingPrincipal.signum() == 0) break;
        }

        if (remainingInterest.signum() > 0 || remainingPrincipal.signum() > 0) {
            throw new IllegalStateException("Payment allocation could not be applied to repayment schedule");
        }
    }

    @Transactional(readOnly = true)
    public List<PaymentAllocation> getAllocations(UUID loanId) {
        return repository.findByLoanId(loanId);
    }
}
