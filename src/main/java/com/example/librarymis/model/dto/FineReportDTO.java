package com.example.librarymis.model.dto;

import com.example.librarymis.model.enumtype.PaymentStatus;
import java.math.BigDecimal;

/**
 * DTO dùng để tổng hợp thông tin tiền phạt của thành viên phục vụ việc hiển thị báo cáo.
 */
public class FineReportDTO {
    private String memberName;
    private String borrowCode;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;

    public FineReportDTO(String memberName, String borrowCode, BigDecimal amount, PaymentStatus paymentStatus) {
        // Khởi tạo DTO dùng cho báo cáo tiền phạt.
        this.memberName = memberName;
        this.borrowCode = borrowCode;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
    }

    public String getMemberName() {
        // Lấy tên thành viên bị phạt.
        return memberName;
    }

    public String getBorrowCode() {
        // Lấy mã phiếu mượn liên quan.
        return borrowCode;
    }

    public BigDecimal getAmount() {
        // Lấy số tiền phạt.
        return amount;
    }

    public PaymentStatus getPaymentStatus() {
        // Lấy trạng thái thanh toán của khoản phạt.
        return paymentStatus;
    }
}
