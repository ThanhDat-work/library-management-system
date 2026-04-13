package com.example.librarymis.model.enumtype;

/**
 * Enum mô tả trạng thái tổng quát của phiếu mượn.
 */
public enum BorrowStatus {
    // Phiếu mượn đang còn sách chưa được trả hết.
    BORROWED,

    // Toàn bộ sách trong phiếu đã được trả.
    RETURNED,

    // Phiếu mượn đã quá hạn trả sách.
    OVERDUE,

    // Có sách trong phiếu được xác định là mất.
    LOST
}
