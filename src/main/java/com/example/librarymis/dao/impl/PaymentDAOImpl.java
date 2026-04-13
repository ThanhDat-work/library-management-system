package com.example.librarymis.dao.impl;

import com.example.librarymis.config.JpaUtil;
import com.example.librarymis.dao.PaymentDAO;
import com.example.librarymis.model.entity.Member;
import com.example.librarymis.model.entity.Payment;
import com.example.librarymis.model.enumtype.PaymentStatus;
import java.util.List;

// DAO xử lý nghiệp vụ thanh toán (Payment)
public class PaymentDAOImpl extends AbstractDAO<Payment> implements PaymentDAO {
    // Constructor truyền entity class cho AbstractDAO
    public PaymentDAOImpl() {
        super(Payment.class);
    }

    // Lấy toàn bộ danh sách thanh toán
    @Override
    public List<Payment> findAll() {
        // fetch các quan hệ để tránh lazy loading
        return JpaUtil.execute(em -> em.createQuery("""
                select p from Payment p
                left join fetch p.member
                left join fetch p.librarian
                left join fetch p.fine
                order by p.id desc
                """, Payment.class).getResultList());
    }

    // Lấy danh sách thanh toán theo Member
    @Override
    public List<Payment> findByMember(Member member) {
        // Mục đích: xử lý logic của hàm findByMember.
        return JpaUtil.execute(em -> em.createQuery("""
                select p from Payment p
                left join fetch p.member
                left join fetch p.librarian
                left join fetch p.fine
                where p.member = :member
                order by p.id desc
                """, Payment.class)
                .setParameter("member", member)
                .getResultList());
    }

    // Lấy danh sách thanh toán theo trạng thái
    @Override
    public List<Payment> findByStatus(PaymentStatus status) {
        // Mục đích: xử lý logic của hàm findByStatus.
        return JpaUtil.execute(em -> em.createQuery("""
                select p from Payment p
                left join fetch p.member
                left join fetch p.librarian
                left join fetch p.fine
                where p.status = :status
                order by p.id desc
                """, Payment.class)
                .setParameter("status", status)
                .getResultList());
    }
}
