package com.trustlend.api.repaymentrecord;

import java.math.BigDecimal;
import java.util.UUID;

public record RepaymentRecord(
        UUID userId,
        long totalLoans,
        long settledLoans,
        BigDecimal principalOriginated,
        BigDecimal principalRepaid,
        BigDecimal principalOutstanding) {}
