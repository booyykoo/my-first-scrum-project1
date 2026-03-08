package com.epam.assessment.library.service.response;

import com.epam.assessment.library.domain.Book;

public record RatedBook(Book book, double rating) {
}
