package com.example.librarymis.dao.impl;

import com.example.librarymis.config.JpaUtil;
import com.example.librarymis.dao.ReportDAO;
import com.example.librarymis.model.dto.BookBorrowDTO;
import com.example.librarymis.model.dto.FineReportDTO;
import com.example.librarymis.model.dto.MemberBorrowDTO;
import com.example.librarymis.model.dto.MonthlyBorrowDTO;
import com.example.librarymis.model.entity.BorrowDetail;
import com.example.librarymis.model.entity.BorrowRecord;
import com.example.librarymis.model.entity.Fine;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

// DAO chuyên xử lý báo cáo (không CRUD trực tiếp entity)
public class ReportDAOImpl implements ReportDAO {
    // Báo cáo số lượt mượn theo tháng
    @Override
    public List<MonthlyBorrowDTO> getMonthlyBorrowReport() {
        // Lấy toàn bộ BorrowRecord + details
        List<BorrowRecord> records = JpaUtil.execute(em -> em.createQuery("""
                select distinct br from BorrowRecord br
                left join fetch br.details d
                order by br.borrowDate asc
                """, BorrowRecord.class).getResultList());

        // Map: key = "MM/yyyy", value = [số lượt mượn, tổng sách]
        Map<String, long[]> map = new LinkedHashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/yyyy");
        for (BorrowRecord record : records) {
            // format tháng/năm
            String label = record.getBorrowDate().format(fmt);
            // khởi tạo nếu chưa có
            map.putIfAbsent(label, new long[2]);
            // tăng số lượt mượn
            map.get(label)[0] += 1;
            // tính tổng số sách trong phiếu
            long totalBooks = record.getDetails().stream().mapToLong(d -> d.getQuantity() == null ? 0 : d.getQuantity())
                    .sum();
            map.get(label)[1] += totalBooks;
        }
        // convert sang DTO
        return map.entrySet().stream()
                .map(e -> new MonthlyBorrowDTO(e.getKey(), e.getValue()[0], e.getValue()[1]))
                .collect(Collectors.toList());
    }

    // Top sách được mượn nhiều nhất
    @Override
    public List<BookBorrowDTO> getTopBorrowedBooks() {
        // Mục đích: xử lý logic của hàm getTopBorrowedBooks.
        List<BorrowDetail> details = JpaUtil.execute(em -> em.createQuery("""
                select d from BorrowDetail d
                left join fetch d.book
                """, BorrowDetail.class).getResultList());

        // Map: title -> tổng số lượng mượn
        Map<String, Long> totals = new HashMap<>();
        for (BorrowDetail detail : details) {
            String title = detail.getBook() != null ? detail.getBook().getTitle() : "Unknown";
            // cộng dồn số lượng
            totals.merge(title, detail.getQuantity().longValue(), Long::sum);
        }
        // sort giảm dần
        return totals.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(e -> new BookBorrowDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    // Top Member mượn nhiều nhất + tổng tiền phạt
    @Override
    public List<MemberBorrowDTO> getTopMembers() {
        // Mục đích: xử lý logic của hàm getTopMembers.
        List<BorrowRecord> records = JpaUtil.execute(em -> em.createQuery("""
                select distinct br from BorrowRecord br
                left join fetch br.member
                left join fetch br.fine
                """, BorrowRecord.class).getResultList());
        Map<String, Long> borrowCount = new HashMap<>();
        Map<String, BigDecimal> fineAmount = new HashMap<>();

        for (BorrowRecord record : records) {
            String member = record.getMember() != null ? record.getMember().getFullName() : "Unknown";
            // đếm số lượt mượn
            borrowCount.merge(member, 1L, Long::sum);
            // cộng tiền phạt nếu có
            if (record.getFine() != null) {
                fineAmount.merge(member, record.getFine().getAmount(), BigDecimal::add);
            }
        }

        return borrowCount.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(e -> new MemberBorrowDTO(e.getKey(), e.getValue(),
                        fineAmount.getOrDefault(e.getKey(), BigDecimal.ZERO)))
                .collect(Collectors.toList());
    }

    // Báo cáo tiền phạt
    @Override
    public List<FineReportDTO> getFineReports() {
        // Mục đích: xử lý logic của hàm getFineReports.
        List<Fine> fines = JpaUtil.execute(em -> em.createQuery("""
                select f from Fine f
                left join fetch f.member
                left join fetch f.borrowRecord
                order by f.createdDate desc
                """, Fine.class).getResultList());
        return fines.stream()
                .map(f -> new FineReportDTO(
                        // tên member
                        f.getMember() != null ? f.getMember().getFullName() : "",
                        // mã phiếu mượn
                        f.getBorrowRecord() != null ? f.getBorrowRecord().getCode() : "",
                        // số tiền phạt
                        f.getAmount(),
                        // trạng thái thanh toán
                        f.getPaymentStatus()))
                .collect(Collectors.toList());
    }
}
