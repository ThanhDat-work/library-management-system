package com.example.librarymis.service.impl;

// Import các interface và lớp cần thiết từ các package khác trong dự án
import java.util.Optional;

import com.example.librarymis.dao.AuthDAO;
import com.example.librarymis.dao.impl.AuthDAOImpl;
import com.example.librarymis.model.entity.Librarian;
import com.example.librarymis.service.AuthService;
import com.example.librarymis.util.ValidationUtil;

/**
 * Lớp AuthServiceImpl triển khai (implement) các phương thức được định nghĩa trong AuthService interface.
 * Đây là nơi chứa logic nghiệp vụ (Business Logic) liên quan đến xác thực người dùng.
 */
public class AuthServiceImpl implements AuthService {
    
    /**
     * Khởi tạo đối tượng authDAO để tương tác với tầng truy cập dữ liệu (Database).
     * Sử dụng interface AuthDAO làm kiểu dữ liệu để đảm bảo tính trừu tượng (Abstraction).
     */
    private final AuthDAO authDAO = new AuthDAOImpl();

    @Override
    public Optional<Librarian> login(String username, String password) {
        
        // --- BƯỚC 1: KIỂM TRA DỮ LIỆU ĐẦU VÀO (VALIDATION) ---
        
        // Kiểm tra xem username có bị trống hoặc chỉ chứa khoảng trắng không.
        // Nếu trống, phương thức này sẽ ném ra một ngoại lệ (Exception) với thông báo kèm theo.
        ValidationUtil.notBlank(username, "Vui lòng nhập tên đăng nhập");
        
        // Tương tự, kiểm tra xem password có hợp lệ hay không trước khi xử lý tiếp.
        ValidationUtil.notBlank(password, "Vui lòng nhập mật khẩu");

        // --- BƯỚC 2: XỬ LÝ NGHIỆP VỤ VÀ TRUY VẤN ---
        
        /* * Gọi xuống tầng DAO để thực hiện kiểm tra trong cơ sở dữ liệu.
         * .trim() được dùng để loại bỏ các khoảng trắng thừa ở hai đầu chuỗi do người dùng vô ý nhập vào.
         * Kết quả trả về là một Optional giúp hạn chế lỗi NullPointerException trong Java.
        */
        return authDAO.authenticate(username.trim(), password.trim());
    }
}