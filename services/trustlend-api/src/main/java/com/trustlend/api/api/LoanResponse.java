package com.trustlend.api.api;

import com.trustlend.api.loan.Loan;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record LoanResponse(
        UUID id, UUID lenderId, UUID borrowerId, BigDecimal principal, BigDecimal apr,
        String interestMethod, LocalDate startDate, LocalDate maturityDate,
        String status, Integer agreementVersion, String productPolicyVersion, Instant createdAt) {
    public static LoanResponse from(Loan loan) {
        return new LoanResponse(loan.getId(), loan.getLenderId(), loan.getBorrowerId(),
                loan.getPrincipal(), loan.getApr(), loan.getInterestMethod(),
                loan.getStartDate(), loan.getMaturityDate(), loan.getStatus().name(),
                loan.getAgreementVersion(), loan.getProductPolicyVersion(), loan.getCreatedAt());
    }
}
