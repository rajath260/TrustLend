package com.trustlend.api.agreement;

import com.trustlend.api.audit.AuditEventService;
import com.trustlend.api.loan.Loan;
import com.trustlend.api.loan.LoanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class AgreementService {
    private final AgreementRepository repository;
    private final LoanService loanService;
    private final AuditEventService auditEventService;

    public AgreementService(AgreementRepository repository, LoanService loanService,
                            AuditEventService auditEventService) {
        this.repository = repository;
        this.loanService = loanService;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public Agreement createInitial(Loan loan) {
        String snapshot = "Loan " + loan.getId() + " | principal=" + loan.getPrincipal()
                + " | apr=" + loan.getApr() + " | interestMethod=" + loan.getInterestMethod()
                + " | startDate=" + loan.getStartDate() + " | maturityDate=" + loan.getMaturityDate()
                + " | productPolicyVersion=" + loan.getProductPolicyVersion();
        return repository.save(new Agreement(loan, loan.getAgreementVersion(), sha256(snapshot), snapshot));
    }

    @Transactional
    public Agreement accept(UUID loanId, UUID actorId) {
        Loan loan = loanService.get(loanId);
        Agreement agreement = repository.findByLoanIdAndVersion(loanId, loan.getAgreementVersion())
                .orElseThrow(() -> new IllegalStateException("Agreement version not found"));
        if (!loan.getBorrowerId().equals(actorId)) {
            throw new IllegalArgumentException("Only the borrower can accept the agreement");
        }
        agreement.accept(actorId);
        loanService.accept(loanId);
        Agreement saved = repository.save(agreement);
        auditEventService.record(loanId, "LoanAccepted", "BORROWER",
                "agreementVersion=" + agreement.getVersion() + ";actorId=" + actorId);
        return saved;
    }

    @Transactional(readOnly = true)
    public Agreement getCurrent(UUID loanId) {
        Loan loan = loanService.get(loanId);
        return repository.findByLoanIdAndVersion(loanId, loan.getAgreementVersion())
                .orElseThrow(() -> new IllegalStateException("Agreement version not found"));
    }

    private String sha256(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash agreement", e);
        }
    }
}
