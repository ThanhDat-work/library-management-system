package com.example.librarymis.dao;

import com.example.librarymis.model.entity.Member;
import com.example.librarymis.model.entity.Payment;
import com.example.librarymis.model.enumtype.PaymentStatus;
import java.util.List;

// DAO cho Payment (thanh toán)
public interface PaymentDAO extends BaseDAO<Payment, Long> {
    // Lấy danh sách payment theo Member
    List<Payment> findByMember(Member member);

    // Lấy danh sách payment theo Member
    List<Payment> findByStatus(PaymentStatus status);
}
