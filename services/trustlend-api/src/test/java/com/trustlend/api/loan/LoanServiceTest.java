package com.trustlend.api.loan;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanServiceTest {

    @Test
    void newLoanStartsAwaitingBorrowerAcceptance() {
        Loan loan = new Loan(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("20000.00"),
                BigDecimal.ZERO,
                "NONE",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2027, 2, 1)
        );

        assertEquals(LoanStatus.PENDING_BORROWER_ACCEPTANCE, loan.getStatus());
        assertEquals(new BigDecimal("20000.00"), loan.getPrincipal());
    }

    @Test
    void acceptedLoanMovesToAccepted() {
        Loan loan = new Loan(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("20000.00"),
                BigDecimal.ZERO,
                "NONE",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2027, 2, 1)
        );

        loan.accept();

        assertEquals(LoanStatus.ACCEPTED, loan.getStatus());
    }
}
