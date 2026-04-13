package com.example.librarymis.dao;

import com.example.librarymis.model.entity.Publisher;
import java.util.List;

// DAO cho nhà xuất bản
public interface PublisherDAO extends BaseDAO<Publisher, Long> {
    // Tìm kiếm theo tên publisher
    List<Publisher> searchByName(String keyword);
}
