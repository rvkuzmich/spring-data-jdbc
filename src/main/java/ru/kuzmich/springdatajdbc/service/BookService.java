package ru.kuzmich.springdatajdbc.service;

import java.util.List;
import java.util.Optional;
import ru.kuzmich.springdatajdbc.model.Book;

public interface BookService {

    Book createBook(Book book);

    List<Book> getAllBooks();

    Optional<Book> getBookById(Long id);

    Optional<Book> updateBook(Long id, Book bookDetails);

    boolean deleteBook(Long id);

    boolean bookExists(Long id);
}
