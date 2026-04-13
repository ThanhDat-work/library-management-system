package com.example.librarymis.service.impl;

// Import các interface DAO, thực thể MembershipType và công cụ kiểm tra dữ liệu
import java.util.List;

import com.example.librarymis.dao.MembershipTypeDAO;
import com.example.librarymis.dao.impl.MembershipTypeDAOImpl;
import com.example.librarymis.model.entity.MembershipType;
import com.example.librarymis.service.MembershipTypeService;
import com.example.librarymis.util.ValidationUtil;

/**
 * Lớp MembershipTypeServiceImpl thực thi các nghiệp vụ quản lý Loại thẻ thành viên.
 * Các loại thẻ này định nghĩa quy tắc mượn sách (như số ngày tối đa) cho các nhóm độc giả khác nhau.
 */
public class MembershipTypeServiceImpl implements MembershipTypeService {
    
    /**
     * Khởi tạo đối tượng DAO để thực hiện truy vấn dữ liệu liên quan đến loại thẻ.
     */
    private final MembershipTypeDAO membershipTypeDAO = new MembershipTypeDAOImpl();

    /**
     * Lưu mới hoặc cập nhật một loại thẻ thành viên.
     * @param membershipType Đối tượng loại thẻ chứa thông tin tên, hạn mức mượn...
     * @return Đối tượng sau khi đã lưu thành công vào cơ sở dữ liệu.
     */
    @Override
    public MembershipType save(MembershipType membershipType) {
        // --- KIỂM TRA DỮ LIỆU ---
        
        // Đảm bảo tên loại thẻ (ví dụ: "Premium", "Student") không được để trống
        ValidationUtil.notBlank(membershipType.getName(), "Tên loại thẻ không được để trống");
        
        // Gọi xuống tầng DAO để thực hiện lưu trữ (INSERT/UPDATE)
        return membershipTypeDAO.save(membershipType);
    }

    /**
     * Lấy danh sách tất cả các loại thẻ thành viên đang có trong hệ thống.
     * @return Danh sách List các đối tượng MembershipType.
     */
    @Override
    public List<MembershipType> getAll() {
        return membershipTypeDAO.findAll();
    }

    /**
     * Tìm kiếm loại thẻ thành viên theo từ khóa.
     * @param keyword Chuỗi tìm kiếm do người dùng nhập.
     * @return Danh sách các loại thẻ khớp với từ khóa tìm kiếm.
     */
    @Override
    public List<MembershipType> search(String keyword) {
        /*
         * Logic xử lý từ khóa:
         * - Nếu keyword là null hoặc chỉ chứa khoảng trắng: Trả về toàn bộ danh sách.
         * - Nếu có keyword: Cắt bỏ khoảng trắng thừa ở hai đầu và thực hiện tìm kiếm trong DB.
         */
        return keyword == null || keyword.isBlank() ? getAll() : membershipTypeDAO.search(keyword.trim());
    }

    /**
     * Xóa một loại thẻ thành viên dựa trên ID.
     * Lưu ý: Trong thực tế, nếu có thành viên (Member) đang sử dụng loại thẻ này, 
     * việc xóa có thể gây lỗi ràng buộc khóa ngoại trong Database.
     * @param id Mã định danh của loại thẻ cần xóa.
     */
    @Override
    public void delete(Long id) {
        membershipTypeDAO.deleteById(id);
    }
}