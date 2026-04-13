package com.example.librarymis.model.dto;

/**
 * DTO dùng để biểu diễn dữ liệu thống kê lượt mượn theo từng tháng.
 */
public class MonthlyBorrowDTO {
    private String monthLabel;
    private Long totalRecords;
    private Long totalBooks;

    public MonthlyBorrowDTO(String monthLabel, Long totalRecords, Long totalBooks) {
        // Khởi tạo DTO thống kê theo tháng.
        this.monthLabel = monthLabel;
        this.totalRecords = totalRecords;
        this.totalBooks = totalBooks;
    }

    public String getMonthLabel() {
        // Lấy nhãn tháng dùng để hiển thị.
        return monthLabel;
    }

    public Long getTotalRecords() {
        // Lấy tổng số phiếu mượn trong tháng.
        return totalRecords;
    }

    public Long getTotalBooks() {
        // Lấy tổng số sách được mượn trong tháng.
        return totalBooks;
    }
}
