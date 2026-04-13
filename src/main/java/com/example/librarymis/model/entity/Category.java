package com.example.librarymis.model.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity lưu thông tin thể loại để phân nhóm sách trong thư viện.
 */
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(length = 500)
    private String description;

    @OneToMany(mappedBy = "category")
    private List<Book> books = new ArrayList<>();

    public Category() {
        // Khởi tạo thể loại sách.
    }

    public Category(String name, String description) {
        // Khởi tạo thể loại sách.
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        // Lấy mã định danh của thể loại.
        return id;
    }

    public String getName() {
        // Lấy tên thể loại.
        return name;
    }

    public void setName(String name) {
        // Cập nhật tên thể loại.
        this.name = name;
    }

    public String getDescription() {
        // Lấy mô tả thể loại.
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        // Cập nhật mô tả thể loại.
        this.description = description;
    }

    public List<Book> getBooks() {
        // Lấy danh sách sách thuộc thể loại.
        return books;
    }

    @Override
    public String toString() {
        // Tạo chuỗi hiển thị ngắn gọn cho thể loại.
        return name;
    }
}
