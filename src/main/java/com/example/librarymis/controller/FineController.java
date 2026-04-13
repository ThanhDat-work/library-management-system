package com.example.librarymis.controller;

import com.example.librarymis.model.entity.Fine;
import com.example.librarymis.service.FineService;
import com.example.librarymis.service.impl.FineServiceImpl;
import java.util.List;

public class FineController {
    private final FineService fineService = new FineServiceImpl();

    public Fine save(Fine fine) {
        // Mục đích: xử lý logic của hàm save.
        return fineService.save(fine);
    }

    public List<Fine> findAll() {
        // Mục đích: xử lý logic của hàm findAll.
        return fineService.getAll();
    }

    public List<Fine> findUnpaid() {
        // Mục đích: xử lý logic của hàm findUnpaid.
        return fineService.getUnpaid();
    }

    public void delete(Long id) {
        // Mục đích: xử lý logic của hàm delete.
        fineService.delete(id);
    }

    public long countUnpaid() {
        // Mục đích: xử lý logic của hàm countUnpaid.
        return fineService.countUnpaid();
    }
}
