package com.epam.assessment.library.persistence;

import com.epam.assessment.library.domain.Book;
import com.epam.assessment.library.domain.Genre;
import org.junit.jupiter.api.Test;
import java.nio.file.Paths;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class BookRepositoryTest {
    @Test
    public void loadBooks_validFile_booksLoadedAndFindByIdReturnsCorrectBooks() {
        // Given
        String booksFilePath = Paths.get("test_data", "books.csv").toString();
        BookRepository bookRepository = new BookRepository(booksFilePath);

        // When
        bookRepository.loadBooks();
        Optional<Book> book1Opt = bookRepository.findById(1);
        Optional<Book> book2Opt = bookRepository.findById(2);

        // Then
        assertTrue(book1Opt.isPresent(), "Book id=1 should be present");
        validateBook(book1Opt.get(), "Jane Austen", "Pride and Prejudice", Genre.FICTION);

        assertTrue(book2Opt.isPresent(), "Book id=2 should be present");
        validateBook(book2Opt.get(), "J. R. R. Tolkien", "The Lord of the Rings", Genre.FANTASY);
    }

    private static void validateBook(Book book, String author, String title, Genre genre) {
        assertEquals(author, book.getAuthor(), "author of book " + book.getBookId());
        assertEquals(title, book.getTitle(), "title of book " + book.getBookId());
        assertEquals(genre, book.getGenre(), "genre of book " + book.getBookId());
    }
}

