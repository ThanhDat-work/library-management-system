package com.example.librarymis.dao.impl;

import com.example.librarymis.config.JpaUtil;
import com.example.librarymis.dao.BorrowDAO;
import com.example.librarymis.model.entity.BorrowRecord;
import com.example.librarymis.model.enumtype.BorrowStatus;
import java.time.LocalDate;
import java.util.List;

// DAO xử lý nghiệp vụ liên quan đến mượn sách (BorrowRecord)
public class BorrowDAOImpl extends AbstractDAO<BorrowRecord> implements BorrowDAO {
    // Constructor truyền entity class cho AbstractDAO
    public BorrowDAOImpl() {
        super(BorrowRecord.class);
    }

    // Lấy toàn bộ phiếu mượn (override để fetch đầy đủ quan hệ)
    @Override
    public List<BorrowRecord> findAll() {
        // Sử dụng fetch join để tránh LazyInitializationException
        return JpaUtil.execute(em -> em.createQuery("""
                select distinct br from BorrowRecord br
                left join fetch br.member
                left join fetch br.librarian
                left join fetch br.details d
                left join fetch d.book
                left join fetch br.fine
                order by br.id desc
                """, BorrowRecord.class).getResultList());
    }

    // Lấy các phiếu mượn đang hoạt động (có thể bao gồm cả đã trả/mất tùy logic)
    @Override
    public List<BorrowRecord> findActiveBorrows() {
        return JpaUtil.execute(em -> em.createQuery("""
                select distinct br from BorrowRecord br
                left join fetch br.member
                left join fetch br.librarian
                left join fetch br.details d
                left join fetch d.book
                where br.borrowStatus in (:borrowed, :overdue, :returned, :lost)
                order by br.dueDate asc
                """, BorrowRecord.class)
                // truyền enum làm tham số query
                .setParameter("borrowed", BorrowStatus.BORROWED)
                .setParameter("overdue", BorrowStatus.OVERDUE)
                .setParameter("returned", BorrowStatus.RETURNED)
                .setParameter("lost", BorrowStatus.LOST)
                .getResultList());
    }

    // Lấy danh sách phiếu mượn quá hạn
    @Override
    public List<BorrowRecord> findOverdueRecords(LocalDate date) {
        return JpaUtil.execute(em -> em.createQuery("""
                select br from BorrowRecord br
                left join fetch br.member
                left join fetch br.librarian
                left join fetch br.details d
                left join fetch d.book
                where br.dueDate < :date
                  and br.borrowStatus in (:borrowed, :returned, :overdue, :lost)
                order by br.dueDate asc
                """, BorrowRecord.class)
                .setParameter("date", date)
                .setParameter("borrowed", BorrowStatus.BORROWED)
                .setParameter("overdue", BorrowStatus.OVERDUE)
                .setParameter("returned", BorrowStatus.RETURNED)
                .setParameter("lost", BorrowStatus.LOST) // THÊM DÒNG NÀY
                .getResultList());
    }

    // Lấy danh sách phiếu bị mất sách
    @Override
    public List<BorrowRecord> findLostBorrows() {
        // Mục đích: xử lý logic của hàm findLostBorrows.
        return JpaUtil.execute(em -> em.createQuery("""
                select distinct br from BorrowRecord br
                left join fetch br.member
                left join fetch br.librarian
                left join fetch br.details d
                left join fetch d.book
                where br.borrowStatus = :lost
                order by br.dueDate asc
                """, BorrowRecord.class)
                .setParameter("lost", BorrowStatus.LOST)
                .getResultList());
    }
}
