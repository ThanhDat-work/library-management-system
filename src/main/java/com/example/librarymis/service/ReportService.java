package com.example.librarymis.service;

import com.example.librarymis.model.dto.BookBorrowDTO;
import com.example.librarymis.model.dto.FineReportDTO;
import com.example.librarymis.model.dto.MemberBorrowDTO;
import com.example.librarymis.model.dto.MonthlyBorrowDTO;
import java.util.List;

public interface ReportService {
    List<MonthlyBorrowDTO> monthlyBorrows();
    List<BookBorrowDTO> topBooks();
    List<MemberBorrowDTO> topMembers();
    List<FineReportDTO> fineReports();
}
