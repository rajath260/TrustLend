package com.trustlend.api.loan;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.trustlend.api.policy.ProductPolicyService;

import java.util.UUID;

@Service
public class LoanService {

    private final LoanRepository repository;
    private final ProductPolicyService productPolicyService;

    public LoanService(LoanRepository repository, ProductPolicyService productPolicyService) {
        this.repository = repository;
        this.productPolicyService = productPolicyService;
    }

    @Transactional
    public Loan create(CreateLoanRequest request) {
        if (request.maturityDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException("Maturity date cannot be before start date");
        }
        productPolicyService.validateApr(request.apr());
        String interestMethod = request.interestMethod().trim().toUpperCase();
        if (!interestMethod.equals("NONE") && !interestMethod.equals("SIMPLE")) {
            throw new IllegalArgumentException("MVP supports interestMethod NONE or SIMPLE");
        }
        if (request.apr().compareTo(java.math.BigDecimal.ZERO) == 0 && !interestMethod.equals("NONE")) {
            throw new IllegalArgumentException("0% APR loans must use interestMethod NONE");
        }
        return repository.save(new Loan(
                request.lenderId(),
                request.borrowerId(),
                request.principal(),
                request.apr(),
                interestMethod,
                request.startDate(),
                request.maturityDate()
        ));
    }

    @Transactional(readOnly = true)
    public Loan get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException(id));
    }

    @Transactional
    public Loan accept(UUID id) {
        Loan loan = get(id);
        loan.accept();
        return repository.save(loan);
    }
}
