package com.trustlend.api.api;

import com.trustlend.api.payment.Payment;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id, UUID loanId, BigDecimal amount, String idempotencyKey,
        String providerReference, String status, Instant createdAt) {
    public static PaymentResponse from(Payment p) {
        return new PaymentResponse(p.getId(), p.getLoan().getId(), p.getAmount(),
                p.getIdempotencyKey(), p.getProviderReference(), p.getStatus().name(), p.getCreatedAt());
    }
}
