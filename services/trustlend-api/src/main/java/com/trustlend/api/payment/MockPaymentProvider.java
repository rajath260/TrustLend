package com.trustlend.api.payment;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class MockPaymentProvider implements PaymentProvider {
    @Override
    public ProviderPayment recordPayment(UUID loanId, BigDecimal amount,
                                         String idempotencyKey, String providerReference) {
        if (providerReference == null || providerReference.isBlank()) {
            throw new IllegalArgumentException("Provider reference is required");
        }
        return new ProviderPayment(providerReference);
    }
}
