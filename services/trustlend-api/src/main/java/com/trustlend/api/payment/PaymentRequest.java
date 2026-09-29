package com.trustlend.api.payment;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
public record PaymentRequest(
    @NotNull @DecimalMin("0.01") BigDecimal amount,
    @NotBlank String idempotencyKey,
    @NotBlank String providerReference
) {}
