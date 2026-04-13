package com.example.librarymis.model.enumtype;

/**
 * Enum mô tả tình trạng sách khi thành viên hoàn trả.
 */
public enum ReturnStatus {
    // Sách được trả trong tình trạng bình thường.
    GOOD,

    // Sách được trả nhưng có dấu hiệu hư hỏng.
    DAMAGED,

    // Sách được xác nhận là thất lạc hoặc mất.
    LOST
}
