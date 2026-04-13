package com.example.librarymis.service.impl;

// Import các interface DAO, thực thể Publisher và công cụ kiểm tra dữ liệu
import java.util.List;

import com.example.librarymis.dao.PublisherDAO;
import com.example.librarymis.dao.impl.PublisherDAOImpl;
import com.example.librarymis.model.entity.Publisher;
import com.example.librarymis.service.PublisherService;
import com.example.librarymis.util.ValidationUtil;

/**
 * Lớp PublisherServiceImpl thực thi các nghiệp vụ liên quan đến Nhà xuất bản.
 * Đóng vai trò là tầng trung gian xử lý logic và kiểm tra ràng buộc trước khi tương tác với DB.
 */
public class PublisherServiceImpl implements PublisherService {
    
    /**
     * Khởi tạo đối tượng DAO để thực hiện các thao tác truy xuất dữ liệu nhà xuất bản.
     */
    private final PublisherDAO publisherDAO = new PublisherDAOImpl();

    /**
     * Lưu mới hoặc cập nhật thông tin một Nhà xuất bản.
     * @param publisher Đối tượng chứa thông tin nhà xuất bản (tên, email, địa chỉ...).
     * @return Đối tượng Publisher sau khi đã được lưu thành công.
     */
    @Override
    public Publisher save(Publisher publisher) {
        // --- KIỂM TRA DỮ LIỆU ĐẦU VÀO ---
        
        // Đảm bảo tên Nhà xuất bản không được để trống hoặc chỉ có khoảng trắng
        ValidationUtil.notBlank(publisher.getName(), "Tên nhà xuất bản không được để trống");
        
        // Kiểm tra định dạng Email của nhà xuất bản để đảm bảo tính liên lạc
        ValidationUtil.email(publisher.getEmail(), "Email nhà xuất bản không hợp lệ");
        
        // Gọi xuống tầng DAO để thực hiện lưu vào cơ sở dữ liệu
        return publisherDAO.save(publisher);
    }

    /**
     * Lấy toàn bộ danh sách các Nhà xuất bản đang có trong hệ thống.
     * @return Danh sách List các đối tượng Publisher.
     */
    @Override
    public List<Publisher> getAll() {
        return publisherDAO.findAll();
    }

    /**
     * Tìm kiếm Nhà xuất bản theo từ khóa (keyword).
     * @param keyword Chuỗi ký tự do người dùng nhập vào để tìm kiếm theo tên.
     * @return Danh sách các nhà xuất bản khớp với từ khóa.
     */
    @Override
    public List<Publisher> search(String keyword) {
        /*
         * Logic xử lý tìm kiếm:
         * - Nếu keyword là null hoặc rỗng: Trả về tất cả danh sách (getAll).
         * - Nếu có keyword: Loại bỏ khoảng trắng thừa (.trim()) và gọi DAO tìm kiếm theo tên.
         */
        return keyword == null || keyword.isBlank() ? getAll() : publisherDAO.searchByName(keyword.trim());
    }

    /**
     * Xóa một Nhà xuất bản khỏi hệ thống dựa trên ID.
     * @param id Mã định danh duy nhất của Nhà xuất bản cần xóa.
     */
    @Override
    public void delete(Long id) {
        // Gọi DAO để thực hiện lệnh xóa bản ghi trong DB
        publisherDAO.deleteById(id);
    }
}