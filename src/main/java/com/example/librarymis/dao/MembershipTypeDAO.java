package com.example.librarymis.dao;

import com.example.librarymis.model.entity.MembershipType;
import java.util.List;

// DAO cho loại thành viên
public interface MembershipTypeDAO extends BaseDAO<MembershipType, Long> {
    // Tìm kiếm theo tên loại
    List<MembershipType> search(String keyword);
}
