package com.example.librarymis.controller;

import com.example.librarymis.model.dto.BookBorrowDTO;
import com.example.librarymis.model.dto.FineReportDTO;
import com.example.librarymis.model.dto.MemberBorrowDTO;
import com.example.librarymis.model.dto.MonthlyBorrowDTO;
import com.example.librarymis.service.ReportService;
import com.example.librarymis.service.impl.ReportServiceImpl;
import java.util.List;

public class ReportController {
    private final ReportService reportService = new ReportServiceImpl();

    public List<MonthlyBorrowDTO> monthlyBorrows() {
        // Mục đích: xử lý logic của hàm monthlyBorrows.
        return reportService.monthlyBorrows();
    }

    public List<BookBorrowDTO> topBooks() {
        // Mục đích: xử lý logic của hàm topBooks.
        return reportService.topBooks();
    }

    public List<MemberBorrowDTO> topMembers() {
        // Mục đích: xử lý logic của hàm topMembers.
        return reportService.topMembers();
    }

    public List<FineReportDTO> fineReports() {
        // Mục đích: xử lý logic của hàm fineReports.
        return reportService.fineReports();
    }
}
