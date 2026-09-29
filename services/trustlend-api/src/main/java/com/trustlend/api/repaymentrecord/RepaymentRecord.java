package com.trustlend.api.repaymentrecord;

import java.math.BigDecimal;
import java.util.UUID;

public record RepaymentRecord(
        UUID userId,
        Activity lendingActivity,
        Activity borrowingActivity) {

    public record Activity(
            long loanCount,
            long settledLoanCount,
            BigDecimal principalAmount,
            BigDecimal principalRepaid,
            BigDecimal principalOutstanding) {}
}
