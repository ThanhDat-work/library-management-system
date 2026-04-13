package com.example.librarymis.dao;

import com.example.librarymis.model.entity.Fine;
import com.example.librarymis.model.entity.Member;
import java.util.List;

// DAO cho Fine (tiền phạt)
public interface FineDAO extends BaseDAO<Fine, Long> {
    // Lấy danh sách tiền phạt chưa thanh toán
    List<Fine> findUnpaidFines();

    // Lấy danh sách tiền phạt theo Member
    List<Fine> findByMember(Member member);
}
