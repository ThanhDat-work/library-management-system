package com.example.librarymis.service.impl;

// Import các interface và lớp hỗ trợ trong hệ thống
import java.util.List;

import com.example.librarymis.dao.CategoryDAO;
import com.example.librarymis.dao.impl.CategoryDAOImpl;
import com.example.librarymis.model.entity.Category;
import com.example.librarymis.service.CategoryService;
import com.example.librarymis.util.ValidationUtil;

/**
 * Lớp CategoryServiceImpl thực thi các nghiệp vụ liên quan đến Thể loại sách.
 * Đóng vai trò là tầng Service, xử lý logic trước khi gọi xuống tầng DAO.
 */
public class CategoryServiceImpl implements CategoryService {
    
    /**
     * Khởi tạo đối tượng CategoryDAO để tương tác với cơ sở dữ liệu.
     * Sử dụng tính đóng gói (private) và bất biến (final).
     */
    private final CategoryDAO categoryDAO = new CategoryDAOImpl();

    /**
     * Lưu một thể loại mới hoặc cập nhật thể loại hiện có.
     * @param category Đối tượng thể loại cần lưu.
     * @return Đối tượng sau khi đã được lưu thành công vào DB.
     */
    @Override
    public Category save(Category category) {
        // --- KIỂM TRA DỮ LIỆU ---
        // Đảm bảo tên thể loại không được để trống hoặc chỉ có khoảng trắng
        ValidationUtil.notBlank(category.getName(), "Tên thể loại không được để trống");
        
        // Gọi xuống tầng DAO để thực hiện câu lệnh INSERT hoặc UPDATE
        return categoryDAO.save(category);
    }

    /**
     * Lấy danh sách tất cả các thể loại sách có trong hệ thống.
     * @return List các đối tượng Category.
     */
    @Override
    public List<Category> getAll() {
        return categoryDAO.findAll();
    }

    /**
     * Tìm kiếm thể loại sách theo từ khóa tên.
     * @param keyword Từ khóa cần tìm.
     * @return Danh sách thể loại khớp với từ khóa.
     */
    @Override
    public List<Category> search(String keyword) {
        /*
         * Kiểm tra từ khóa:
         * - Nếu keyword là null hoặc rỗng: Trả về toàn bộ danh sách (getAll).
         * - Nếu có keyword: Cắt khoảng trắng thừa (.trim()) và gọi hàm tìm kiếm của DAO.
         */
        return keyword == null || keyword.isBlank() ? getAll() : categoryDAO.searchByName(keyword.trim());
    }

    /**
     * Xóa một thể loại sách dựa trên ID.
     * @param id Mã định danh của thể loại cần xóa.
     */
    @Override
    public void delete(Long id) {
        // Gọi DAO để thực hiện lệnh xóa trong database
        categoryDAO.deleteById(id);
    }

    /**
     * Thống kê tổng số lượng thể loại sách hiện có.
     * @return Số lượng thể loại (kiểu long).
     */
    @Override
    public long count() {
        // Lấy toàn bộ danh sách và đếm số phần tử
        return getAll().size();
    }
}