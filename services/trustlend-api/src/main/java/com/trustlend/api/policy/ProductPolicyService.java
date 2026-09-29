package com.trustlend.api.policy;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ProductPolicyService {

    private final ProductPolicy policy = ProductPolicy.mvp();

    public ProductPolicy current() {
        return policy;
    }

    public void validateApr(BigDecimal apr) {
        if (apr == null) throw new IllegalArgumentException("APR is required");
        if (apr.compareTo(policy.minApr()) < 0 || apr.compareTo(policy.maxApr()) > 0) {
            throw new IllegalArgumentException(
                    "APR must be between " + policy.minApr() + "% and " + policy.maxApr() + "% under product policy " + policy.version());
        }
    }
}
