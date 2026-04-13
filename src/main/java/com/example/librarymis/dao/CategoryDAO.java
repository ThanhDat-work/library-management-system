package com.example.librarymis.dao;

import com.example.librarymis.model.entity.Category;
import java.util.List;

// DAO cho Category (thể loại sách)
public interface CategoryDAO extends BaseDAO<Category, Long> {
    // Tìm kiếm theo tên thể loại
    List<Category> searchByName(String keyword);
}
