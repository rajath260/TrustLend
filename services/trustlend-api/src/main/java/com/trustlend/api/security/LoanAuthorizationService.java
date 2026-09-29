package com.trustlend.api.security;

import com.trustlend.api.loan.Loan;
import org.springframework.stereotype.Service;

@Service
public class LoanAuthorizationService {
    public void requireBorrower(Loan loan, ActorIdentity actor) {
        if (!loan.getBorrowerId().equals(actor.userId())) {
            throw new IllegalArgumentException("Only the borrower can perform this action");
        }
    }

    public void requireLender(Loan loan, ActorIdentity actor) {
        if (!loan.getLenderId().equals(actor.userId())) {
            throw new IllegalArgumentException("Only the lender can perform this action");
        }
    }

    public void requireParticipant(Loan loan, ActorIdentity actor) {
        if (!loan.getLenderId().equals(actor.userId())
                && !loan.getBorrowerId().equals(actor.userId())) {
            throw new IllegalArgumentException("Actor is not a participant in this loan");
        }
    }
}
