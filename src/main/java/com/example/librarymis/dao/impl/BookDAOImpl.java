package com.example.librarymis.dao.impl;

import com.example.librarymis.config.JpaUtil;
import com.example.librarymis.dao.BookDAO;
import com.example.librarymis.model.entity.Book;
import java.util.List;

/**
 * BookDAOImpl:
 * - DAO xử lý riêng cho entity Book
 * - Kế thừa toàn bộ CRUD từ AbstractDAO
 * - Bổ sung các query đặc thù: search, findLowStock
 */
public class BookDAOImpl extends AbstractDAO<Book> implements BookDAO {
    public BookDAOImpl() {
        super(Book.class); // Truyền class Book cho AbstractDAO
    }

    /**
     * Tìm kiếm sách theo từ khóa
     * - Tìm theo: title, author, code, isbn
     * - Không phân biệt hoa thường
     * - Có join fetch để load luôn category + publisher (tránh lazy loading)
     */
    @Override
    public List<Book> search(String keyword) {
        // Mục đích: xử lý logic của hàm search.
        String q = "%" + keyword.toLowerCase() + "%"; // Tạo pattern LIKE
        return JpaUtil.execute(em -> em.createQuery("""
                select b from Book b
                left join fetch b.category
                left join fetch b.publisher
                where lower(b.title) like :q
                   or lower(b.author) like :q
                   or lower(b.code) like :q
                   or lower(coalesce(b.isbn,'')) like :q
                order by b.id desc
                """, Book.class)
                .setParameter("q", q) // Gán tham số tìm kiếm
                .getResultList());
    }

    /**
     * Lấy danh sách sách có số lượng tồn thấp
     *
     * @param threshold ngưỡng (ví dụ <= 5)
     */
    @Override
    public List<Book> findLowStock(int threshold) {
        // Mục đích: xử lý logic của hàm findLowStock.
        return JpaUtil.execute(em -> em.createQuery("""
                select b from Book b
                where b.availableQuantity <= :threshold
                order by b.availableQuantity asc
                """, Book.class)
                .setParameter("threshold", threshold)
                .getResultList());
    }
}
