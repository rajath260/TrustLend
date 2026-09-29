package com.trustlend.api.api;

import com.trustlend.api.agreement.Agreement;
import java.time.Instant;
import java.util.UUID;

public record AgreementResponse(
        UUID id, UUID loanId, Integer version, String contentHash, String termsSnapshot,
        Instant createdAt, Instant acceptedAt, UUID acceptedBy) {
    public static AgreementResponse from(Agreement a) {
        return new AgreementResponse(a.getId(), a.getLoan().getId(), a.getVersion(),
                a.getContentHash(), a.getTermsSnapshot(), a.getCreatedAt(), a.getAcceptedAt(), a.getAcceptedBy());
    }
}
