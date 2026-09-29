package com.trustlend.api.policy;

import java.math.BigDecimal;

public record ProductPolicy(
        String version,
        BigDecimal minApr,
        BigDecimal maxApr
) {
    public static ProductPolicy mvp() {
        // Product-policy example only; this is not an RBI-mandated rate cap.
        return new ProductPolicy("MVP-1", new BigDecimal("0.00"), new BigDecimal("15.00"));
    }
}
