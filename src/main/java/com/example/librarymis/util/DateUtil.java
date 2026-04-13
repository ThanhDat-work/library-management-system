package com.example.librarymis.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class xử lý các thao tác liên quan đến ngày tháng.
 * - Parse String -> LocalDate
 * - Format LocalDate -> String
 */
public final class DateUtil {
    /**
     * Format hiển thị cho UI (người dùng)
     * Ví dụ: 07/04/2026
     */
    public static final DateTimeFormatter UI_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    /**
     * Format chuẩn để nhập / lưu DB
     * Ví dụ: 2026-04-07
     */
    public static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Constructor private để ngăn tạo instance
     */
    private DateUtil() {
        // Mục đích: xử lý logic của hàm DateUtil.
    }

    /**
     * Parse chuỗi thành LocalDate
     * Ưu tiên:
     * 1. INPUT_FORMAT (yyyy-MM-dd)
     * 2. Nếu fail → thử UI_FORMAT (dd/MM/yyyy)
     */
    public static LocalDate parse(String value) {
        // Nếu null hoặc rỗng → không parse được
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            // Thử parse theo format chuẩn (DB)
            return LocalDate.parse(value.trim(), INPUT_FORMAT);
        } catch (DateTimeParseException ex) {
            // Nếu fail → thử parse theo format UI
            return LocalDate.parse(value.trim(), UI_FORMAT);
        }
    }

    /**
     * Format LocalDate sang String theo chuẩn DB
     */
    public static String format(LocalDate date) {
        // Nếu null → trả về chuỗi rỗng để tránh NullPointerException
        return date == null ? "" : date.format(INPUT_FORMAT);
    }

    /**
     * Format LocalDate sang String theo chuẩn UI
     */
    public static String formatUi(LocalDate date) {
        // Mục đích: xử lý logic của hàm formatUi.
        return date == null ? "" : date.format(UI_FORMAT);
    }
}
