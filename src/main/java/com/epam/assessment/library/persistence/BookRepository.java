package com.epam.assessment.library.persistence;

import com.epam.assessment.library.domain.Book;
import com.epam.assessment.library.domain.Genre;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepository {


    private final String filePath;
    private final List<Book> books;

    public BookRepository(String filePath) {
        this.filePath = filePath;
        this.books = new ArrayList<>();
    }

    public void loadBooks() {
        books.clear();

        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line == null || line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(",");

                if (parts.length != 5) {
                    throw new IllegalArgumentException("Invalid CSV: " + line);
                }

                long bookId = Long.parseLong(parts[0].trim());
                String isbn = parts[1].trim();
                String author = parts[2].trim();
                String title = parts[3].trim();
                Genre genre = Genre.valueOf(parts[4].trim());

                Book book = new Book(bookId, isbn, author, title, genre);
                books.add(book);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed (to load books from file): " + filePath, e);
        }
    }

    public Optional<Book> findById(long bookId) {
        for (Book book : books) {
            if (book.getBookId() == bookId) {
                return Optional.of(book);
            }
        }
        return Optional.empty();
    }
}
