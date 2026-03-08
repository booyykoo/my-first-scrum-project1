package com.epam.assessment.library.service;

import com.epam.assessment.library.domain.Book;
import com.epam.assessment.library.domain.Genre;
import com.epam.assessment.library.domain.Loan;
import com.epam.assessment.library.persistence.BookRepository;
import com.epam.assessment.library.persistence.LoanRepository;
import com.epam.assessment.library.service.response.BookUsage;
import com.epam.assessment.library.service.response.RatedBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookLoanServiceTest {
    private BookRepository bookRepositoryMock;
    private LoanRepository loanRepositoryMock;
    private BookLoanService bookLoanService;

    private Book book1;
    private Book book2;
    private Book book3;
    private Book book4;

    @BeforeEach
    public void setup() {
        bookRepositoryMock = mock(BookRepository.class);
        loanRepositoryMock = mock(LoanRepository.class);
        bookLoanService = new BookLoanService(bookRepositoryMock, loanRepositoryMock);

        createBooks();
        setupLoans();
    }

    private void createBooks() {
        book1 = new Book(1, "978-0451524935", "Jane Austen", "Pride and Prejudice", Genre.FICTION);
        book2 = new Book(2, "978-0261103573", "J. R. R. Tolkien", "The Lord of the Rings", Genre.FANTASY);
        book3 = new Book(3, "978-0062316110", "Antoine de Saint-Exupéry", "The Little Prince", Genre.FICTION);
        book4 = new Book(4, "978-0062316110", "J. K. Rowling", "Harry Potter and the Philosopher's Stone", Genre.FICTION);
    }

    private void setupLoans() {
        List<Loan> loans = Arrays.asList(
                new Loan(1001, book1, LocalDateTime.now(), LocalDateTime.now(), 3),
                new Loan(1002, book1, LocalDateTime.now(), LocalDateTime.now(), 5),

                new Loan(1003, book2, LocalDateTime.now(), LocalDateTime.now(), 5),
                new Loan(1004, book2, LocalDateTime.now(), LocalDateTime.now(), 4),

                new Loan(1005, book3, LocalDateTime.now(), LocalDateTime.now(), 5),
                new Loan(1006, book3, LocalDateTime.now(), LocalDateTime.now(), 4),
                new Loan(1007, book3, LocalDateTime.now(), LocalDateTime.now(), 4),

                new Loan(1008, book4, LocalDateTime.now(), LocalDateTime.now(), 3)
        );
        when(loanRepositoryMock.getLoans()).thenReturn(loans);
    }

    @Test
    public void queryTopRatedBooks_threeBooksWithRatings_returnsTop3ByAverageRating() {
        // Given
        // see setup()

        // When
        List<RatedBook> topRatedBooks = bookLoanService.queryTopRatedBooks(3);

        // Then
        assertEquals(3, topRatedBooks.size(), "Number of books returned");

        assertEquals(2, topRatedBooks.get(0).book().getBookId(), "id of first book");
        assertEquals(4.5, topRatedBooks.get(0).rating(), 0.005);

        assertEquals(3, topRatedBooks.get(1).book().getBookId(), "id of second book");
        assertEquals(4.33, topRatedBooks.get(1).rating(), 0.005);

        assertEquals(1, topRatedBooks.get(2).book().getBookId(), "id of third book");
        assertEquals(4.0, topRatedBooks.get(2).rating(), 0.005);
    }

    @Test
    public void queryMostWantedBooks_threeBooksWithDifferentLoanCounts_returnsTop3ByUsageCount() {
        // Given
        // see setup()

        // When
        List<BookUsage> mostWantedBooks = bookLoanService.queryMostWantedBooks(3);

        // Then
        assertEquals(3, mostWantedBooks.size(), "Number of books returned");

        Optional<BookUsage> book1Opt = findBookUsageByBookId(mostWantedBooks, 1);
        assertTrue(book1Opt.isPresent(), "Book id=1 should be in the results");
        assertEquals(2, book1Opt.get().count(), "loan count of book id=1");

        Optional<BookUsage> book2Opt = findBookUsageByBookId(mostWantedBooks, 2);
        assertTrue(book2Opt.isPresent(), "Book id=1 should be in the results");
        assertEquals(2, book2Opt.get().count(), "loan count of book id=2");

        Optional<BookUsage> book3Opt = findBookUsageByBookId(mostWantedBooks, 3);
        assertTrue(book3Opt.isPresent(), "Book id=3 should be in the results");
        assertEquals(3, book3Opt.get().count(), "loan count of book id=3");
    }

    private Optional<BookUsage> findBookUsageByBookId(List<BookUsage> bookUsages, long bookId) {
        return bookUsages.stream()
                .filter(bu -> bu.book().getBookId() == bookId)
                .findFirst();
    }
}
