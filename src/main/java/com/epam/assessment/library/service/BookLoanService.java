package com.epam.assessment.library.service;

import com.epam.assessment.library.domain.Loan;
import com.epam.assessment.library.persistence.BookRepository;
import com.epam.assessment.library.persistence.LoanRepository;
import com.epam.assessment.library.service.response.BookUsage;
import com.epam.assessment.library.service.response.RatedBook;
import com.epam.assessment.library.domain.Book;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class BookLoanService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public BookLoanService(BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

    public List<RatedBook> queryTopRatedBooks(int topN) {
        List<RatedBook> result = new ArrayList<>();
        List<Book> processedBooks = new ArrayList<>();

        for (Loan loan : loanRepository.getLoans()) {
            Book book = loan.getBook();

            if (processedBooks.contains(book)) {
                continue;
            }

            double sum = 0;
            int count = 0;

            for (Loan currentLoan : loanRepository.getLoans()) {
                if (currentLoan.getBook().getBookId() == book.getBookId() && currentLoan.getRating() != null) {
                    sum += currentLoan.getRating();
                    count++;
                }
            }

            if (count > 0) {
                double average = sum / count;
                average = BigDecimal.valueOf(average)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();

                result.add(new RatedBook(book, average));
            }

            processedBooks.add(book);
        }

        result.sort(Comparator.comparing(RatedBook::rating).reversed());

        if (result.size() > topN) {
            return result.subList(0, topN);
        }

        return result;
    }

    public List<BookUsage> queryMostWantedBooks(int topN) {
        List<BookUsage> result = new ArrayList<>();
        List<Book> processedBooks = new ArrayList<>();

        for (Loan loan : loanRepository.getLoans()) {
            Book book = loan.getBook();

            if (processedBooks.contains(book)) {
                continue;
            }

            int count = 0;

            for (Loan currentLoan : loanRepository.getLoans()) {
                if (currentLoan.getBook().getBookId() == book.getBookId()) {
                    count++;
                }
            }

            result.add(new BookUsage(book, count));
            processedBooks.add(book);
        }

        result.sort(Comparator.comparing(BookUsage::count).reversed());

        if (result.size() > topN) {
            return result.subList(0, topN);
        }

        return result;
    }

    public Loan createLoan(long bookId) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);

        if (bookOptional.isEmpty()) {
            throw new IllegalArgumentException("Book not found with ID: " + bookId);
        }

        Loan loan = new Loan(
                loanRepository.getNextLoanId(),
                bookOptional.get(),
                LocalDateTime.now(),
                null,
                null
        );

        loanRepository.getLoans().add(loan);
        return loan;
    }

    public void returnBook(long loanId, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Optional<Loan> loanOptional = loanRepository.findById(loanId);

        if (loanOptional.isEmpty()) {
            throw new IllegalArgumentException("Loan not found with ID: " + loanId);
        }

        Loan loan = loanOptional.get();

        if (loan.getReturnDate() != null) {
            throw new IllegalStateException("Loan with ID: " + loanId + " is already returned");
        }

        loan.setReturnDate(LocalDateTime.now());
        loan.setRating(rating);
    }
}
