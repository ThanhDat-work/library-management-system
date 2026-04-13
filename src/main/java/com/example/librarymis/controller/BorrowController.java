package com.example.librarymis.controller;

import com.example.librarymis.model.entity.BorrowDetail;
import com.example.librarymis.model.entity.BorrowRecord;
import com.example.librarymis.service.BorrowService;
import com.example.librarymis.service.impl.BorrowServiceImpl;
import java.util.List;

public class BorrowController {
    private final BorrowService borrowService = new BorrowServiceImpl();

    public BorrowRecord save(BorrowRecord record) {
        // Mục đích: xử lý logic của hàm save.
        return borrowService.save(record);
    }

    public BorrowRecord markAsLost(Long borrowRecordId, List<BorrowDetail> lostDetailsWithCompensation) {
        // Mục đích: xử lý logic của hàm markAsLost với bồi thường.
        return borrowService.markAsLost(borrowRecordId, lostDetailsWithCompensation);
    }

    public BorrowRecord returnBook(Long borrowRecordId, List<BorrowDetail> updatedDetails) {
        // Mục đích: xử lý logic của hàm returnBook.
        return borrowService.returnBook(borrowRecordId, updatedDetails);
    }

    public List<BorrowRecord> findAll() {
        // Mục đích: xử lý logic của hàm findAll.
        return borrowService.getAll();
    }

    public List<BorrowRecord> findActive() {
        // Mục đích: xử lý logic của hàm findActive.
        return borrowService.getActive();
    }

    public List<BorrowRecord> findOverdue() {
        // Mục đích: xử lý logic của hàm findOverdue.
        return borrowService.getOverdue();
    }

    public List<BorrowRecord> findLost() {
        // Mục đích: xử lý logic của hàm findLost.
        return borrowService.getLost();
    }

    public void delete(Long id) {
        // Mục đích: xử lý logic của hàm delete.
        borrowService.delete(id);
    }

    public long countActive() {
        // Mục đích: xử lý logic của hàm countActive.
        return borrowService.countActive();
    }
}
