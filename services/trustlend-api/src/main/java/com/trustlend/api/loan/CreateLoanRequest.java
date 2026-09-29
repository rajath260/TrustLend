package com.trustlend.api.loan;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateLoanRequest(
        @NotNull UUID lenderId,
        @NotNull UUID borrowerId,
        @NotNull @DecimalMin("0.01") BigDecimal principal,
        @NotNull @DecimalMin("0.00") BigDecimal apr,
        @NotNull String interestMethod,
        @NotNull LocalDate startDate,
        @NotNull LocalDate maturityDate
) {}
