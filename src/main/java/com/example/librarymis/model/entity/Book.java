package com.example.librarymis.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity lưu trữ thông tin sách trong hệ thống quản lý thư viện.
 */
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @Column(nullable = false, length = 220)
    private String title;

    @Column(nullable = false, length = 160)
    private String author;

    @Column(unique = true, length = 30)
    private String isbn;

    @Column(precision = 12, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    private Integer publishYear;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(nullable = false)
    private Integer availableQuantity = 0;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @OneToMany(mappedBy = "book")
    private List<BorrowDetail> borrowDetails = new ArrayList<>();

    public Book() {
        // Khởi tạo đối tượng sách với các thông tin cơ bản.
    }

    public Book(String code, String title, String author, String isbn, BigDecimal price, Integer publishYear, Integer quantity, Integer availableQuantity, String description, Category category, Publisher publisher) {
        // Khởi tạo đối tượng sách với các thông tin cơ bản.
        this.code = code;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.price = price;
        this.publishYear = publishYear;
        this.quantity = quantity;
        this.availableQuantity = availableQuantity;
        this.description = description;
        this.category = category;
        this.publisher = publisher;
    }

    public Long getId() {
        // Lấy mã định danh của sách.
        return id;
    }

    public String getCode() {
        // Lấy mã sách.
        return code;
    }

    public String getTitle() {
        // Lấy tên sách.
        return title;
    }

    public String getAuthor() {
        // Lấy tác giả.
        return author;
    }

    public String getIsbn() {
        // Lấy mã ISBN của sách.
        return isbn == null ? "" : isbn;
    }

    public BigDecimal getPrice() {
        // Lấy giá sách.
        return price == null ? BigDecimal.ZERO : price;
    }

    public Integer getPublishYear() {
        // Lấy năm xuất bản.
        return publishYear;
    }

    public Integer getQuantity() {
        // Lấy tổng số lượng sách.
        return quantity == null ? 0 : quantity;
    }

    public Integer getAvailableQuantity() {
        // Lấy số lượng sách còn có thể cho mượn.
        return availableQuantity == null ? 0 : availableQuantity;
    }

    public String getDescription() {
        // Lấy mô tả sách.
        return description == null ? "" : description;
    }

    public Category getCategory() {
        // Lấy thể loại của sách.
        return category;
    }

    public Publisher getPublisher() {
        // Lấy nhà xuất bản của sách.
        return publisher;
    }

    public boolean isActive() {
        // Kiểm tra trạng thái hoạt động của sách.
        return active;
    }

    public void setCode(String code) {
        // Cập nhật mã sách.
        this.code = code;
    }

    public void setTitle(String title) {
        // Cập nhật tên sách.
        this.title = title;
    }

    public void setAuthor(String author) {
        // Cập nhật tác giả.
        this.author = author;
    }

    public void setIsbn(String isbn) {
        // Cập nhật mã ISBN của sách.
        this.isbn = isbn;
    }

    public void setPrice(BigDecimal price) {
        // Cập nhật giá sách.
        this.price = price;
    }

    public void setPublishYear(Integer publishYear) {
        // Cập nhật năm xuất bản.
        this.publishYear = publishYear;
    }

    public void setQuantity(Integer quantity) {
        // Cập nhật tổng số lượng sách.
        this.quantity = quantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        // Cập nhật số lượng sách còn có thể cho mượn.
        this.availableQuantity = availableQuantity;
    }

    public void setDescription(String description) {
        // Cập nhật mô tả sách.
        this.description = description;
    }

    public void setCategory(Category category) {
        // Cập nhật thể loại của sách.
        this.category = category;
    }

    public void setPublisher(Publisher publisher) {
        // Cập nhật nhà xuất bản của sách.
        this.publisher = publisher;
    }

    public void setActive(boolean active) {
        // Cập nhật trạng thái hoạt động của sách.
        this.active = active;
    }

    public void increaseAvailable(int amount) {
        // Tăng số lượng sách còn có thể cho mượn nhưng không vượt quá tổng số lượng hiện có.
        this.availableQuantity = getAvailableQuantity() + amount;
        if (this.availableQuantity > getQuantity()) {
            this.availableQuantity = getQuantity();
        }
    }

    public void decreaseAvailable(int amount) {
        // Giảm số lượng sách còn có thể cho mượn nhưng không để nhỏ hơn 0.
        this.availableQuantity = Math.max(0, getAvailableQuantity() - amount);
    }

    @Override
    public String toString() {
        // Tạo chuỗi hiển thị ngắn gọn cho sách.
        return title + " (" + code + ")";
    }
}
