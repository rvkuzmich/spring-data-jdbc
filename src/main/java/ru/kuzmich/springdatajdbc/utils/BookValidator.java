package ru.kuzmich.springdatajdbc.utils;

import ru.kuzmich.springdatajdbc.model.Book;

public class BookValidator {

    public static void validateBook(Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title can not be empty");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Book author can not be empty");
        }
        int currentYear = java.time.Year.now().getValue();
        if (book.getPublicationYear() < 0 || book.getPublicationYear() > currentYear) {
            throw new IllegalArgumentException(
                "Publication year must be between 0 and " + currentYear);
        }
    }
}
