package com.example.librarymis.dao.impl;

import com.example.librarymis.config.JpaUtil;
import com.example.librarymis.dao.PublisherDAO;
import com.example.librarymis.model.entity.Publisher;
import java.util.List;

// DAO xử lý dữ liệu nhà xuất bản
public class PublisherDAOImpl extends AbstractDAO<Publisher> implements PublisherDAO {
    public PublisherDAOImpl() {
        super(Publisher.class);
    }

    // DAO xử lý dữ liệu nhà xuất bản
    @Override
    public List<Publisher> searchByName(String keyword) {
        // Mục đích: xử lý logic của hàm searchByName.
        String q = "%" + keyword.toLowerCase() + "%";
        return JpaUtil.execute(em -> em.createQuery("""
                select p from Publisher p
                where lower(p.name) like :q
                order by p.name
                """, Publisher.class)
                .setParameter("q", q)
                .getResultList());
    }
}
