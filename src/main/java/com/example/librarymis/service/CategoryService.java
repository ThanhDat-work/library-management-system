package com.example.librarymis.service;

import com.example.librarymis.model.entity.Category;
import java.util.List;

public interface CategoryService {
    Category save(Category category);
    List<Category> getAll();
    List<Category> search(String keyword);
    void delete(Long id);
    long count();
}
