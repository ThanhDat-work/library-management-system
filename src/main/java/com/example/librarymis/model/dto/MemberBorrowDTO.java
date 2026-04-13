package com.example.librarymis.model.dto;

import java.math.BigDecimal;

/**
 * DTO dùng để thống kê số lượt mượn và tổng tiền phạt theo từng thành viên.
 */
public class MemberBorrowDTO {
    private String memberName;
    private Long totalBorrowed;
    private BigDecimal totalFines;

    public MemberBorrowDTO(String memberName, Long totalBorrowed, BigDecimal totalFines) {
        // Khởi tạo DTO thống kê mượn sách theo thành viên.
        this.memberName = memberName;
        this.totalBorrowed = totalBorrowed;
        this.totalFines = totalFines;
    }

    public String getMemberName() {
        // Lấy tên thành viên trong thống kê.
        return memberName;
    }

    public Long getTotalBorrowed() {
        // Lấy tổng số lượt mượn của thành viên.
        return totalBorrowed;
    }

    public BigDecimal getTotalFines() {
        // Lấy tổng tiền phạt của thành viên.
        return totalFines;
    }
}
