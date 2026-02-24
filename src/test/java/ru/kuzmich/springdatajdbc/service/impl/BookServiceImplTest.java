package ru.kuzmich.springdatajdbc.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kuzmich.springdatajdbc.model.Book;
import ru.kuzmich.springdatajdbc.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book1;
    private Book book2;
    private Book invalidBook;

    @BeforeEach
    void setUp() {
        book1 = new Book("Book1", "Author1", 1869);
        book1.setId(1L);

        book2 = new Book("Book2", "Author2", 1866);
        book2.setId(2L);

        invalidBook = new Book("", "", 2026);
    }

    @Test
    void createBook_ShouldReturnCreatedBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(book1);

        Book createdBook = bookService.createBook(book1);

        assertThat(createdBook).isNotNull();
        assertThat(createdBook.getId()).isEqualTo(1L);
        assertThat(createdBook.getTitle()).isEqualTo("Book1");
        assertThat(createdBook.getAuthor()).isEqualTo("Author1");
        assertThat(createdBook.getPublicationYear()).isEqualTo(1869);

        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void createBook_WithInvalidData_ShouldThrowException() {
        assertThatThrownBy(() -> bookService.createBook(invalidBook))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Book title can not be empty");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void getAllBooks_ShouldReturnListOfBooks() {
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        when(bookRepository.findAll()).thenReturn(expectedBooks);

        List<Book> actualBooks = bookService.getAllBooks();

        assertThat(actualBooks).hasSize(2);
        assertThat(actualBooks).containsExactly(book1, book2);
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getBookById_WithExistingId_ShouldReturnBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));

        Optional<Book> foundBook = bookService.getBookById(1L);

        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("Book1");
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void getBookById_WithNonExistingId_ShouldReturnEmptyOptional() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Book> foundBook = bookService.getBookById(99L);

        assertThat(foundBook).isEmpty();
        verify(bookRepository, times(1)).findById(99L);
    }

    @Test
    void updateBook_WithExistingId_ShouldReturnUpdatedBook() {
        Book updatedDetails = new Book("Book1 updated", "Author1", 1870);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(bookRepository.update(any(Book.class))).thenReturn(Optional.of(updatedDetails));

        Optional<Book> updatedBook = bookService.updateBook(1L, updatedDetails);

        assertThat(updatedBook).isPresent();
        assertThat(updatedBook.get().getTitle()).isEqualTo("Book1 updated");
        assertThat(updatedBook.get().getPublicationYear()).isEqualTo(1870);

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).update(any(Book.class));
    }

    @Test
    void updateBook_WithNonExistingId_ShouldReturnEmptyOptional() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Book> updatedBook = bookService.updateBook(99L, book1);

        assertThat(updatedBook).isEmpty();
        verify(bookRepository, times(1)).findById(99L);
        verify(bookRepository, never()).update(any(Book.class));
    }

    @Test
    void updateBook_WithInvalidData_ShouldThrowException() {
        Book invalidUpdate = new Book("", "", 2025);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));

        assertThatThrownBy(() -> bookService.updateBook(1L, invalidUpdate))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Book title can not be empty");

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, never()).update(any(Book.class));
    }

    @Test
    void deleteBook_WithExistingId_ShouldReturnTrue() {
        when(bookRepository.deleteById(1L)).thenReturn(true);

        boolean result = bookService.deleteBook(1L);

        assertThat(result).isTrue();
        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteBook_WithNonExistingId_ShouldReturnFalse() {
        when(bookRepository.deleteById(99L)).thenReturn(false);

        boolean result = bookService.deleteBook(99L);

        assertThat(result).isFalse();
        verify(bookRepository, times(1)).deleteById(99L);
    }

    @Test
    void bookExists_WithExistingId_ShouldReturnTrue() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        boolean result = bookService.bookExists(1L);

        assertThat(result).isTrue();
        verify(bookRepository, times(1)).existsById(1L);
    }

    @Test
    void bookExists_WithNonExistingId_ShouldReturnFalse() {
        when(bookRepository.existsById(99L)).thenReturn(false);

        boolean result = bookService.bookExists(99L);

        assertThat(result).isFalse();
        verify(bookRepository, times(1)).existsById(99L);
    }
}
