package com.example.librarymis.service.impl;

// Import các lớp DAO hỗ trợ truy vấn báo cáo
import java.util.List;

import com.example.librarymis.dao.ReportDAO;
import com.example.librarymis.dao.impl.ReportDAOImpl;
import com.example.librarymis.model.dto.BookBorrowDTO;
import com.example.librarymis.model.dto.FineReportDTO;
import com.example.librarymis.model.dto.MemberBorrowDTO;
import com.example.librarymis.model.dto.MonthlyBorrowDTO;
import com.example.librarymis.service.ReportService;

/**
 * Lớp ReportServiceImpl chịu trách nhiệm cung cấp các số liệu thống kê cho hệ thống.
 * Nó không thao tác trên một Entity đơn lẻ mà thường thực hiện các câu lệnh SQL phức tạp (JOIN, GROUP BY) 
 * thông qua DAO để lấy dữ liệu tổng hợp.
 */
public class ReportServiceImpl implements ReportService {
    
    /**
     * Khởi tạo đối tượng ReportDAO để thực hiện các truy vấn thống kê đặc thù.
     */
    private final ReportDAO reportDAO = new ReportDAOImpl();

    /**
     * Lấy báo cáo số lượng lượt mượn sách theo từng tháng.
     * Thường dùng để vẽ biểu đồ tăng trưởng (Line Chart) trên Dashboard.
     * @return Danh sách các đối tượng MonthlyBorrowDTO chứa (Tháng/Năm, Tổng lượt mượn).
     */
    @Override
    public List<MonthlyBorrowDTO> monthlyBorrows() {
        return reportDAO.getMonthlyBorrowReport();
    }

    /**
     * Thống kê các đầu sách được mượn nhiều nhất.
     * Giúp thủ thư biết được xu hướng đọc sách của độc giả để có kế hoạch nhập thêm sách.
     * @return Danh sách BookBorrowDTO chứa (Tên sách, Số lần được mượn).
     */
    @Override
    public List<BookBorrowDTO> topBooks() {
        return reportDAO.getTopBorrowedBooks();
    }

    /**
     * Thống kê các thành viên tích cực nhất (mượn nhiều sách nhất).
     * Dùng để vinh danh hoặc tặng quà cho các độc giả thân thiết của thư viện.
     * @return Danh sách MemberBorrowDTO chứa (Tên thành viên, Tổng số sách đã mượn).
     */
    @Override
    public List<MemberBorrowDTO> topMembers() {
        return reportDAO.getTopMembers();
    }

    /**
     * Tổng hợp báo cáo về các khoản phạt.
     * Cung cấp cái nhìn tổng quan về tình hình tài chính, số tiền đã thu và số tiền còn nợ.
     * @return Danh sách FineReportDTO chứa các thông tin tổng hợp về tiền phạt.
     */
    @Override
    public List<FineReportDTO> fineReports() {
        return reportDAO.getFineReports();
    }
}