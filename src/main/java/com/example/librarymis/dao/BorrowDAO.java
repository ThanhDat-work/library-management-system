package com.example.librarymis.dao;

import com.example.librarymis.model.entity.BorrowRecord;
import java.time.LocalDate;
import java.util.List;

// DAO cho BorrowRecord (phiếu mượn)
public interface BorrowDAO extends BaseDAO<BorrowRecord, Long> {
    // Lấy danh sách phiếu mượn đang hoạt động
    List<BorrowRecord> findActiveBorrows();

    // Lấy danh sách phiếu quá hạn theo ngày
    List<BorrowRecord> findOverdueRecords(LocalDate date);

    // Lấy danh sách phiếu bị mất sách
    List<BorrowRecord> findLostBorrows();
}
