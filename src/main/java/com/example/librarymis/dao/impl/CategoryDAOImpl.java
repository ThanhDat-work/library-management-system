package com.example.librarymis.dao.impl;

import com.example.librarymis.config.JpaUtil;
import com.example.librarymis.dao.CategoryDAO;
import com.example.librarymis.model.entity.Category;
import java.util.List;

// DAO xử lý dữ liệu thể loại sách
public class CategoryDAOImpl extends AbstractDAO<Category> implements CategoryDAO {
    // Constructor truyền entity class cho AbstractDAO
    public CategoryDAOImpl() {
        super(Category.class);
    }

    // Constructor truyền entity class cho AbstractDAO
    @Override
    public List<Category> searchByName(String keyword) {
        // Chuẩn hóa keyword về lowercase + thêm wildcard
        String q = "%" + keyword.toLowerCase() + "%";
        return JpaUtil.execute(em -> em.createQuery("""
                select c from Category c
                where lower(c.name) like :q
                order by c.name
                """, Category.class)
                .setParameter("q", q)
                .getResultList());
    }
}
