package com.example.librarymis.util;

import java.math.BigDecimal;

/**
 * Utility class dùng để validate dữ liệu đầu vào
 */
public final class ValidationUtil {

    /**
     * Constructor private → tránh tạo instance
     */
    private ValidationUtil() {
    }

    /**
     * Kiểm tra điều kiện chung
     * Nếu sai → throw exception
     */
    public static void require(boolean condition, String message) {
        // Mục đích: xử lý logic của hàm require.
        if (!condition) {
            // Mục đích: xử lý logic của hàm if.
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Kiểm tra chuỗi không được null hoặc rỗng
     */
    public static void notBlank(String value, String message) {
        // Mục đích: xử lý logic của hàm notBlank.
        require(value != null && !value.trim().isEmpty(), message);
    }

    /**
     * Parse String → int
     * Nếu lỗi → throw exception với message custom
     */
    public static int parseInt(String value, String message) {
        // Mục đích: xử lý logic của hàm parseInt.
        try {
            // Mục đích: xử lý logic của hàm parseInt.
            return Integer.parseInt(value.trim());
        } catch (Exception ex) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Parse String → BigDecimal
     */
    public static BigDecimal parseBigDecimal(String value, String message) {
        // Mục đích: xử lý logic của hàm parseBigDecimal.
        try {
            // Mục đích: xử lý logic của hàm parseBigDecimal.
            return new BigDecimal(value.trim());
        } catch (Exception ex) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validate email cơ bản
     */
    public static void email(String value, String message) {
        // Cho phép null / rỗng (optional field)
        if (value == null || value.isBlank()) {
            // Mục đích: xử lý logic của hàm isBlank.
            return;
        }
        // Check đơn giản (không dùng regex)
        require(value.contains("@") && value.contains("."), message);
    }
}
