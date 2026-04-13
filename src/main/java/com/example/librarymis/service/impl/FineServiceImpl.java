package com.example.librarymis.service.impl;

// Import các công cụ cấu hình JPA, các lớp DAO và Entity cần thiết
import java.util.List;

import com.example.librarymis.config.JpaUtil;
import com.example.librarymis.dao.FineDAO;
import com.example.librarymis.dao.impl.FineDAOImpl;
import com.example.librarymis.model.entity.Fine;
import com.example.librarymis.model.entity.Payment;
import com.example.librarymis.service.FineService;
import com.example.librarymis.util.ValidationUtil;

/**
 * Lớp FineServiceImpl thực thi các nghiệp vụ liên quan đến quản lý các khoản phạt.
 * Bao gồm tạo mới, truy vấn danh sách chưa thanh toán và xóa khoản phạt kèm dữ liệu liên quan.
 */
public class FineServiceImpl implements FineService {
    
    // Đối tượng truy cập dữ liệu cho bảng Fine
    private final FineDAO fineDAO = new FineDAOImpl();

    /**
     * Lưu một khoản phạt mới hoặc cập nhật khoản phạt hiện có.
     * @param fine Đối tượng Fine chứa thông tin phạt.
     * @return Đối tượng Fine sau khi đã lưu thành công.
     */
    @Override
    public Fine save(Fine fine) {
        // --- BƯỚC 1: KIỂM TRA TÍNH HỢP LỆ (VALIDATION) ---
        // Đảm bảo khoản phạt phải gắn liền với một thành viên cụ thể
        ValidationUtil.require(fine.getMember() != null, "Chưa chọn thành viên");
        // Đảm bảo số tiền phạt không được để trống
        ValidationUtil.require(fine.getAmount() != null, "Số tiền phạt không hợp lệ");
        
        // --- BƯỚC 2: CẤU HÌNH MÃ TỰ ĐỘNG ---
        // Nếu là tạo mới (chưa có mã), hệ thống tự sinh mã bắt đầu bằng "FN" + timestamp
        if (fine.getCode() == null || fine.getCode().isBlank()) {
            fine.setCode("FN" + System.currentTimeMillis());
        }
        
        // Gọi DAO để thực hiện lưu xuống database
        return fineDAO.save(fine);
    }

    /**
     * Lấy toàn bộ danh sách các khoản phạt trong hệ thống.
     */
    @Override
    public List<Fine> getAll() {
        return fineDAO.findAll();
    }

    /**
     * Lấy danh sách các khoản phạt mà thành viên chưa thanh toán.
     */
    @Override
    public List<Fine> getUnpaid() {
        return fineDAO.findUnpaidFines();
    }

    /**
     * Xóa một khoản phạt dựa trên ID và xử lý dọn dẹp các ràng buộc dữ liệu liên quan.
     * @param id Mã định danh của khoản phạt cần xóa.
     */
    @Override
    public void delete(Long id) {
        // Thực hiện trong một Transaction để đảm bảo nếu xóa thanh toán lỗi thì không xóa khoản phạt
        JpaUtil.executeInTransaction(em -> {
            // Tìm đối tượng Fine trong Database
            Fine fine = em.find(Fine.class, id);
            if (fine == null) {
                return null; // Nếu không tìm thấy thì kết thúc
            }
            
            // --- BƯỚC 1: XÓA CÁC GIAO DỊCH THANH TOÁN (PAYMENTS) LIÊN QUAN ---
            // Tìm tất cả các phiếu thu (Payment) đã được tạo cho khoản phạt này
            List<Payment> payments = em.createQuery("""
                    select p from Payment p
                    where p.fine.id = :fineId
                    """, Payment.class)
                    .setParameter("fineId", id)
                    .getResultList();
            
            // Duyệt và xóa từng bản ghi thanh toán để tránh lỗi ràng buộc khóa ngoại
            for (Payment payment : payments) {
                em.remove(payment);
            }
            
            // --- BƯỚC 2: GỠ LIÊN KẾT VỚI PHIẾU MƯỢN (BORROW RECORD) ---
            // Nếu khoản phạt này sinh ra từ một phiếu mượn, ta phải gỡ bỏ tham chiếu từ phiếu mượn đó
            if (fine.getBorrowRecord() != null) {
                fine.getBorrowRecord().setFine(null);
            }
            
            // --- BƯỚC 3: XÓA KHOẢN PHẠT ---
            em.remove(fine);
            return null;
        });
    }

    /**
     * Đếm tổng số lượng các khoản phạt chưa được thanh toán.
     */
    @Override
    public long countUnpaid() {
        // Tận dụng hàm getUnpaid() để lấy danh sách và đếm size
        return getUnpaid().size();
    }
}