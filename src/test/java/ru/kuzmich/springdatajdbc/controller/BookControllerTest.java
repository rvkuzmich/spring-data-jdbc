package ru.kuzmich.springdatajdbc.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.kuzmich.springdatajdbc.model.Book;
import ru.kuzmich.springdatajdbc.service.BookService;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    private Book book1;
    private Book book2;
    private List<Book> bookList;

    @BeforeEach
    void setUp() {
        book1 = new Book("Book1", "Author1", 1869);
        book1.setId(1L);

        book2 = new Book("Book2", "Author2", 1866);
        book2.setId(2L);

        bookList = Arrays.asList(book1, book2);
    }

    @Test
    void createBook_ShouldReturnCreatedBook() throws Exception {
        when(bookService.createBook(any(Book.class))).thenReturn(book1);

        mockMvc.perform(post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book1)))
            .andExpect(status().isCreated());

        verify(bookService, times(1)).createBook(any(Book.class));
    }

    @Test
    void createBook_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        Book invalidBook = new Book("", "", 2026);
        when(bookService.createBook(any(Book.class)))
            .thenThrow(new IllegalArgumentException("Book title can not be empty"));

        mockMvc.perform(post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBook)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getAllBooks_ShouldReturnListOfBooks() throws Exception {
        when(bookService.getAllBooks()).thenReturn(bookList);

        mockMvc.perform(get("/api/v1/books"))
            .andExpect(status().isOk());

        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void getBookById_WithExistingId_ShouldReturnBook() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(Optional.of(book1));

        mockMvc.perform(get("/api/v1/books/1"))
            .andExpect(status().isOk());

        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    void getBookById_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        when(bookService.getBookById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/books/99"))
            .andExpect(status().isNotFound());

        verify(bookService, times(1)).getBookById(99L);
    }

    @Test
    void updateBook_WithExistingId_ShouldReturnUpdatedBook() throws Exception {
        Book updatedBook = new Book("Updated book", "Updated author", 2026);
        updatedBook.setId(1L);

        when(bookService.updateBook(eq(1L), any(Book.class)))
            .thenReturn(Optional.of(updatedBook));

        mockMvc.perform(put("/api/v1/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook)))
            .andExpect(status().isOk());

        verify(bookService, times(1)).updateBook(eq(1L), any(Book.class));
    }

    @Test
    void updateBook_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        when(bookService.updateBook(eq(99L), any(Book.class)))
            .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/books/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book1)))
            .andExpect(status().isNotFound());

        verify(bookService, times(1)).updateBook(eq(99L), any(Book.class));
    }

    @Test
    void deleteBook_WithExistingId_ShouldReturnOk() throws Exception {
        when(bookService.bookExists(1L)).thenReturn(true);
        when(bookService.deleteBook(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/books/1"))
            .andExpect(status().isOk())
            .andExpect(content().string("Book deleted successfully"));

        verify(bookService, times(1)).bookExists(1L);
        verify(bookService, times(1)).deleteBook(1L);
    }

    @Test
    void deleteBook_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        when(bookService.bookExists(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/books/99"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Book with ID 99 not found"));

        verify(bookService, times(1)).bookExists(99L);
        verify(bookService, never()).deleteBook(anyLong());
    }

    @Test
    void deleteAllBooks_ShouldReturnOk() throws Exception {
        when(bookService.getAllBooks()).thenReturn(bookList);

        mockMvc.perform(delete("/api/v1/books"))
            .andExpect(status().isOk())
            .andExpect(content().string("All books deleted successfully"));

        verify(bookService, times(1)).getAllBooks();
        verify(bookService, times(2)).deleteBook(anyLong());
    }
}