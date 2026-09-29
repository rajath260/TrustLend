package com.trustlend.api.api;

import com.trustlend.api.repayment.RepaymentSchedule;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RepaymentScheduleResponse(
        UUID id, UUID loanId, LocalDate dueDate, BigDecimal principalDue, BigDecimal interestDue,
        BigDecimal totalDue, BigDecimal principalPaid, BigDecimal interestPaid,
        BigDecimal outstanding, String status) {
    public static RepaymentScheduleResponse from(RepaymentSchedule s) {
        return new RepaymentScheduleResponse(s.getId(), s.getLoan().getId(), s.getDueDate(),
                s.getPrincipalDue(), s.getInterestDue(), s.getTotalDue(),
                s.getPrincipalPaid(), s.getInterestPaid(), s.getOutstanding(), s.getStatus().name());
    }
}
