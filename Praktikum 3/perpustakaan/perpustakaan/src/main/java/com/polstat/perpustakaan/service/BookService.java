/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.polstat.perpustakaan.service;

/**
 *
 * @author Dwinanda
 */
import com.polstat.perpustakaan.dto.BookDto;
import java.util.List;

public interface BookService {

    public void createBook(BookDto bookDto);

    public List<BookDto> getBooks();

     public List<BookDto> searchBooks(String searchTerm);

    public List<BookDto> searchBooks(String title, String author);
}