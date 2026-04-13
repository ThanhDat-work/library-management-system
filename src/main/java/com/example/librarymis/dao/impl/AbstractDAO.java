package com.example.librarymis.dao.impl;

import com.example.librarymis.config.JpaUtil;
import com.example.librarymis.dao.BaseDAO;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * AbstractDAO:
 * - Lớp DAO tổng quát (generic) cho tất cả entity
 * - Cung cấp các CRUD cơ bản: save, findById, findAll, delete
 * - Các DAO cụ thể (BookDAO, MemberDAO,...) sẽ kế thừa class này
 */
public abstract class AbstractDAO<T> implements BaseDAO<T, Long> {
    private final Class<T> entityClass; // Lưu kiểu entity (Book, Member,...)

    protected AbstractDAO(Class<T> entityClass) {
        // Gán class của entity để dùng trong query
        this.entityClass = entityClass;
    }

    /**
     * Lưu hoặc cập nhật entity
     * - Nếu entity chưa tồn tại (id = null) → persist (INSERT)
     * - Nếu đã tồn tại → merge (UPDATE)
     */
    @Override
    public T save(T entity) {
        // Mục đích: xử lý logic của hàm save.
        return JpaUtil.executeInTransaction(em -> {
            if (isNew(em, entity)) {
                em.persist(entity); // Thêm mới vào DB
                return entity;
            }
            return em.merge(entity); // Cập nhật dữ liệu
        });
    }

    /**
     * Tìm entity theo ID
     */
    @Override
    // Hàm tìm theo ID
    public Optional<T> findById(Long id) {
        // Mục đích: xử lý logic của hàm findById.
        return JpaUtil.execute(em -> Optional.ofNullable(em.find(entityClass, id)));
    }

    /**
     * Lấy toàn bộ danh sách entity
     * - Sắp xếp giảm dần theo id (mới nhất lên đầu)
     */
    @Override
    public List<T> findAll() {
        // Mục đích: xử lý logic của hàm findAll.
        return JpaUtil.execute(
                em -> em.createQuery("from " + entityClass.getSimpleName() + " e order by e.id desc", entityClass)
                        .getResultList());
    }

    /**
     * Xóa entity theo ID
     */
    @Override
    public void deleteById(Long id) {
        // Mục đích: xử lý logic của hàm deleteById.
        JpaUtil.executeInTransaction(em -> {
            T entity = em.find(entityClass, id); // Tìm entity
            if (entity != null) {
                em.remove(entity); // Xóa nếu tồn tại
            }
        });
    }

    /**
     * Tạo EntityManager mới (ít dùng trực tiếp vì đã có JpaUtil)
     */
    protected EntityManager createEntityManager() {
        // Mục đích: xử lý logic của hàm createEntityManager.
        return JpaUtil.createEntityManager();
    }

    /**
     * Kiểm tra entity có phải mới hay không
     * Điều kiện:
     * - Không nằm trong persistence context (em.contains = false)
     * - Và id == null
     */
    private boolean isNew(EntityManager em, T entity) {
        // Mục đích: xử lý logic của hàm isNew.
        return !em.contains(entity) && tryGetId(entity) == null;
    }

    /**
     * Dùng reflection để lấy id của entity
     * - Gọi method getId() nếu tồn tại
     * - Nếu lỗi → trả về null
     */
    private Object tryGetId(T entity) {
        // Mục đích: xử lý logic của hàm tryGetId.
        try {
            // Mục đích: xử lý logic của hàm tryGetId.
            return entity.getClass().getMethod("getId").invoke(entity); // tìm method getId() gọi method đó
        } catch (Exception e) {
            return null;
        }
    }
}
