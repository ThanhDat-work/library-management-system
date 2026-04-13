package com.example.librarymis.dao.impl;

import com.example.librarymis.config.JpaUtil;
import com.example.librarymis.dao.MembershipTypeDAO;
import com.example.librarymis.model.entity.MembershipType;
import java.util.List;

// DAO xử lý loại thành viên (MembershipType)
public class MembershipTypeDAOImpl extends AbstractDAO<MembershipType> implements MembershipTypeDAO {
    public MembershipTypeDAOImpl() {
        super(MembershipType.class);
    }

    // Tìm kiếm loại thành viên theo tên
    @Override
    public List<MembershipType> search(String keyword) {
        // Mục đích: xử lý logic của hàm search.
        String q = "%" + keyword.toLowerCase() + "%";
        return JpaUtil.execute(em -> em.createQuery("""
                select m from MembershipType m
                where lower(m.name) like :q
                order by m.name
                """, MembershipType.class)
                .setParameter("q", q)
                .getResultList());
    }
}
