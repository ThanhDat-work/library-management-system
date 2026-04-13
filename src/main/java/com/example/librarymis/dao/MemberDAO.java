package com.example.librarymis.dao;

import com.example.librarymis.model.entity.Member;
import java.util.List;

// DAO cho Member (độc giả)
public interface MemberDAO extends BaseDAO<Member, Long> {
    // Tìm kiếm member
    List<Member> search(String keyword);

    // Lấy member còn hoạt động
    List<Member> findActiveMembers();
}
