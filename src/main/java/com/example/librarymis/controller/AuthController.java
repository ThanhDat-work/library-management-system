package com.example.librarymis.controller;

import com.example.librarymis.model.entity.Librarian;
import com.example.librarymis.service.AuthService;
import com.example.librarymis.service.impl.AuthServiceImpl;
import java.util.Optional;

public class AuthController {
    private final AuthService authService = new AuthServiceImpl();

    public Optional<Librarian> login(String username, String password) {
        // Mục đích: xử lý logic của hàm login.
        return authService.login(username, password);
    }
}
