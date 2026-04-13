package com.example.librarymis.service.impl;

// Import các lớp hỗ trợ giao dịch, DAO, Entity và Enum liên quan đến thanh toán
import java.time.LocalDate;
import java.util.List;

import com.example.librarymis.config.JpaUtil;
import com.example.librarymis.dao.PaymentDAO;
import com.example.librarymis.dao.impl.PaymentDAOImpl;
import com.example.librarymis.model.entity.Fine;
import com.example.librarymis.model.entity.Payment;
import com.example.librarymis.model.enumtype.PaymentStatus;
import com.example.librarymis.service.PaymentService;
import com.example.librarymis.util.ValidationUtil;

/**
 * Lớp PaymentServiceImpl quản lý các giao dịch thanh toán tiền phạt.
 * Chịu trách nhiệm ghi nhận phiếu thu và đồng bộ trạng thái thanh toán với khoản phạt tương ứng.
 */
public class PaymentServiceImpl implements PaymentService {
    // Đối tượng truy cập dữ liệu cho bảng Payment
    private final PaymentDAO paymentDAO = new PaymentDAOImpl();
    
    // Sử dụng FineService để cập nhật lại trạng thái của khoản phạt sau khi thanh toán
    private final FineServiceImpl fineService = new FineServiceImpl();

    /**
     * Thực hiện lưu phiếu thanh toán và cập nhật trạng thái khoản phạt.
     * @param payment Đối tượng chứa thông tin giao dịch thu tiền.
     * @return Đối tượng Payment sau khi đã lưu thành công.
     */
    @Override
    public Payment save(Payment payment) {
        // --- BƯỚC 1: KIỂM TRA TÍNH HỢP LỆ (VALIDATION) ---
        // Đảm bảo phải có đầy đủ thông tin: Người đóng, Người thu, Khoản phạt nào và Số tiền bao nhiêu
        ValidationUtil.require(payment.getMember() != null, "Chưa chọn thành viên");
        ValidationUtil.require(payment.getLibrarian() != null, "Chưa chọn thủ thư");
        ValidationUtil.require(payment.getFine() != null, "Chưa chọn khoản phạt");
        ValidationUtil.require(payment.getAmount() != null, "Số tiền không hợp lệ");
        
        // --- BƯỚC 2: THIẾT LẬP DỮ LIỆU MẶC ĐỊNH ---
        // Tự động sinh mã phiếu thu nếu chưa có (PM + thời gian hiện tại)
        if (payment.getCode() == null || payment.getCode().isBlank()) {
            payment.setCode("PM" + System.currentTimeMillis());
        }
        // Nếu không chọn ngày thanh toán, mặc định lấy ngày hiện tại
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDate.now());
        }
        
        // --- BƯỚC 3: LƯU PHIẾU THANH TOÁN ---
        Payment saved = paymentDAO.save(payment);
        
        // --- BƯỚC 4: ĐỒNG BỘ TRẠNG THÁI SANG KHOẢN PHẠT (FINE) ---
        Fine fine = saved.getFine();
        // Nếu thanh toán thành công (PAID), ta cần cập nhật khoản phạt đó thành "Đã thanh toán"
        if (fine != null && saved.getStatus() == PaymentStatus.PAID) {
            fine.setPaymentStatus(PaymentStatus.PAID);
            fineService.save(fine); // Gọi FineService để cập nhật lại DB cho bảng Fine
        }
        
        return saved;
    }

    /**
     * Lấy toàn bộ danh sách lịch sử các giao dịch thanh toán.
     */
    @Override
    public List<Payment> getAll() {
        return paymentDAO.findAll();
    }

    /**
     * Tìm kiếm danh sách thanh toán dựa trên trạng thái (Ví dụ: Chờ xử lý, Đã hoàn tất).
     * @param status Trạng thái cần lọc (PaymentStatus).
     */
    @Override
    public List<Payment> findByStatus(PaymentStatus status) {
        return paymentDAO.findByStatus(status);
    }

    /**
     * Xóa phiếu thanh toán và hoàn tác trạng thái của khoản phạt liên quan.
     * @param id ID của phiếu thanh toán cần xóa.
     */
    @Override
    public void delete(Long id) {
        // Thực hiện trong Transaction để đảm bảo tính nhất quán (Atomicity)
        JpaUtil.executeInTransaction(em -> {
            // 1. Tìm phiếu thanh toán cần xóa
            Payment payment = em.find(Payment.class, id);
            if (payment == null) {
                return null;
            }
            
            // 2. Lưu lại tham chiếu đến khoản phạt trước khi xóa phiếu thanh toán
            Fine fine = payment.getFine();
            
            // 3. Tiến hành xóa phiếu thanh toán khỏi Database
            em.remove(payment);
            
            // 4. LOGIC HOÀN TÁC (ROLLBACK TRẠNG THÁI):
            // Nếu phiếu thanh toán bị xóa, khoản phạt đó phải trở về trạng thái "Chưa thanh toán" (UNPAID)
            if (fine != null) {
                Fine managedFine = em.find(Fine.class, fine.getId());
                if (managedFine != null) {
                    managedFine.setPaymentStatus(PaymentStatus.UNPAID);
                    em.merge(managedFine); // Cập nhật lại bảng Fine
                }
            }
            return null;
        });
    }
}