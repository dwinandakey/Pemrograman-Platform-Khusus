package com.polstat.perpustakaan.controller;

import com.polstat.perpustakaan.dto.BookDto;
import com.polstat.perpustakaan.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    // Get All Books
    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        List<BookDto> books = bookService.getBooks();
        return ResponseEntity.ok(books);
    }

    // Create a New Book
    @PostMapping
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookDto bookDto) {
        bookService.createBook(bookDto);
        return new ResponseEntity<>(bookDto, HttpStatus.CREATED);
    }

    // Search Books
    @GetMapping("/search")
    public ResponseEntity<List<BookDto>> searchBooks(@RequestParam String keyword) {
        List<BookDto> books = bookService.searchBooks(keyword);
        return ResponseEntity.ok(books);
    }

    // Update Book (You'll need to modify BookService and BookServiceImpl to support this)
    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookDto bookDto) {
        // Implementasi update book akan bergantung pada modifikasi BookService
        bookDto.setId(id);
        bookService.updateBook(bookDto);
        return ResponseEntity.ok(bookDto);
    }

    // Delete Book (You'll need to modify BookService and BookServiceImpl to support this)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        // Implementasi delete book akan bergantung pada modifikasi BookService
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}