package com.epam.assessment.library.service.response;

import com.epam.assessment.library.domain.Book;

public record BookUsage(Book book, int count) {
}
