package com.epam.assessment.library.persistence;

import com.epam.assessment.library.domain.Loan;

import java.util.List;
import java.util.Optional;

public class LoanRepository {
    public LoanRepository(String filePath, BookRepository bookRepository) {
    }

    public void loadLoans() {
    }

    public List<Loan> getLoans() {
        return null;
    }

    public Optional<Loan> findById(long loanId) {
        return null;
    }

    public long getNextLoanId() {
        return 0;
    }
}
