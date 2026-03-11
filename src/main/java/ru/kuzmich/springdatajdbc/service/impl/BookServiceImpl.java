package ru.kuzmich.springdatajdbc.service.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kuzmich.springdatajdbc.model.Book;
import ru.kuzmich.springdatajdbc.repository.BookRepository;
import ru.kuzmich.springdatajdbc.service.BookService;
import ru.kuzmich.springdatajdbc.utils.BookValidator;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public Book createBook(Book book) {
        BookValidator.validateBook(book);
        return bookRepository.save(book);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Optional<Book> updateBook(Long id, Book bookDetails) {
        Optional<Book> existingBook = bookRepository.findById(id);

        if (existingBook.isPresent()) {
            Book book = existingBook.get();
            book.setTitle(bookDetails.getTitle());
            book.setAuthor(bookDetails.getAuthor());
            book.setPublicationYear(bookDetails.getPublicationYear());

            BookValidator.validateBook(book);

            return bookRepository.update(book);
        }

        return Optional.empty();
    }

    @Override
    public boolean deleteBook(Long id) {
        return bookRepository.deleteById(id);
    }

    @Override
    public boolean bookExists(Long id) {
        return bookRepository.existsById(id);
    }
}
