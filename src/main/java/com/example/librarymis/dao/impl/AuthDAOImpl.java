package com.example.librarymis.dao.impl;

import com.example.librarymis.config.JpaUtil;
import com.example.librarymis.dao.AuthDAO;
import com.example.librarymis.model.entity.Librarian;
import java.util.Optional;

/**
 * AuthDAOImpl:
 * - Xử lý logic đăng nhập (authentication)
 * - Kiểm tra username + password trong database
 */
public class AuthDAOImpl implements AuthDAO {
    /**
     * Xác thực người dùng
     *
     * @param username tên đăng nhập
     * @param password mật khẩu
     * @return Optional<Librarian> nếu đăng nhập thành công
     */
    @Override
    public Optional<Librarian> authenticate(String username, String password) {
        return JpaUtil.execute(em -> em.createQuery("""
                select l from Librarian l
                where lower(l.username) = lower(:username)
                  and l.password = :password
                  and l.active = true
                """, Librarian.class)
                // Gán tham số username (không phân biệt hoa thường)
                .setParameter("username", username)
                // Gán mật khẩu
                .setParameter("password", password)
                // Trả về stream kết quả
                .getResultStream()
                // Lấy phần tử đầu tiên nếu có
                .findFirst());
    }
}
