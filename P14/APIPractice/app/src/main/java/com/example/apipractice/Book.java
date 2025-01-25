package com.example.apipractice;

public class Book {
    private Long id;
    private String title;
    private String author;

    // Constructor for creating new books
    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    // Empty constructor
    public Book() {}

    // Buat getter dan setter sesuai kebutuhan
    // Contoh:
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
}
