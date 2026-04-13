package com.example.librarymis.controller;

import com.example.librarymis.model.entity.Librarian;
import com.example.librarymis.service.LibrarianService;
import com.example.librarymis.service.impl.LibrarianServiceImpl;
import java.util.List;
import java.util.Optional;

public class LibrarianController {
    private final LibrarianService librarianService = new LibrarianServiceImpl();

    public Librarian save(Librarian librarian) {
        // Mục đích: xử lý logic của hàm save.
        return librarianService.save(librarian);
    }

    public List<Librarian> findAll() {
        // Mục đích: xử lý logic của hàm findAll.
        return librarianService.getAll();
    }

    public List<Librarian> search(String keyword) {
        // Mục đích: xử lý logic của hàm search.
        return librarianService.search(keyword);
    }

    public Optional<Librarian> findByUsername(String username) {
        // Mục đích: xử lý logic của hàm findByUsername.
        return librarianService.findByUsername(username);
    }

    public void delete(Long id) {
        // Mục đích: xử lý logic của hàm delete.
        librarianService.delete(id);
    }
}
