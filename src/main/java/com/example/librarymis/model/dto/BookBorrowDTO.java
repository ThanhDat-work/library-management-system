package com.example.librarymis.model.dto;

/**
 * DTO dùng để lưu tên sách và tổng số lượt mượn của sách đó trong báo cáo thống kê.
 */
public class BookBorrowDTO {
    private String bookTitle;
    private Long totalBorrowed;

    public BookBorrowDTO(String bookTitle, Long totalBorrowed) {
        // Khởi tạo DTO thống kê số lượt mượn theo sách.
        this.bookTitle = bookTitle;
        this.totalBorrowed = totalBorrowed;
    }

    public String getBookTitle() {
        // Lấy tên sách trong thống kê.
        return bookTitle;
    }

    public Long getTotalBorrowed() {
        // Lấy tổng số lượt mượn của sách.
        return totalBorrowed;
    }
}
