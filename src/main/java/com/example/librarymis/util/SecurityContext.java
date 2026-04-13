package com.example.librarymis.util;

import com.example.librarymis.model.entity.Librarian;

/**
 * Quản lý thông tin đăng nhập và quyền hạn trong phiên làm việc hiện tại.
 */
public class SecurityContext {
    /**
     * Lưu user đang đăng nhập
     */
    private static Librarian currentUser;

    /**
     * Set user hiện tại sau khi login
     */
    public static void setCurrentUser(Librarian user) {
        currentUser = user;
    }

    /**
     * Get user hiện tại
     */

    public static Librarian getCurrentUser() {
        return currentUser;
    }

    /**
     * Kiểm tra user có phải ADMIN không
     */
    public static boolean isAdmin() {
        return currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());
    }

    /**
     * Logout → xóa user khỏi context
     */
    public static void logout() {
        currentUser = null;
    }
}