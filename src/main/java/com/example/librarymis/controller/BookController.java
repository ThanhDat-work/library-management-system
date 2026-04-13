package com.example.librarymis.controller;

import com.example.librarymis.model.entity.Book;
import com.example.librarymis.service.BookService;
import com.example.librarymis.service.impl.BookServiceImpl;
import java.util.List;

public class BookController {
    private final BookService bookService = new BookServiceImpl();

    public Book save(Book book) {
        // Mục đích: xử lý logic của hàm save.
        return bookService.save(book);
    }

    public List<Book> findAll() {
        // Mục đích: xử lý logic của hàm findAll.
        return bookService.getAll();
    }

    public List<Book> search(String keyword) {
        // Mục đích: xử lý logic của hàm search.
        return bookService.search(keyword);
    }

    public List<Book> lowStock(int threshold) {
        // Mục đích: xử lý logic của hàm lowStock.
        return bookService.getLowStock(threshold);
    }

    public void delete(Long id) {
        // Mục đích: xử lý logic của hàm delete.
        bookService.delete(id);
    }

    public long count() {
        // Mục đích: xử lý logic của hàm count.
        return bookService.count();
    }

    public Book findById(Long id) {
        // Mục đích: xử lý logic của hàm findById.
        return bookService.getById(id);
    }
}
