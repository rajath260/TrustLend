package com.trustlend.api.repayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface RepaymentScheduleRepository extends JpaRepository<RepaymentSchedule, UUID> {
    List<RepaymentSchedule> findByLoanIdOrderByDueDate(UUID loanId);
}
