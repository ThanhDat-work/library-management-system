package com.example.librarymis.dao;

import java.util.List;
import java.util.Optional;

// Generic DAO dùng chung cho tất cả entity
// T: kiểu entity
// ID: kiểu khóa chính (Long, Integer,...)
public interface BaseDAO<T, ID> {
    // Lưu mới hoặc cập nhật entity
    T save(T entity);

    // Tìm theo ID
    Optional<T> findById(ID id);

    // Lấy toàn bộ dữ liệu
    List<T> findAll();

    // Xóa theo ID
    void deleteById(ID id);
}
