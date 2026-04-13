package com.example.librarymis.model.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity lưu thông tin tài khoản thủ thư thao tác trong hệ thống.
 */
@Entity
@Table(name = "librarians")
public class Librarian {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String username;

    @Column(nullable = false, length = 120)
    private String password;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Column(length = 150)
    private String email;

    @Column(length = 50)
    private String role = "STAFF";

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "librarian")
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    @OneToMany(mappedBy = "librarian")
    private List<Payment> payments = new ArrayList<>();

    public Librarian() {
        // Khởi tạo thông tin thủ thư.
    }

    public Librarian(String username, String password, String fullName, String email, String role) {
        // Khởi tạo thông tin thủ thư.
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        // Lấy mã định danh của thủ thư.
        return id;
    }

    public String getUsername() {
        // Lấy tên đăng nhập của thủ thư.
        return username;
    }

    public String getPassword() {
        // Lấy mật khẩu đăng nhập của thủ thư.
        return password;
    }

    public String getFullName() {
        // Lấy họ tên thủ thư.
        return fullName;
    }

    public String getEmail() {
        // Lấy email của thủ thư.
        return email == null ? "" : email;
    }

    public String getRole() {
        // Lấy vai trò của thủ thư.
        return role == null ? "STAFF" : role;
    }

    public boolean isActive() {
        // Kiểm tra trạng thái hoạt động của tài khoản thủ thư.
        return active;
    }

    public void setUsername(String username) {
        // Cập nhật tên đăng nhập của thủ thư.
        this.username = username;
    }

    public void setPassword(String password) {
        // Cập nhật mật khẩu đăng nhập của thủ thư.
        this.password = password;
    }

    public void setFullName(String fullName) {
        // Cập nhật họ tên thủ thư.
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        // Cập nhật email của thủ thư.
        this.email = email;
    }

    public void setRole(String role) {
        // Cập nhật vai trò của thủ thư.
        this.role = role;
    }

    public void setActive(boolean active) {
        // Cập nhật trạng thái hoạt động của tài khoản thủ thư.
        this.active = active;
    }

    @Override
    public String toString() {
        // Tạo chuỗi hiển thị ngắn gọn cho thủ thư.
        return fullName + " (" + username + ")";
    }
}
