package com.example.librarymis.controller;

import com.example.librarymis.model.entity.Category;
import com.example.librarymis.service.CategoryService;
import com.example.librarymis.service.impl.CategoryServiceImpl;
import java.util.List;

public class CategoryController {
    private final CategoryService categoryService = new CategoryServiceImpl();

    public Category save(Category category) {
        // Mục đích: xử lý logic của hàm save.
        return categoryService.save(category);
    }

    public List<Category> findAll() {
        // Mục đích: xử lý logic của hàm findAll.
        return categoryService.getAll();
    }

    public List<Category> search(String keyword) {
        // Mục đích: xử lý logic của hàm search.
        return categoryService.search(keyword);
    }

    public void delete(Long id) {
        // Mục đích: xử lý logic của hàm delete.
        categoryService.delete(id);
    }

    public long count() {
        // Mục đích: xử lý logic của hàm count.
        return categoryService.count();
    }
}
