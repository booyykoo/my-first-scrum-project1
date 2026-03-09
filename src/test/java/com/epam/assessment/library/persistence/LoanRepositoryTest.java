package com.epam.assessment.library.persistence;

import com.epam.assessment.library.domain.Book;
import com.epam.assessment.library.domain.Genre;
import com.epam.assessment.library.domain.Loan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanRepositoryTest {
    public static final String LOANS_FILE_PATH = "test_data/loans.csv";

    private LoanRepository loanRepository;
    private BookRepository bookRepositoryMock;

    @BeforeEach
    public void setup() {
        bookRepositoryMock = mock(BookRepository.class);
        mockBooks();
        loanRepository = new LoanRepository(LOANS_FILE_PATH, bookRepositoryMock);
    }

    private void mockBooks() {
        Book book1 = new Book(1, "978-0451524935", "Jane Austen", "Pride and Prejudice", Genre.FICTION);
        when(bookRepositoryMock.findById(1L)).thenReturn(Optional.of(book1));

        Book book2 = new Book(2, "978-0261103573", "J. R. R. Tolkien", "The Lord of the Rings", Genre.FANTASY);
        when(bookRepositoryMock.findById(2L)).thenReturn(Optional.of(book2));

        Book book3 = new Book(3, "978-0062316110", "Antoine de Saint-Exupéry", "The Little Prince", Genre.FICTION);
        when(bookRepositoryMock.findById(3L)).thenReturn(Optional.of(book3));

        Book book4 = new Book(4, "978-0553380163", "J. K. Rowling", "Harry Potter and the Philosopher's Stone", Genre.FICTION);
        when(bookRepositoryMock.findById(4L)).thenReturn(Optional.of(book4));
    }

    @Test
    public void findById_existingLoanId_returnsCorrectLoan() {
        // Given: see setup

        // When
        loanRepository.loadLoans();
        Optional<Loan> loanOptional = loanRepository.findById(1001);

        // Then
        assertTrue(loanOptional.isPresent(), "Loan with ID 1001 should be present");
        Loan loan = loanOptional.get();
        assertEquals(1001, loan.getLoanId(), "loanId");
        assertEquals(1, loan.getBook().getBookId(), "bookId");
        LocalDateTime ldt = LocalDateTime.of(2025, 11, 3, 10, 15);
        assertEquals(ldt, loan.getBorrowDate(), "borrowDate");
    }

    @Test
    public void getLoans_afterLoading_returnsAllLoans() {
        // Given: see setup

        // When
        loanRepository.loadLoans();
        List<Loan> loans = loanRepository.getLoans();

        // Then
        assertEquals(9, loans.size(), "number of loans loaded");
    }

    @Test
    public void getNextLoanId_afterLoading_returnsNextAvailableId() {
        // Given

        // When
        loanRepository.loadLoans();
        long nextLoanId = loanRepository.getNextLoanId();

        // Then
        assertEquals(1010, nextLoanId, "generated loanId");
    }
}
