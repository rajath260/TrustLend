package com.trustlend.api.payment;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentProvider {
    ProviderPayment recordPayment(UUID loanId, BigDecimal amount, String idempotencyKey, String providerReference);

    record ProviderPayment(String providerReference) {}
}
