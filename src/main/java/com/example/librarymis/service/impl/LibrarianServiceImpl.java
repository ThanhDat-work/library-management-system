package com.example.librarymis.service.impl;

// Import các interface DAO, thực thể Librarian và các tiện ích kiểm tra dữ liệu
import java.util.List;
import java.util.Optional;

import com.example.librarymis.dao.LibrarianDAO;
import com.example.librarymis.dao.impl.LibrarianDAOImpl;
import com.example.librarymis.model.entity.Librarian;
import com.example.librarymis.service.LibrarianService;
import com.example.librarymis.util.ValidationUtil;

/**
 * Lớp LibrarianServiceImpl thực thi các nghiệp vụ quản lý thông tin thủ thư.
 * Đây là nơi đảm bảo các thông tin như tài khoản, mật khẩu và email được nhập đúng quy tắc.
 */
public class LibrarianServiceImpl implements LibrarianService {
    
    /**
     * Khởi tạo đối tượng LibrarianDAO để thực hiện các thao tác CRUD với bảng thủ thư.
     */
    private final LibrarianDAO librarianDAO = new LibrarianDAOImpl();

    /**
     * Lưu mới hoặc cập nhật thông tin một thủ thư.
     * @param librarian Đối tượng chứa thông tin thủ thư cần lưu.
     * @return Đối tượng Librarian sau khi lưu thành công.
     */
    @Override
    public Librarian save(Librarian librarian) {
        // --- KIỂM TRA DỮ LIỆU ĐẦU VÀO (VALIDATION) ---
        
        // Đảm bảo các trường thông tin cơ bản không được để trống
        ValidationUtil.notBlank(librarian.getUsername(), "Username không được để trống");
        ValidationUtil.notBlank(librarian.getPassword(), "Mật khẩu không được để trống");
        ValidationUtil.notBlank(librarian.getFullName(), "Họ tên không được để trống");
        
        // Kiểm tra định dạng Email (phải có dấu @, tên miền,...) thông qua Regex trong ValidationUtil
        ValidationUtil.email(librarian.getEmail(), "Email không hợp lệ");
        
        // Sau khi các điều kiện trên thỏa mãn, gọi DAO để lưu dữ liệu
        return librarianDAO.save(librarian);
    }

    /**
     * Truy xuất danh sách tất cả thủ thư hiện có trong hệ thống.
     * @return Danh sách các đối tượng Librarian.
     */
    @Override
    public List<Librarian> getAll() {
        return librarianDAO.findAll();
    }

    /**
     * Tìm kiếm thủ thư dựa trên một từ khóa (keyword).
     * @param keyword Chuỗi tìm kiếm (thường là tên hoặc username).
     * @return Danh sách thủ thư khớp với từ khóa.
     */
    @Override
    public List<Librarian> search(String keyword) {
        /*
         * Nếu từ khóa rỗng hoặc null: Trả về tất cả thủ thư.
         * Nếu có từ khóa: Loại bỏ khoảng trắng thừa ở hai đầu và thực hiện tìm kiếm.
         */
        return keyword == null || keyword.isBlank() ? getAll() : librarianDAO.search(keyword.trim());
    }

    /**
     * Tìm kiếm thủ thư chính xác theo tên đăng nhập.
     * @param username Tên đăng nhập cần tìm.
     * @return Một Optional chứa thủ thư (nếu tìm thấy) hoặc rỗng (nếu không thấy).
     */
    @Override
    public Optional<Librarian> findByUsername(String username) {
        return librarianDAO.findByUsername(username);
    }

    /**
     * Xóa tài khoản thủ thư khỏi hệ thống dựa trên ID.
     * @param id Mã định danh của thủ thư.
     */
    @Override
    public void delete(Long id) {
        librarianDAO.deleteById(id);
    }
}