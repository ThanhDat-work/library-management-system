package com.example.librarymis.service;

import com.example.librarymis.model.entity.Payment;
import com.example.librarymis.model.enumtype.PaymentStatus;
import java.util.List;

public interface PaymentService {
    Payment save(Payment payment);
    List<Payment> getAll();
    List<Payment> findByStatus(PaymentStatus status);
    void delete(Long id);
}
