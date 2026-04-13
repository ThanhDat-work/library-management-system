package com.example.librarymis.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity lưu thông tin thành viên đăng ký sử dụng thư viện.
 */
@Entity
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String fullName;

    @Column(unique = true, length = 150)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(nullable = false)
    private LocalDate joinDate = LocalDate.now();

    @Column(nullable = false)
    private LocalDate expiryDate = LocalDate.now().plusYears(1);

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "membership_type_id")
    private MembershipType membershipType;

    @OneToMany(mappedBy = "member")
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Fine> fines = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Payment> payments = new ArrayList<>();

    public Member() {
        // Khởi tạo thông tin thành viên thư viện.
    }

    public Member(String fullName, String email, String phone, String address, LocalDate joinDate, LocalDate expiryDate, MembershipType membershipType) {
        // Khởi tạo thông tin thành viên thư viện.
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.joinDate = joinDate;
        this.expiryDate = expiryDate;
        this.membershipType = membershipType;
    }

    public Long getId() {
        // Lấy mã định danh của thành viên.
        return id;
    }

    public String getFullName() {
        // Lấy họ tên thành viên.
        return fullName;
    }

    public String getEmail() {
        // Lấy email của thành viên.
        return email == null ? "" : email;
    }

    public String getPhone() {
        // Lấy số điện thoại của thành viên.
        return phone == null ? "" : phone;
    }

    public String getAddress() {
        // Lấy địa chỉ của thành viên.
        return address == null ? "" : address;
    }

    public LocalDate getJoinDate() {
        // Lấy ngày tham gia thư viện.
        return joinDate;
    }

    public LocalDate getExpiryDate() {
        // Lấy ngày hết hạn thành viên.
        return expiryDate;
    }

    public boolean isActive() {
        // Kiểm tra trạng thái hoạt động của thành viên.
        return active;
    }

    public MembershipType getMembershipType() {
        // Lấy loại thành viên đang áp dụng.
        return membershipType;
    }

    public void setFullName(String fullName) {
        // Cập nhật họ tên thành viên.
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        // Cập nhật email của thành viên.
        this.email = email;
    }

    public void setPhone(String phone) {
        // Cập nhật số điện thoại của thành viên.
        this.phone = phone;
    }

    public void setAddress(String address) {
        // Cập nhật địa chỉ của thành viên.
        this.address = address;
    }

    public void setJoinDate(LocalDate joinDate) {
        // Cập nhật ngày tham gia thư viện.
        this.joinDate = joinDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        // Cập nhật ngày hết hạn thành viên.
        this.expiryDate = expiryDate;
    }

    public void setActive(boolean active) {
        // Cập nhật trạng thái hoạt động của thành viên.
        this.active = active;
    }

    public void setMembershipType(MembershipType membershipType) {
        // Cập nhật loại thành viên đang áp dụng.
        this.membershipType = membershipType;
    }

    @Override
    public String toString() {
        // Tạo chuỗi hiển thị ngắn gọn cho thành viên.
        return fullName + " - " + (membershipType != null ? membershipType.getName() : "No Type");
    }
}
