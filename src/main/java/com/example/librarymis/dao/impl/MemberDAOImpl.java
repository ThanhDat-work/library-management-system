package com.example.librarymis.dao.impl;

import com.example.librarymis.config.JpaUtil;
import com.example.librarymis.dao.MemberDAO;
import com.example.librarymis.model.entity.Member;
import java.util.List;

// DAO xử lý dữ liệu Member (độc giả)
public class MemberDAOImpl extends AbstractDAO<Member> implements MemberDAO {
    public MemberDAOImpl() {
        super(Member.class);
    }

    // Tìm kiếm Member theo nhiều tiêu chí
    @Override
    public List<Member> search(String keyword) {
        // Mục đích: xử lý logic của hàm search.
        String q = "%" + keyword.toLowerCase() + "%";
        return JpaUtil.execute(em -> em.createQuery("""
                select m from Member m
                left join fetch m.membershipType
                where lower(m.fullName) like :q
                   or lower(coalesce(m.email,'')) like :q
                   or lower(coalesce(m.phone,'')) like :q
                order by m.id desc
                """, Member.class)
                .setParameter("q", q)
                .getResultList());
    }

    // Lấy danh sách Member đang hoạt động
    @Override
    public List<Member> findActiveMembers() {
        // Mục đích: xử lý logic của hàm findActiveMembers.
        return JpaUtil.execute(em -> em.createQuery("""
                select m from Member m
                left join fetch m.membershipType
                where m.active = true
                order by m.fullName
                """, Member.class).getResultList());
    }
}
