package com.example.librarymis.service;

import com.example.librarymis.model.entity.BorrowDetail;
import com.example.librarymis.model.entity.BorrowRecord;
import java.util.List;

public interface BorrowService {
    BorrowRecord issueBorrow(BorrowRecord record);

    BorrowRecord save(BorrowRecord record);

    // BorrowRecord returnBorrow(Long borrowId); // Replaced by returnBook with
    // details

    BorrowRecord markAsLost(Long borrowRecordId, List<BorrowDetail> lostDetailsWithCompensation);

    BorrowRecord returnBook(Long borrowRecordId, List<BorrowDetail> updatedDetails);

    List<BorrowRecord> getAll();

    List<BorrowRecord> getActive();

    List<BorrowRecord> getOverdue();

    List<BorrowRecord> getLost();

    void delete(Long id);

    long countActive();
}
