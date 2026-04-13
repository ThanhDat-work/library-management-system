package com.example.librarymis.view.panel.common;

/**
 * Interface dành cho các panel cần reload dữ liệu
 * khi được hiển thị lại (ví dụ: khi chuyển tab/menu).
 */
public interface Reloadable {
    /**
     * Phương thức reload dữ liệu.
     * Thường được gọi khi:
     * - Người dùng quay lại panel
     * - Cần cập nhật dữ liệu mới từ database
     */
    void reloadData();
}
