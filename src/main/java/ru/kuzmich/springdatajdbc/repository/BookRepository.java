package ru.kuzmich.springdatajdbc.repository;

import java.util.List;
import java.util.Optional;
import ru.kuzmich.springdatajdbc.model.Book;


public interface BookRepository {

    Book save(Book book);

    List<Book> findAll();

    Optional<Book> findById(Long id);

    Optional<Book> update(Book book);

    boolean deleteById(Long id);

    boolean existsById(Long id);
}
