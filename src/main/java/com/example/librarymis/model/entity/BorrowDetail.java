package com.example.librarymis.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entity lưu chi tiết từng đầu sách xuất hiện trong một phiếu mượn.
 */
@Entity
@Table(name = "borrow_details")
public class BorrowDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "borrow_record_id", nullable = false)
    private BorrowRecord borrowRecord;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(nullable = false)
    private Integer returnedQuantity = 0;

    @Column(length = 255)
    private String note;

    @Column(length = 500)
    private String damageDescription;

    private BigDecimal damageCompensationAmount;

    public BorrowDetail() {
        // Khởi tạo chi tiết sách nằm trong phiếu mượn.
    }

    public BorrowDetail(Book book, Integer quantity, String note) {
        // Khởi tạo chi tiết sách nằm trong phiếu mượn.
        this.book = book;
        this.quantity = quantity;
        this.note = note;
    }

    public Long getId() {
        // Lấy mã định danh của chi tiết mượn.
        return id;
    }

    public BorrowRecord getBorrowRecord() {
        // Lấy phiếu mượn chứa chi tiết này.
        return borrowRecord;
    }

    public Book getBook() {
        // Lấy sách được mượn.
        return book;
    }

    public Integer getQuantity() {
        // Lấy số lượng sách mượn.
        return quantity == null ? 0 : quantity;
    }

    public Integer getReturnedQuantity() {
        // Lấy số lượng sách đã trả.
        return returnedQuantity == null ? 0 : returnedQuantity;
    }

    public String getNote() {
        // Lấy ghi chú của chi tiết mượn.
        return note == null ? "" : note;
    }

    public void setId(Long id) {
        // Cập nhật mã định danh của chi tiết mượn.
        this.id = id;
    }

    public void setBorrowRecord(BorrowRecord borrowRecord) {
        // Cập nhật phiếu mượn chứa chi tiết này.
        this.borrowRecord = borrowRecord;
    }

    public void setBook(Book book) {
        // Cập nhật sách được mượn.
        this.book = book;
    }

    public void setQuantity(Integer quantity) {
        // Cập nhật số lượng sách mượn.
        this.quantity = quantity;
    }

    public void setReturnedQuantity(Integer returnedQuantity) {
        // Cập nhật số lượng sách đã trả.
        this.returnedQuantity = returnedQuantity;
    }

    public void setNote(String note) {
        // Cập nhật ghi chú của chi tiết mượn.
        this.note = note;
    }

    public String getDamageDescription() {
        // Lấy mô tả tình trạng hư hỏng của sách.
        return damageDescription == null ? "" : damageDescription;
    }

    public void setDamageDescription(String damageDescription) {
        // Cập nhật mô tả tình trạng hư hỏng của sách.
        this.damageDescription = damageDescription;
    }

    public BigDecimal getDamageCompensationAmount() {
        // Lấy số tiền bồi thường hư hỏng.
        return damageCompensationAmount;
    }

    public void setDamageCompensationAmount(BigDecimal damageCompensationAmount) {
        // Cập nhật số tiền bồi thường hư hỏng.
        this.damageCompensationAmount = damageCompensationAmount;
    }

    @Override
    public String toString() {
        // Tạo chuỗi hiển thị nhanh cho chi tiết mượn.
        return book != null ? book.getTitle() + " x" + quantity : "";
    }
}
