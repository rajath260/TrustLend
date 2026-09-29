package com.trustlend.api.api;

import com.trustlend.api.settlement.Settlement;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SettlementResponse(
        UUID id, UUID loanId, BigDecimal originalPrincipal, BigDecimal totalInterest,
        BigDecimal totalObligation, BigDecimal totalPaid, BigDecimal outstanding,
        String status, Instant settlementDate) {
    public static SettlementResponse from(Settlement s) {
        return new SettlementResponse(s.getId(), s.getLoan().getId(), s.getOriginalPrincipal(),
                s.getTotalInterest(), s.getTotalObligation(), s.getTotalPaid(),
                s.getOutstanding(), s.getStatus().name(), s.getSettlementDate());
    }
}
