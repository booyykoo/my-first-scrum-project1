package com.epam.assessment.library.service;

import com.epam.assessment.library.domain.Loan;
import com.epam.assessment.library.persistence.BookRepository;
import com.epam.assessment.library.persistence.LoanRepository;
import com.epam.assessment.library.service.response.BookUsage;
import com.epam.assessment.library.service.response.RatedBook;

import java.util.List;

public class BookLoanService {

    public BookLoanService(BookRepository bookRepository, LoanRepository loanRepository) {
    }

    public List<RatedBook> queryTopRatedBooks(int topN) {
        return null;
    }

    public List<BookUsage> queryMostWantedBooks(int topN) {
        return null;
    }

    public Loan enterLoan(long bookId) {
        return null;
    }

    public void returnBook(long loanId, int rating) {
    }
}
