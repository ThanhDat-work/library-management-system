package com.example.librarymis.service;

import com.example.librarymis.model.entity.Librarian;
import java.util.List;
import java.util.Optional;

public interface LibrarianService {
    Librarian save(Librarian librarian);
    List<Librarian> getAll();
    List<Librarian> search(String keyword);
    Optional<Librarian> findByUsername(String username);
    void delete(Long id);
}
