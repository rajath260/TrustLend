package com.trustlend.api.repaymentrecord;

import com.trustlend.api.loan.Loan;
import com.trustlend.api.loan.LoanRepository;
import com.trustlend.api.loan.LoanStatus;
import com.trustlend.api.payment.PaymentAllocation;
import com.trustlend.api.payment.PaymentAllocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class RepaymentRecordService {
    private final LoanRepository loanRepository;
    private final PaymentAllocationRepository allocationRepository;

    public RepaymentRecordService(LoanRepository loanRepository,
                                  PaymentAllocationRepository allocationRepository) {
        this.loanRepository = loanRepository;
        this.allocationRepository = allocationRepository;
    }

    @Transactional(readOnly = true)
    public RepaymentRecord get(UUID userId) {
        var loans = loanRepository.findByLenderIdOrBorrowerId(userId, userId);
        return new RepaymentRecord(
                userId,
                summarize(loans.stream().filter(loan -> loan.getLenderId().equals(userId)).toList()),
                summarize(loans.stream().filter(loan -> loan.getBorrowerId().equals(userId)).toList()));
    }

    private RepaymentRecord.Activity summarize(List<Loan> loans) {
        BigDecimal amount = loans.stream()
                .map(Loan::getPrincipal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal repaid = loans.stream()
                .flatMap(loan -> allocationRepository.findByLoanId(loan.getId()).stream())
                .map(PaymentAllocation::getPrincipalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long settled = loans.stream()
                .filter(loan -> loan.getStatus() == LoanStatus.SETTLED)
                .count();

        return new RepaymentRecord.Activity(
                loans.size(),
                settled,
                amount,
                repaid,
                amount.subtract(repaid).max(BigDecimal.ZERO));
    }
}
