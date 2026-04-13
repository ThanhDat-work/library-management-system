package com.example.librarymis.controller;

import com.example.librarymis.model.entity.Payment;
import com.example.librarymis.model.enumtype.PaymentStatus;
import com.example.librarymis.service.PaymentService;
import com.example.librarymis.service.impl.PaymentServiceImpl;
import java.util.List;

public class PaymentController {
    private final PaymentService paymentService = new PaymentServiceImpl();

    public Payment save(Payment payment) {
        // Mục đích: xử lý logic của hàm save.
        return paymentService.save(payment);
    }

    public List<Payment> findAll() {
        // Mục đích: xử lý logic của hàm findAll.
        return paymentService.getAll();
    }

    public List<Payment> findByStatus(PaymentStatus status) {
        // Mục đích: xử lý logic của hàm findByStatus.
        return paymentService.findByStatus(status);
    }

    public void delete(Long id) {
        // Mục đích: xử lý logic của hàm delete.
        paymentService.delete(id);
    }
}
