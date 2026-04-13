package com.example.librarymis.service.impl;

// Import các interface, lớp thực thi và các tiện ích cần thiết
import java.util.List;

import com.example.librarymis.dao.BookDAO;
import com.example.librarymis.dao.impl.BookDAOImpl;
import com.example.librarymis.model.entity.Book;
import com.example.librarymis.service.BookService;
import com.example.librarymis.util.ValidationUtil;

/**
 * Lớp BookServiceImpl quản lý các nghiệp vụ liên quan đến sách.
 * Nó đóng vai trò trung gian, nhận dữ liệu từ Controller, xử lý logic/kiểm tra 
 * rồi mới chuyển cho DAO để lưu trữ vào Database.
 */
public class BookServiceImpl implements BookService {
    
    /**
     * Khai báo và khởi tạo đối tượng DAO để làm việc với Database.
     * Tính chất 'final' đảm bảo biến này không bị gán lại sau khi khởi tạo.
     */
    private final BookDAO bookDAO = new BookDAOImpl();

    /**
     * Lưu thông tin sách mới hoặc cập nhật sách hiện có.
     * @param book Đối tượng sách chứa dữ liệu cần lưu.
     * @return Đối tượng sách sau khi đã được lưu thành công.
     */
    @Override
    public Book save(Book book) {
        // --- KIỂM TRA TÍNH HỢP LỆ CỦA DỮ LIỆU (VALIDATION) ---
        
        // Đảm bảo các trường thông tin bắt buộc không được để trống hoặc null
        ValidationUtil.notBlank(book.getCode(), "Mã sách không được để trống");
        ValidationUtil.notBlank(book.getTitle(), "Tên sách không được để trống");
        ValidationUtil.notBlank(book.getAuthor(), "Tác giả không được để trống");
        
        // Kiểm tra tính logic của số lượng (không được là số âm)
        ValidationUtil.require(book.getQuantity() >= 0, "Số lượng không hợp lệ");
        ValidationUtil.require(book.getAvailableQuantity() >= 0, "Số lượng sẵn có không hợp lệ");
        
        // Ràng buộc nghiệp vụ: Sách có sẵn trong kho không thể nhiều hơn tổng số lượng sách sở hữu
        ValidationUtil.require(book.getAvailableQuantity() <= book.getQuantity(),
                "Số lượng sẵn có không thể lớn hơn tổng số lượng");
        
        // Sau khi vượt qua tất cả kiểm tra, gọi DAO để lưu vào DB
        return bookDAO.save(book);
    }

    /**
     * Lấy toàn bộ danh sách sách hiện có trong hệ thống.
     * @return Danh sách các đối tượng Book.
     */
    @Override
    public List<Book> getAll() {
        return bookDAO.findAll();
    }

    /**
     * Tìm kiếm sách theo từ khóa.
     * @param keyword Chuỗi ký tự người dùng nhập để tìm (tên, mã, tác giả...).
     * @return Danh sách sách khớp với từ khóa.
     */
    @Override
    public List<Book> search(String keyword) {
        /* * Logic thông minh: 
         * - Nếu từ khóa trống (null hoặc chỉ có khoảng trắng): trả về tất cả sách.
         * - Nếu có từ khóa: cắt bỏ khoảng trắng thừa (.trim()) và gọi DAO tìm kiếm.
         */
        return keyword == null || keyword.isBlank() ? getAll() : bookDAO.search(keyword.trim());
    }

    /**
     * Lấy danh sách sách sắp hết hàng dựa trên một ngưỡng số lượng.
     * @param threshold Ngưỡng số lượng (ví dụ: lấy những sách còn dưới 5 cuốn).
     */
    @Override
    public List<Book> getLowStock(int threshold) {
        return bookDAO.findLowStock(threshold);
    }

    /**
     * Xóa một cuốn sách khỏi hệ thống dựa trên ID.
     * @param id Mã định danh duy nhất của cuốn sách.
     */
    @Override
    public void delete(Long id) {
        bookDAO.deleteById(id);
    }

    /**
     * Thống kê tổng số lượng đầu sách đang quản lý.
     * @return Số lượng kiểu long.
     */
    @Override
    public long count() {
        // Tận dụng hàm getAll() đã viết sẵn để tính kích thước danh sách
        return getAll().size();
    }

    /**
     * Tìm chi tiết một cuốn sách theo ID.
     * @param id Mã định danh của sách.
     * @return Đối tượng Book nếu tìm thấy.
     * @throws RuntimeException Nếu không tìm thấy ID tương ứng trong DB.
     */
    @Override
    public Book getById(Long id) {
        // Sử dụng Optional.orElseThrow để xử lý trường hợp không tìm thấy dữ liệu một cách gọn gàng
        Book book = bookDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + id));
        return book;
    }
}