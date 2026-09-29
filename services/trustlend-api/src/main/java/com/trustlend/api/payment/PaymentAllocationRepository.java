package com.trustlend.api.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentAllocationRepository extends JpaRepository<PaymentAllocation, UUID> {
    Optional<PaymentAllocation> findByPaymentId(UUID paymentId);
    List<PaymentAllocation> findByLoanId(UUID loanId);
}
