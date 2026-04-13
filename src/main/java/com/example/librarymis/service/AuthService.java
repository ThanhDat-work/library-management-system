package com.example.librarymis.service;

import com.example.librarymis.model.entity.Librarian;
import java.util.Optional;

public interface AuthService {
    Optional<Librarian> login(String username, String password);
}
