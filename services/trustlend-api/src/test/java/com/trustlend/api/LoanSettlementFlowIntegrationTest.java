package com.trustlend.api;

import com.trustlend.api.agreement.AgreementService;
import com.trustlend.api.loan.CreateLoanRequest;
import com.trustlend.api.loan.Loan;
import com.trustlend.api.loan.LoanService;
import com.trustlend.api.loan.LoanStatus;
import com.trustlend.api.payment.PaymentService;
import com.trustlend.api.repayment.RepaymentService;
import com.trustlend.api.settlement.SettlementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class LoanSettlementFlowIntegrationTest {

    @Autowired LoanService loanService;
    @Autowired AgreementService agreementService;
    @Autowired RepaymentService repaymentService;
    @Autowired PaymentService paymentService;
    @Autowired SettlementService settlementService;

    @Test
    void interestFreeLoanCanBeRepaidAndSettled() {
        UUID lender = UUID.randomUUID();
        UUID borrower = UUID.randomUUID();

        Loan loan = loanService.create(new CreateLoanRequest(
                lender, borrower, new BigDecimal("20000.00"), BigDecimal.ZERO,
                "NONE", LocalDate.of(2026, 10, 1), LocalDate.of(2027, 2, 1)));

        agreementService.createInitial(loan);
        agreementService.accept(loan.getId(), borrower);

        var schedule = repaymentService.createEqualPrincipalSchedule(loan.getId(), 4);
        assertEquals(4, schedule.size());
        assertEquals(BigDecimal.ZERO.setScale(2), schedule.get(0).getInterestDue());

        assertThrows(IllegalStateException.class,
                () -> settlementService.settle(loan.getId()));

        for (int i = 1; i <= 4; i++) {
            paymentService.record(
                    loan.getId(),
                    new BigDecimal("5000.00"),
                    "idem-" + i,
                    "provider-" + i);
        }

        var duplicate = paymentService.record(
                loan.getId(), new BigDecimal("5000.00"), "idem-4", "provider-4");
        assertNotNull(duplicate);

        var settlement = settlementService.settle(loan.getId());
        assertEquals(new BigDecimal("20000.00"), settlement.getTotalObligation());
        assertEquals(BigDecimal.ZERO.setScale(2), settlement.getOutstanding());
        assertEquals(LoanStatus.SETTLED, loanService.get(loan.getId()).getStatus());
    }

    @Test
    void simpleInterestIsIncludedInContractualSchedule() {
        UUID lender = UUID.randomUUID();
        UUID borrower = UUID.randomUUID();

        Loan loan = loanService.create(new CreateLoanRequest(
                lender, borrower, new BigDecimal("20000.00"), new BigDecimal("12.00"),
                "SIMPLE", LocalDate.of(2026, 10, 1), LocalDate.of(2027, 2, 1)));

        agreementService.createInitial(loan);
        agreementService.accept(loan.getId(), borrower);

        var schedule = repaymentService.createEqualPrincipalSchedule(loan.getId(), 4);
        BigDecimal totalInterest = schedule.stream()
                .map(s -> s.getInterestDue())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(new BigDecimal("800.00"), totalInterest);
        assertEquals(new BigDecimal("20800.00"),
                schedule.stream().map(s -> s.getTotalDue())
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}
