package com.trustlend.api.loan;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
    List<Loan> findByLenderIdOrBorrowerId(UUID lenderId, UUID borrowerId);
}
