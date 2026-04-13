package com.example.librarymis.service;

import com.example.librarymis.model.entity.Book;
import java.util.List;

public interface BookService {
    Book save(Book book);

    List<Book> getAll();

    List<Book> search(String keyword);

    List<Book> getLowStock(int threshold);

    void delete(Long id);

    long count();

    Book getById(Long id);
}
