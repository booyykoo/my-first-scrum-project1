package com.epam.assessment.library.domain;

import java.time.LocalDateTime;

public class Loan {
    private long loanId;
    private Book book;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;
    private Integer rating;

    public Loan(long loanId, Book book, LocalDateTime borrowDate, LocalDateTime returnDate, Integer rating) {
        this.loanId = loanId;
        this.book = book;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.rating = rating;
    }

    public long getLoanId() {
        return loanId;
    }

    public Book getBook() {
        return book;
    }

    public LocalDateTime getBorrowDate() {
        return borrowDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public Integer getRating() {
        return rating;
    }

}
