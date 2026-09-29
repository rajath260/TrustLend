package com.trustlend.api.agreement;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AgreementRepository extends JpaRepository<Agreement, UUID> {
    Optional<Agreement> findByLoanIdAndVersion(UUID loanId, Integer version);
}
