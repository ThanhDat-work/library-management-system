package com.example.librarymis.service.impl;

// Import các lớp DAO, Entity và tiện ích kiểm tra dữ liệu
import java.util.List;

import com.example.librarymis.dao.MemberDAO;
import com.example.librarymis.dao.impl.MemberDAOImpl;
import com.example.librarymis.model.entity.Member;
import com.example.librarymis.service.MemberService;
import com.example.librarymis.util.ValidationUtil;

/**
 * Lớp MemberServiceImpl thực thi các nghiệp vụ liên quan đến quản lý thành viên (độc giả).
 * Đảm bảo thông tin thành viên hợp lệ trước khi thực hiện các thao tác lưu trữ.
 */
public class MemberServiceImpl implements MemberService {
    
    /**
     * Khởi tạo đối tượng MemberDAO để tương tác với cơ sở dữ liệu bảng thành viên.
     */
    private final MemberDAO memberDAO = new MemberDAOImpl();

    /**
     * Lưu thông tin thành viên mới hoặc cập nhật thông tin thành viên hiện có.
     * @param member Đối tượng thành viên cần lưu.
     * @return Đối tượng thành viên sau khi đã được lưu vào DB.
     */
    @Override
    public Member save(Member member) {
        // --- KIỂM TRA TÍNH HỢP LỆ CỦA DỮ LIỆU (VALIDATION) ---
        
        // Kiểm tra họ tên: không được để trống hoặc chỉ chứa khoảng trắng
        ValidationUtil.notBlank(member.getFullName(), "Họ tên thành viên không được để trống");
        
        // Kiểm tra định dạng Email: phải đúng cấu trúc email chuẩn
        ValidationUtil.email(member.getEmail(), "Email không hợp lệ");
        
        // Ràng buộc nghiệp vụ: Mỗi thành viên bắt buộc phải được gán một loại thẻ (MembershipType)
        // Điều này quan trọng vì loại thẻ sẽ quyết định số ngày mượn tối đa (như đã thấy ở BorrowService)
        ValidationUtil.require(member.getMembershipType() != null, "Vui lòng chọn loại thẻ");
        
        // Gọi tầng DAO để thực hiện lưu trữ
        return memberDAO.save(member);
    }

    /**
     * Lấy toàn bộ danh sách thành viên trong hệ thống.
     * @return Danh sách List<Member>.
     */
    @Override
    public List<Member> getAll() {
        return memberDAO.findAll();
    }

    /**
     * Tìm kiếm thành viên theo từ khóa (tên, mã thẻ, email...).
     * @param keyword Từ khóa tìm kiếm.
     * @return Danh sách thành viên phù hợp.
     */
    @Override
    public List<Member> search(String keyword) {
        /*
         * Xử lý logic tìm kiếm:
         * - Nếu không nhập từ khóa: Trả về tất cả thành viên.
         * - Nếu có nhập: Xóa khoảng trắng thừa và gọi hàm tìm kiếm của DAO.
         */
        return keyword == null || keyword.isBlank() ? getAll() : memberDAO.search(keyword.trim());
    }

    /**
     * Truy xuất danh sách các thành viên đang ở trạng thái hoạt động (Active).
     * Thường dùng để hiển thị trong danh sách chọn khi tạo phiếu mượn.
     */
    @Override
    public List<Member> getActiveMembers() {
        return memberDAO.findActiveMembers();
    }

    /**
     * Xóa một thành viên khỏi hệ thống dựa trên ID.
     * Lưu ý: Nếu thành viên đã có lịch sử mượn sách, việc xóa này có thể bị chặn bởi ràng buộc DB.
     * @param id Mã định danh của thành viên.
     */
    @Override
    public void delete(Long id) {
        memberDAO.deleteById(id);
    }

    /**
     * Thống kê tổng số lượng thành viên đang quản lý.
     * @return Tổng số lượng kiểu long.
     */
    @Override
    public long count() {
        // Tận dụng hàm getAll() và lấy kích thước danh sách trả về
        return getAll().size();
    }
}