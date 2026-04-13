package com.example.librarymis.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity mô tả loại thành viên cùng các quyền lợi và mức phí tương ứng.
 */
@Entity
@Table(name = "membership_types")
public class MembershipType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer maxBorrowBooks = 3;

    @Column(nullable = false)
    private Integer maxBorrowDays = 14;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal yearlyFee = BigDecimal.ZERO;

    @Column(length = 500)
    private String description;

    @OneToMany(mappedBy = "membershipType")
    private List<Member> members = new ArrayList<>();

    public MembershipType() {
        // Khởi tạo loại thành viên cùng giới hạn mượn sách.
    }

    public MembershipType(String name, Integer maxBorrowBooks, Integer maxBorrowDays, BigDecimal yearlyFee, String description) {
        // Khởi tạo loại thành viên cùng giới hạn mượn sách.
        this.name = name;
        this.maxBorrowBooks = maxBorrowBooks;
        this.maxBorrowDays = maxBorrowDays;
        this.yearlyFee = yearlyFee;
        this.description = description;
    }

    public Long getId() {
        // Lấy mã định danh của loại thành viên.
        return id;
    }

    public String getName() {
        // Lấy tên loại thành viên.
        return name;
    }

    public Integer getMaxBorrowBooks() {
        // Lấy số sách tối đa được mượn.
        return maxBorrowBooks;
    }

    public Integer getMaxBorrowDays() {
        // Lấy số ngày tối đa được mượn.
        return maxBorrowDays;
    }

    public BigDecimal getYearlyFee() {
        // Lấy mức phí hằng năm.
        return yearlyFee;
    }

    public String getDescription() {
        // Lấy mô tả loại thành viên.
        return description == null ? "" : description;
    }

    public void setName(String name) {
        // Cập nhật tên loại thành viên.
        this.name = name;
    }

    public void setMaxBorrowBooks(Integer maxBorrowBooks) {
        // Cập nhật số sách tối đa được mượn.
        this.maxBorrowBooks = maxBorrowBooks;
    }

    public void setMaxBorrowDays(Integer maxBorrowDays) {
        // Cập nhật số ngày tối đa được mượn.
        this.maxBorrowDays = maxBorrowDays;
    }

    public void setYearlyFee(BigDecimal yearlyFee) {
        // Cập nhật mức phí hằng năm.
        this.yearlyFee = yearlyFee;
    }

    public void setDescription(String description) {
        // Cập nhật mô tả loại thành viên.
        this.description = description;
    }

    @Override
    public String toString() {
        // Tạo chuỗi hiển thị ngắn gọn cho loại thành viên.
        return name + " (" + maxBorrowBooks + " sách)";
    }
}
