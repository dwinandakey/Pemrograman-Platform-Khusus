/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.polstat.perpustakaan.service;

/**
 *
 * @author Dwinanda
 */
import com.polstat.perpustakaan.dto.BookDto;
import com.polstat.perpustakaan.entity.Book;
import com.polstat.perpustakaan.mapper.BookMapper;
import com.polstat.perpustakaan.repository.BookRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void createBook(BookDto bookDto) {
        bookRepository.save(BookMapper.mapToBook(bookDto));
    }

    @Override
    public List<BookDto> getBooks() {
        List<Book> books = bookRepository.findAll();
        List<BookDto> bookDtos = books.stream()
                .map((product) -> (BookMapper.mapToBookDto(product)))
                .collect(Collectors.toList());
        return bookDtos;
    }

    @Override
    public List<BookDto> searchBooks(String keyword) {
        List<Book> books = bookRepository.searchByKeyword(keyword);
        return books.stream()
                .map(book -> BookDto.builder()
                        .title(book.getTitle())
                        .author(book.getAuthor())
                        .description(book.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void updateBook(BookDto bookDto) {
        // Cari buku berdasarkan ID
        Book existingBook = bookRepository.findById(bookDto.getId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Update fields
        existingBook.setTitle(bookDto.getTitle());
        existingBook.setAuthor(bookDto.getAuthor());
        existingBook.setDescription(bookDto.getDescription());

        bookRepository.save(existingBook);
    }

    @Override
    public void deleteBook(Long id) {
        // Cari buku berdasarkan ID
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        bookRepository.delete(book);
    }
}
