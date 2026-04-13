package com.example.librarymis.dao.impl;

import com.example.librarymis.config.JpaUtil;
import com.example.librarymis.dao.FineDAO;
import com.example.librarymis.model.entity.Fine;
import com.example.librarymis.model.entity.Member;
import com.example.librarymis.model.enumtype.PaymentStatus;
import java.util.List;

// DAO xử lý tiền phạt
public class FineDAOImpl extends AbstractDAO<Fine> implements FineDAO {
    // Constructor truyền entity class
    public FineDAOImpl() {
        super(Fine.class);
    }

    // Lấy toàn bộ danh sách tiền phạt
    @Override
    public List<Fine> findAll() {
        // Mục đích: xử lý logic của hàm findAll.
        return JpaUtil.execute(em -> em.createQuery("""
                select f from Fine f
                left join fetch f.member
                left join fetch f.borrowRecord
                order by f.id desc
                """, Fine.class).getResultList());
    }

    // Lấy danh sách tiền phạt chưa thanh toán
    @Override
    public List<Fine> findUnpaidFines() {
        // Mục đích: xử lý logic của hàm findUnpaidFines.
        return JpaUtil.execute(em -> em.createQuery("""
                select f from Fine f
                left join fetch f.member
                left join fetch f.borrowRecord
                where f.paymentStatus = :status
                order by f.createdDate desc
                """, Fine.class)
                .setParameter("status", PaymentStatus.UNPAID)
                .getResultList());
    }

    // Lấy danh sách tiền phạt theo Member
    @Override
    public List<Fine> findByMember(Member member) {
        // Mục đích: xử lý logic của hàm findByMember.
        return JpaUtil.execute(em -> em.createQuery("""
                select f from Fine f
                left join fetch f.member
                left join fetch f.borrowRecord
                where f.member = :member
                order by f.id desc
                """, Fine.class)
                .setParameter("member", member)
                .getResultList());
    }
}
