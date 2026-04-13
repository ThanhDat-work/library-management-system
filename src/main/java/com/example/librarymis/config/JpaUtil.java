package com.example.librarymis.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility class dùng để quản lý JPA:
 * - Tạo EntityManager
 * - Quản lý transaction
 * - Thực thi query an toàn
 */
public final class JpaUtil {
    /**
     * EntityManagerFactory là đối tượng nặng (heavyweight)
     * → chỉ tạo 1 lần duy nhất cho toàn ứng dụng
     */
    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("libraryPU");

    /**
     * Constructor private để ngăn tạo instance
     */
    private JpaUtil() {
        // Mục đích: xử lý logic của hàm JpaUtil.
    }

    /**
     * Tạo EntityManager mới
     * → lightweight, tạo nhiều lần được
     */
    public static EntityManager createEntityManager() {
        // Mục đích: xử lý logic của hàm createEntityManager.
        return EMF.createEntityManager();
    }

    /**
     * Execute logic KHÔNG có transaction
     * Dùng cho query SELECT
     */
    public static <T> T execute(Function<EntityManager, T> action) {
        // Mục đích: xử lý logic của hàm execute.
        EntityManager em = createEntityManager();
        try {
            // Thực thi logic truyền vào
            return action.apply(em);
        } finally {
            // Luôn đóng EntityManager để tránh leak
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Execute logic CÓ transaction (void)
     * Dùng cho INSERT / UPDATE / DELETE
     */
    public static void executeInTransaction(Consumer<EntityManager> action) {
        // Mục đích: xử lý logic của hàm executeInTransaction.
        EntityManager em = createEntityManager();
        try {
            // Bắt đầu transaction
            em.getTransaction().begin();

            // Thực thi logic
            action.accept(em);

            // Commit nếu không lỗi
            em.getTransaction().commit();
        } catch (Exception e) {
            // Nếu lỗi → rollback
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e; // ném lại lỗi
        } finally {
            // Đóng EntityManager
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Execute logic CÓ transaction (có return)
     */
    public static <T> T executeInTransaction(Function<EntityManager, T> action) {
        // Mục đích: xử lý logic của hàm executeInTransaction.
        EntityManager em = createEntityManager();
        try {
            em.getTransaction().begin();
            // Thực thi và lấy kết quả
            T result = action.apply(em);
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Đóng EntityManagerFactory khi shutdown app
     * → tránh memory leak
     */
    public static void shutdown() {
        // Mục đích: xử lý logic của hàm shutdown.
        if (EMF.isOpen()) {
            // Mục đích: xử lý logic của hàm isOpen.
            EMF.close();
        }
    }
}
