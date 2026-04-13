package com.example.librarymis.model.entity;

import com.example.librarymis.model.enumtype.BorrowStatus;
import com.example.librarymis.model.enumtype.ReturnStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity đại diện cho một phiếu mượn sách của thành viên.
 */
@Entity
@Table(name = "borrow_records")
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @Column(nullable = false)
    private LocalDate borrowDate = LocalDate.now();

    @Column(nullable = false)
    private LocalDate dueDate = LocalDate.now().plusDays(14);

    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BorrowStatus borrowStatus = BorrowStatus.BORROWED;

    @Column(length = 500)
    private String notes;

    @Enumerated(EnumType.STRING)
    private ReturnStatus returnStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "librarian_id", nullable = false)
    private Librarian librarian;

    @OneToMany(mappedBy = "borrowRecord", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<BorrowDetail> details = new ArrayList<>();

    @OneToOne(mappedBy = "borrowRecord")
    private Fine fine;

    public BorrowRecord() {
        // Khởi tạo phiếu mượn với thành viên và thủ thư phụ trách.
    }

    public BorrowRecord(String code, LocalDate borrowDate, LocalDate dueDate, BorrowStatus borrowStatus, String notes,
            Member member, Librarian librarian) {
        // Khởi tạo phiếu mượn với thành viên và thủ thư phụ trách.
        this.code = code;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.borrowStatus = borrowStatus;
        this.notes = notes;
        this.member = member;
        this.librarian = librarian;
    }

    public Long getId() {
        // Lấy mã định danh của phiếu mượn.
        return id;
    }

    public String getCode() {
        // Lấy mã phiếu mượn.
        return code;
    }

    public LocalDate getBorrowDate() {
        // Lấy ngày mượn.
        return borrowDate;
    }

    public LocalDate getDueDate() {
        // Lấy hạn phải trả.
        return dueDate;
    }

    public LocalDate getReturnDate() {
        // Lấy ngày trả thực tế.
        return returnDate;
    }

    public BorrowStatus getBorrowStatus() {
        // Lấy trạng thái mượn.
        return borrowStatus;
    }

    public String getNotes() {
        // Lấy ghi chú của phiếu mượn.
        return notes == null ? "" : notes;
    }

    public Member getMember() {
        // Lấy thành viên mượn sách.
        return member;
    }

    public Librarian getLibrarian() {
        // Lấy thủ thư xử lý phiếu mượn.
        return librarian;
    }

    public List<BorrowDetail> getDetails() {
        // Lấy danh sách chi tiết sách trong phiếu mượn.
        return details;
    }

    public Fine getFine() {
        // Lấy khoản phạt phát sinh từ phiếu mượn.
        return fine;
    }

    public void setCode(String code) {
        // Cập nhật mã phiếu mượn.
        this.code = code;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        // Cập nhật ngày mượn.
        this.borrowDate = borrowDate;
    }

    public void setDueDate(LocalDate dueDate) {
        // Cập nhật hạn phải trả.
        this.dueDate = dueDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        // Cập nhật ngày trả thực tế.
        this.returnDate = returnDate;
    }

    public void setBorrowStatus(BorrowStatus borrowStatus) {
        // Cập nhật trạng thái mượn.
        this.borrowStatus = borrowStatus;
    }

    public ReturnStatus getReturnStatus() {
        // Lấy tình trạng sách khi trả.
        return returnStatus;
    }

    public void setReturnStatus(ReturnStatus returnStatus) {
        // Cập nhật tình trạng sách khi trả.
        this.returnStatus = returnStatus;
    }

    public void setNotes(String notes) {
        // Cập nhật ghi chú của phiếu mượn.
        this.notes = notes;
    }

    public void setMember(Member member) {
        // Cập nhật thành viên mượn sách.
        this.member = member;
    }

    public void setLibrarian(Librarian librarian) {
        // Cập nhật thủ thư xử lý phiếu mượn.
        this.librarian = librarian;
    }

    public void setFine(Fine fine) {
        // Cập nhật khoản phạt phát sinh từ phiếu mượn.
        this.fine = fine;
    }

    public void addDetail(BorrowDetail detail) {
        // Liên kết chi tiết mượn với phiếu hiện tại và thêm vào danh sách chi tiết.
        detail.setBorrowRecord(this);
        this.details.add(detail);
    }

    @Override
    public String toString() {
        // Tạo chuỗi hiển thị ngắn gọn cho phiếu mượn.
        return code + " - " + (member != null ? member.getFullName() : "");
    }
}
