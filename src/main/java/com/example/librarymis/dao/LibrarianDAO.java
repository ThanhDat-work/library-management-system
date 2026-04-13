package com.example.librarymis.dao;

import com.example.librarymis.model.entity.Librarian;
import java.util.List;
import java.util.Optional;

// Lấy danh sách tiền phạt theo Member
public interface LibrarianDAO extends BaseDAO<Librarian, Long> {
    // Tìm theo username (unique)
    Optional<Librarian> findByUsername(String username);

    // Tìm kiếm theo tên, username, email
    List<Librarian> search(String keyword);
}
