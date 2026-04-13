package com.example.librarymis.dao;

import com.example.librarymis.model.entity.Librarian;
import java.util.Optional;

// DAO chuyên xử lý xác thực đăng nhập
public interface AuthDAO {
    // Xác thực username + password
    // Trả về Optional để tránh null nếu không tìm thấy
    Optional<Librarian> authenticate(String username, String password);
}
