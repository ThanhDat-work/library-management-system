package com.example.librarymis.dao;

import com.example.librarymis.model.dto.BookBorrowDTO;
import com.example.librarymis.model.dto.FineReportDTO;
import com.example.librarymis.model.dto.MemberBorrowDTO;
import com.example.librarymis.model.dto.MonthlyBorrowDTO;
import java.util.List;

// DAO chuyên xử lý báo cáo (không CRUD trực tiếp entity)
public interface ReportDAO {
    // DAO chuyên xử lý báo cáo (không CRUD trực tiếp entity)
    List<MonthlyBorrowDTO> getMonthlyBorrowReport();

    // Báo cáo số lượt mượn theo tháng
    List<BookBorrowDTO> getTopBorrowedBooks();

    // Top member mượn nhiều + tiền phạt
    List<MemberBorrowDTO> getTopMembers();

    // Báo cáo tiền phạt
    List<FineReportDTO> getFineReports();
}
