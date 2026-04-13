package com.example.librarymis.dao.impl;

import com.example.librarymis.config.JpaUtil;
import com.example.librarymis.dao.LibrarianDAO;
import com.example.librarymis.model.entity.Librarian;
import java.util.List;
import java.util.Optional;

// DAO xử lý dữ liệu Librarian (thủ thư)
public class LibrarianDAOImpl extends AbstractDAO<Librarian> implements LibrarianDAO {
    // Constructor truyền entity class cho AbstractDAO
    public LibrarianDAOImpl() {
        super(Librarian.class);
    }

    // Tìm Librarian theo username (duy nhất)
    @Override
    public Optional<Librarian> findByUsername(String username) {
        // Query không phân biệt hoa thường
        return JpaUtil.execute(em -> em.createQuery("""
                select l from Librarian l
                where lower(l.username) = lower(:username)
                """, Librarian.class)
                .setParameter("username", username)
                .getResultStream() // stream để tránh exception nếu không có kết quả
                .findFirst()); // lấy 1 record đầu tiên (Optional)
    }

    // Tìm kiếm Librarian theo nhiều trường
    @Override
    public List<Librarian> search(String keyword) {
        // Chuẩn hóa keyword
        String q = "%" + keyword.toLowerCase() + "%";
        return JpaUtil.execute(em -> em.createQuery("""
                select l from Librarian l
                where lower(l.fullName) like :q
                   or lower(l.username) like :q
                   or lower(coalesce(l.email,'')) like :q
                order by l.id desc
                """, Librarian.class)
                .setParameter("q", q)
                .getResultList());
    }
}
