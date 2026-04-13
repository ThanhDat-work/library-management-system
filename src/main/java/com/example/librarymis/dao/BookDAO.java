package com.example.librarymis.dao;

import com.example.librarymis.model.entity.Book;
import java.util.List;

// DAO cho Book
public interface BookDAO extends BaseDAO<Book, Long> {
    // Tìm kiếm sách theo keyword (title, author, code,...)
    List<Book> search(String keyword);

    // Tìm sách sắp hết hàng (availableQuantity <= threshold)
    List<Book> findLowStock(int threshold);
}
