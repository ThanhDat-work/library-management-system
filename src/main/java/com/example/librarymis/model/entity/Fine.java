package com.example.librarymis.model.entity;

import com.example.librarymis.model.enumtype.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity lưu thông tin tiền phạt phát sinh từ việc trả sách trễ, hỏng hoặc mất.
 */
@Entity
@Table(name = "fines")
public class Fine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "borrow_record_id", nullable = false)
    private BorrowRecord borrowRecord;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(nullable = true, length = 255)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Column(nullable = false)
    private LocalDate createdDate = LocalDate.now();

    public Fine() {
        // Khởi tạo khoản tiền phạt cho một phiếu mượn.

    }

    public Fine(String code, BorrowRecord borrowRecord, Member member, BigDecimal amount, String reason,
            PaymentStatus paymentStatus) {
        // Khởi tạo khoản tiền phạt cho một phiếu mượn.
        this.code = code;
        this.borrowRecord = borrowRecord;
        this.member = member;
        this.amount = amount;
        this.reason = reason;
        this.paymentStatus = paymentStatus;
    }

    public Long getId() {
        // Lấy mã định danh của khoản phạt.
        return id;
    }

    public String getCode() {
        // Lấy mã khoản phạt.
        return code;
    }

    public BorrowRecord getBorrowRecord() {
        // Lấy phiếu mượn phát sinh tiền phạt.
        return borrowRecord;
    }

    public Member getMember() {
        // Lấy thành viên bị phạt.
        return member;
    }

    public BigDecimal getAmount() {
        // Lấy số tiền phạt.
        return amount == null ? BigDecimal.ZERO : amount;
    }

    public String getReason() {
        // Lấy lý do bị phạt.
        return reason == null ? "" : reason;
    }

    public PaymentStatus getPaymentStatus() {
        // Lấy trạng thái thanh toán của khoản phạt.
        return paymentStatus;
    }

    public LocalDate getCreatedDate() {
        // Lấy ngày tạo khoản phạt.
        return createdDate;
    }

    public void setCode(String code) {
        // Cập nhật mã khoản phạt.
        this.code = code;
    }

    public void setBorrowRecord(BorrowRecord borrowRecord) {
        // Cập nhật phiếu mượn phát sinh tiền phạt.
        this.borrowRecord = borrowRecord;
    }

    public void setMember(Member member) {
        // Cập nhật thành viên bị phạt.
        this.member = member;
    }

    public void setAmount(BigDecimal amount) {
        // Cập nhật số tiền phạt.
        this.amount = amount;
    }

    public void setReason(String reason) {
        // Cập nhật lý do bị phạt.
        this.reason = reason;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        // Cập nhật trạng thái thanh toán của khoản phạt.
        this.paymentStatus = paymentStatus;
    }

    public void setCreatedDate(LocalDate createdDate) {
        // Cập nhật ngày tạo khoản phạt.
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        // Tạo chuỗi hiển thị ngắn gọn cho khoản phạt.
        return code + " - " + amount;
    }
}
