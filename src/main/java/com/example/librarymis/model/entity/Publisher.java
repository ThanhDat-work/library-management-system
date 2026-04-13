package com.example.librarymis.model.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity lưu thông tin nhà xuất bản liên kết với sách.
 */
@Entity
@Table(name = "publishers")
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(length = 150)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 250)
    private String address;

    @OneToMany(mappedBy = "publisher")
    private List<Book> books = new ArrayList<>();

    public Publisher() {
        // Khởi tạo thông tin nhà xuất bản.
    }

    public Publisher(String name, String email, String phone, String address) {
        // Khởi tạo thông tin nhà xuất bản.
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public Long getId() {
        // Lấy mã định danh của nhà xuất bản.
        return id;
    }

    public String getName() {
        // Lấy tên nhà xuất bản.
        return name;
    }

    public void setName(String name) {
        // Cập nhật tên nhà xuất bản.
        this.name = name;
    }

    public String getEmail() {
        // Lấy email của nhà xuất bản.
        return email == null ? "" : email;
    }

    public void setEmail(String email) {
        // Cập nhật email của nhà xuất bản.
        this.email = email;
    }

    public String getPhone() {
        // Lấy số điện thoại của nhà xuất bản.
        return phone == null ? "" : phone;
    }

    public void setPhone(String phone) {
        // Cập nhật số điện thoại của nhà xuất bản.
        this.phone = phone;
    }

    public String getAddress() {
        // Lấy địa chỉ của nhà xuất bản.
        return address == null ? "" : address;
    }

    public void setAddress(String address) {
        // Cập nhật địa chỉ của nhà xuất bản.
        this.address = address;
    }

    public List<Book> getBooks() {
        // Lấy danh sách sách do nhà xuất bản phát hành.
        return books;
    }

    @Override
    public String toString() {
        // Tạo chuỗi hiển thị ngắn gọn cho nhà xuất bản.
        return name;
    }
}
