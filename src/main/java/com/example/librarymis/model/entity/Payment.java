package com.example.librarymis.model.entity;

import com.example.librarymis.model.enumtype.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity lưu thông tin thanh toán các khoản phạt của thành viên.
 */
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "librarian_id", nullable = false)
    private Librarian librarian;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fine_id", nullable = false, unique = true)
    private Fine fine;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDate paymentDate = LocalDate.now();

    @Column(length = 50)
    private String method = "CASH";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status = PaymentStatus.PAID;

    @Column(length = 255)
    private String note;

    public Payment() {
        // Khởi tạo thông tin thanh toán cho khoản phạt.
    }

    public Payment(String code, Member member, Librarian librarian, Fine fine, BigDecimal amount, LocalDate paymentDate,
            String method, PaymentStatus status, String note) {
        // Khởi tạo thông tin thanh toán cho khoản phạt.
        this.code = code;
        this.member = member;
        this.librarian = librarian;
        this.fine = fine;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.method = method;
        this.status = status;
        this.note = note;
    }

    public Long getId() {
        // Lấy mã định danh của thanh toán.
        return id;
    }

    public String getCode() {
        // Lấy mã thanh toán.
        return code;
    }

    public Member getMember() {
        // Lấy thành viên thực hiện thanh toán.
        return member;
    }

    public Librarian getLibrarian() {
        // Lấy thủ thư xác nhận thanh toán.
        return librarian;
    }

    public Fine getFine() {
        // Lấy khoản phạt được thanh toán.
        return fine;
    }

    public BigDecimal getAmount() {
        // Lấy số tiền thanh toán.
        return amount == null ? BigDecimal.ZERO : amount;
    }

    public LocalDate getPaymentDate() {
        // Lấy ngày thanh toán.
        return paymentDate;
    }

    public String getMethod() {
        // Lấy phương thức thanh toán.
        return method == null ? "CASH" : method;
    }

    public PaymentStatus getStatus() {
        // Lấy trạng thái thanh toán.
        return status;
    }

    public String getNote() {
        // Lấy ghi chú thanh toán.
        return note == null ? "" : note;
    }

    public void setCode(String code) {
        // Cập nhật mã thanh toán.
        this.code = code;
    }

    public void setMember(Member member) {
        // Cập nhật thành viên thực hiện thanh toán.
        this.member = member;
    }

    public void setLibrarian(Librarian librarian) {
        // Cập nhật thủ thư xác nhận thanh toán.
        this.librarian = librarian;
    }

    public void setFine(Fine fine) {
        // Cập nhật khoản phạt được thanh toán.
        this.fine = fine;
    }

    public void setAmount(BigDecimal amount) {
        // Cập nhật số tiền thanh toán.
        this.amount = amount;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        // Cập nhật ngày thanh toán.
        this.paymentDate = paymentDate;
    }

    public void setMethod(String method) {
        // Cập nhật phương thức thanh toán.
        this.method = method;
    }

    public void setStatus(PaymentStatus status) {
        // Cập nhật trạng thái thanh toán.
        this.status = status;
    }

    public void setNote(String note) {
        // Cập nhật ghi chú thanh toán.
        this.note = note;
    }

    @Override
    public String toString() {
        // Tạo chuỗi hiển thị ngắn gọn cho giao dịch thanh toán.
        return code + " - " + amount;
    }
}
