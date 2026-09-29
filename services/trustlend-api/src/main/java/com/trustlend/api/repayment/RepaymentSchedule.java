package com.trustlend.api.repayment;

import com.trustlend.api.loan.Loan;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "repayment_schedules")
public class RepaymentSchedule {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;
    @Column(nullable = false) private LocalDate dueDate;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal principalDue;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal interestDue;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal totalDue;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal principalPaid;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal interestPaid;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private ScheduleStatus status;

    protected RepaymentSchedule() {}

    public RepaymentSchedule(Loan loan, LocalDate dueDate, BigDecimal principalDue, BigDecimal interestDue) {
        this.loan = loan;
        this.dueDate = dueDate;
        this.principalDue = principalDue;
        this.interestDue = interestDue;
        this.totalDue = principalDue.add(interestDue);
        this.principalPaid = BigDecimal.ZERO;
        this.interestPaid = BigDecimal.ZERO;
        this.status = ScheduleStatus.DUE;
    }

    public UUID getId() { return id; }
    public Loan getLoan() { return loan; }
    public LocalDate getDueDate() { return dueDate; }
    public BigDecimal getPrincipalDue() { return principalDue; }
    public BigDecimal getInterestDue() { return interestDue; }
    public BigDecimal getTotalDue() { return totalDue; }
    public BigDecimal getPrincipalPaid() { return principalPaid; }
    public BigDecimal getInterestPaid() { return interestPaid; }
    public BigDecimal getOutstanding() { return totalDue.subtract(principalPaid.add(interestPaid)).max(BigDecimal.ZERO); }
    public ScheduleStatus getStatus() { return status; }
    public void applyPayment(BigDecimal principalAmount, BigDecimal interestAmount) {
        principalPaid = principalPaid.add(principalAmount);
        interestPaid = interestPaid.add(interestAmount);
        BigDecimal paid = principalPaid.add(interestPaid);
        if (paid.compareTo(totalDue) >= 0) {
            principalPaid = principalDue;
            interestPaid = interestDue;
            status = ScheduleStatus.PAID;
        } else {
            status = ScheduleStatus.PARTIALLY_PAID;
        }
    }
}
