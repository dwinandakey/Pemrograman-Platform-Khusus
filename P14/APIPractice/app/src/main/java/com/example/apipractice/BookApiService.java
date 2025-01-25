package com.example.apipractice;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface BookApiService {
    @GET("/api/books")
    Call<List<Book>> getBooks();

    @GET("/api/books/{id}")
    Call<Book> getBookById(@Path("id") Long id);

    @POST("/api/books")
    Call<Book> createBook(@Body Book book);

    @PUT("/api/books/{id}")
    Call<Book> updateBook(@Path("id") Long id, @Body Book book);

    @DELETE("/api/books/{id}")
    Call<Void> deleteBook(@Path("id") Long id);

    @GET("/api/books/search")
    Call<List<Book>> searchBooks(@Query("keyword") String keyword);
}