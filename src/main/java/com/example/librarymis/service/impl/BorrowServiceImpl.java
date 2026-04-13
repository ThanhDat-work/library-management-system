package com.example.librarymis.service.impl;

import com.example.librarymis.config.JpaUtil;
import com.example.librarymis.dao.BorrowDAO;
import com.example.librarymis.dao.impl.BorrowDAOImpl;
import com.example.librarymis.model.entity.Book;
import com.example.librarymis.model.entity.BorrowDetail;
import com.example.librarymis.model.entity.BorrowRecord;
import com.example.librarymis.model.entity.Fine;
import com.example.librarymis.model.entity.Payment;
import com.example.librarymis.model.enumtype.BorrowStatus;
import com.example.librarymis.model.enumtype.PaymentStatus;
import com.example.librarymis.model.enumtype.ReturnStatus;
import com.example.librarymis.service.BorrowService;
import com.example.librarymis.util.ValidationUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BorrowServiceImpl implements BorrowService {
    private final BorrowDAO borrowDAO = new BorrowDAOImpl();
    // Hằng số cho tiền phạt mỗi ngày
    private static final BigDecimal FINE_PER_DAY = new BigDecimal("5000.00");

    /**
     * Tự động quét và cập nhật trạng thái từ BORROWED sang OVERDUE dựa trên ngày
     * hiện tại.
     */
    private void refreshOverdueStatus() {
        JpaUtil.executeInTransaction(em -> {
            em.createQuery("UPDATE BorrowRecord r SET r.borrowStatus = :overdue " +
                    "WHERE r.borrowStatus = :borrowed AND r.dueDate < :today")
                    .setParameter("overdue", BorrowStatus.OVERDUE)
                    .setParameter("borrowed", BorrowStatus.BORROWED)
                    .setParameter("today", LocalDate.now())
                    .executeUpdate();
            return null;
        });
    }

    @Override
    public BorrowRecord issueBorrow(BorrowRecord record) {
        // Mục đích: xử lý logic của hàm issueBorrow.
        ValidationUtil.require(record.getMember() != null, "Chưa chọn thành viên");
        ValidationUtil.require(record.getLibrarian() != null, "Chưa chọn thủ thư");
        ValidationUtil.require(record.getDetails() != null && !record.getDetails().isEmpty(),
                "Phiếu mượn phải có ít nhất 1 đầu sách");

        if (record.getCode() == null || record.getCode().isBlank()) {
            record.setCode("BR" + System.currentTimeMillis());
        }
        if (record.getBorrowDate() == null) {
            record.setBorrowDate(LocalDate.now());
        }
        if (record.getDueDate() == null) {
            int maxDays = record.getMember().getMembershipType() != null
                    ? record.getMember().getMembershipType().getMaxBorrowDays()
                    : 14;
            record.setDueDate(record.getBorrowDate().plusDays(maxDays));
        }
        record.setBorrowStatus(BorrowStatus.BORROWED);

        return JpaUtil.executeInTransaction(em -> {
            BorrowRecord managed = new BorrowRecord();
            managed.setCode(record.getCode());
            managed.setBorrowDate(record.getBorrowDate());
            managed.setDueDate(record.getDueDate());
            managed.setNotes(record.getNotes());
            managed.setBorrowStatus(record.getBorrowStatus());
            managed.setMember(em.find(record.getMember().getClass(), record.getMember().getId()));
            managed.setLibrarian(em.find(record.getLibrarian().getClass(), record.getLibrarian().getId()));

            for (BorrowDetail detail : record.getDetails()) {
                Book book = em.find(Book.class, detail.getBook().getId());
                ValidationUtil.require(book != null, "Không tìm thấy sách: " + detail.getBook());
                ValidationUtil.require(book.getAvailableQuantity() >= detail.getQuantity(),
                        "Sách " + book.getTitle() + " không đủ số lượng");
                book.decreaseAvailable(detail.getQuantity());
                em.merge(book);

                BorrowDetail managedDetail = new BorrowDetail();
                managedDetail.setBook(book);
                managedDetail.setQuantity(detail.getQuantity());
                managedDetail.setReturnedQuantity(0);
                managedDetail.setNote(detail.getNote());
                managed.addDetail(managedDetail);
            }

            em.persist(managed);
            return managed;
        });
    }

    @Override
    public BorrowRecord save(BorrowRecord record) {
        if (record.getId() == null) {
            return issueBorrow(record);
        } else {
            // This is an update operation for an existing BorrowRecord
            return JpaUtil.executeInTransaction(em -> {
                BorrowRecord managedRecord = em.find(BorrowRecord.class, record.getId());
                ValidationUtil.require(managedRecord != null, "Phiếu mượn không tồn tại.");

                // Update header fields from the incoming record (assuming details are not
                // modified via this save path)
                managedRecord.setCode(record.getCode());
                managedRecord.setMember(em.find(record.getMember().getClass(), record.getMember().getId()));
                managedRecord.setLibrarian(em.find(record.getLibrarian().getClass(), record.getLibrarian().getId()));
                managedRecord.setBorrowDate(record.getBorrowDate());
                managedRecord.setNotes(record.getNotes());
                managedRecord.setDueDate(record.getDueDate()); // Update due date

                // Re-evaluate borrow status based on the new due date
                if (managedRecord.getDueDate().isBefore(LocalDate.now())) {
                    managedRecord.setBorrowStatus(BorrowStatus.OVERDUE);
                } else {
                    managedRecord.setBorrowStatus(BorrowStatus.BORROWED);
                }

                return em.merge(managedRecord);
            });
        }
    }

    @Override
    public List<BorrowRecord> getAll() {
        // Mục đích: xử lý logic của hàm getAll.
        refreshOverdueStatus();
        return borrowDAO.findAll();
    }

    @Override
    public List<BorrowRecord> getActive() {
        // Mục đích: xử lý logic của hàm getActive.
        refreshOverdueStatus();
        return borrowDAO.findActiveBorrows();
    }

    @Override
    public List<BorrowRecord> getOverdue() {
        // Mục đích: xử lý logic của hàm getOverdue.
        refreshOverdueStatus();
        return borrowDAO.findOverdueRecords(LocalDate.now());
    }

    @Override
    public void delete(Long id) {
        // Mục đích: xử lý logic của hàm delete.
        JpaUtil.executeInTransaction(em -> {
            BorrowRecord record = em.find(BorrowRecord.class, id);
            if (record == null) {
                return null;
            }

            for (BorrowDetail detail : record.getDetails()) {
                Book book = em.find(Book.class, detail.getBook().getId());
                int borrowedNotReturned = detail.getQuantity() - detail.getReturnedQuantity();
                if (book != null && borrowedNotReturned > 0) {
                    book.increaseAvailable(borrowedNotReturned);
                    em.merge(book);
                }
            }

            if (record.getFine() != null) {
                List<Payment> payments = em.createQuery("""
                        select p from Payment p
                        where p.fine.id = :fineId
                        """, Payment.class)
                        .setParameter("fineId", record.getFine().getId())
                        .getResultList();
                for (Payment payment : payments) {
                    em.remove(payment);
                }
            }

            em.remove(record);
            return null;
        });
    }

    @Override
    public long countActive() {
        // Mục đích: xử lý logic của hàm countActive.
        refreshOverdueStatus();
        return getActive().size();
    }

    @Override
    public BorrowRecord markAsLost(Long borrowRecordId, List<BorrowDetail> lostDetailsWithCompensation) {
        // Mục đích: xử lý logic của hàm markAsLost, bao gồm cả bồi thường.
        return JpaUtil.executeInTransaction(em -> {
            BorrowRecord record = em.find(BorrowRecord.class, borrowRecordId);
            ValidationUtil.require(record != null, "Không tìm thấy phiếu mượn");
            record.setBorrowStatus(BorrowStatus.LOST);
            record.setReturnStatus(ReturnStatus.LOST); // Set return status to LOST
            record.setReturnDate(LocalDate.now()); // Set return date as now

            // Tính tiền phạt quá hạn (nếu có) để cộng dồn vào tổng Fine
            BigDecimal lateFee = BigDecimal.ZERO;
            String lateReason = "";
            if (record.getDueDate() != null && LocalDate.now().isAfter(record.getDueDate())) {
                long lateDays = ChronoUnit.DAYS.between(record.getDueDate(), LocalDate.now());
                lateFee = BigDecimal.valueOf(lateDays).multiply(FINE_PER_DAY);
                lateReason = "Báo mất sách trễ " + lateDays + " ngày";
            }

            StringBuilder damageReasons = new StringBuilder();

            for (BorrowDetail detail : record.getDetails()) {
                int unreturnedQuantity = detail.getQuantity() - detail.getReturnedQuantity();
                if (unreturnedQuantity > 0) {
                    // Tìm thông tin bồi thường tương ứng được gửi từ UI
                    BigDecimal compAmount = lostDetailsWithCompensation.stream()
                            .filter(ld -> ld.getBook().getId().equals(detail.getBook().getId()))
                            .map(BorrowDetail::getDamageCompensationAmount)
                            .findFirst().orElse(BigDecimal.ZERO);

                    // LOGIC MỚI: Cộng dồn tiền bồi thường (Hư hại cũ + Mất mới)
                    BigDecimal currentComp = detail.getDamageCompensationAmount() != null
                            ? detail.getDamageCompensationAmount()
                            : BigDecimal.ZERO;
                    detail.setDamageCompensationAmount(currentComp.add(compAmount));

                    // Cập nhật mô tả: Nối thêm thông tin báo mất vào mô tả cũ
                    String oldDesc = (detail.getDamageDescription() == null || detail.getDamageDescription().isEmpty())
                            ? ""
                            : detail.getDamageDescription() + "; ";
                    detail.setDamageDescription(oldDesc + "Báo mất " + unreturnedQuantity + " cuốn ("
                            + String.format("%,.2f", compAmount) + "đ)");
                }
            }

            // Tính tổng tất cả bồi thường của các dòng sách sau khi đã cộng dồn
            BigDecimal totalCompensation = record.getDetails().stream()
                    .map(d -> d.getDamageCompensationAmount() != null ? d.getDamageCompensationAmount()
                            : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Tổng hợp lý do cho toàn bộ phiếu để đưa sang trang Khoản phạt
            for (BorrowDetail detail : record.getDetails()) {
                if (detail.getDamageCompensationAmount() != null
                        && detail.getDamageCompensationAmount().compareTo(BigDecimal.ZERO) > 0) {
                    damageReasons.append(detail.getBook().getTitle()).append(": ").append(detail.getDamageDescription())
                            .append("; ");
                }
            }

            // Tính tổng tiền thu cuối cùng (Trễ hạn + Toàn bộ bồi thường)
            BigDecimal totalFineAmount = lateFee.add(totalCompensation);

            // LOGIC: Chỉ tạo/cập nhật Khoản phạt khi phiếu đã kết thúc (LOST hoặc RETURNED)
            if (totalFineAmount.compareTo(BigDecimal.ZERO) > 0 &&
                    (record.getBorrowStatus() == BorrowStatus.LOST
                            || record.getBorrowStatus() == BorrowStatus.RETURNED)) {
                // TRƯỜNG HỢP 2: Luôn tạo một khoản phạt mới (New ID) khi báo mất/trả sách
                Fine fine = createNewFine(record);
                fine.setAmount(totalFineAmount);

                String reason = lateReason;
                if (damageReasons.length() > 0) {
                    if (!reason.isEmpty())
                        reason += "; ";
                    reason += damageReasons.toString().trim();
                }
                fine.setReason(reason);
                em.merge(fine);
            }
            return em.merge(record);
        });
    }

    @Override
    public BorrowRecord returnBook(Long borrowRecordId, List<BorrowDetail> updatedDetails) {
        // Mục đích: xử lý logic của hàm returnBook chi tiết theo từng sách.
        return JpaUtil.executeInTransaction(em -> {
            BorrowRecord record = em.find(BorrowRecord.class, borrowRecordId);
            ValidationUtil.require(record != null, "Không tìm thấy phiếu mượn");

            // Xử lý chi tiết sách và tính tổng bồi thường
            ReturnStatus returnStatus = null;
            BigDecimal totalDamageCompensation = BigDecimal.ZERO;
            StringBuilder damageDetails = new StringBuilder();

            boolean allItemsReturned = true;

            for (BorrowDetail originalDetail : record.getDetails()) {
                // Tìm chi tiết cập nhật từ danh sách incoming
                BorrowDetail incomingDetail = updatedDetails.stream()
                        .filter(d -> d.getBook() != null && originalDetail.getBook() != null &&
                                d.getBook().getId().equals(originalDetail.getBook().getId()))
                        .findFirst()
                        .orElse(null);

                if (incomingDetail != null) {
                    // 1. Cập nhật kho dựa trên chênh lệch số lượng trả mới và cũ
                    int newReturnedQty = incomingDetail.getReturnedQuantity();
                    int delta = newReturnedQty - originalDetail.getReturnedQuantity();

                    if (delta > 0) {
                        Book book = em.find(Book.class, originalDetail.getBook().getId());
                        book.increaseAvailable(delta);
                        em.merge(book);
                    }
                    // Luôn cập nhật số lượng trả và thông tin hư hại từ UI (cộng dồn từ người dùng)
                    originalDetail.setReturnedQuantity(newReturnedQty);

                    // 2. Cập nhật mô tả hư hại và tiền bồi thường
                    originalDetail.setDamageDescription(incomingDetail.getDamageDescription());
                    originalDetail.setDamageCompensationAmount(incomingDetail.getDamageCompensationAmount());

                    if (incomingDetail.getDamageCompensationAmount() != null &&
                            incomingDetail.getDamageCompensationAmount().compareTo(BigDecimal.ZERO) > 0) {
                        totalDamageCompensation = totalDamageCompensation
                                .add(incomingDetail.getDamageCompensationAmount());
                        returnStatus = ReturnStatus.DAMAGED;
                    }
                }

                // 3. Kiểm tra xem dòng này đã trả hết chưa
                if (originalDetail.getReturnedQuantity() < originalDetail.getQuantity()) {
                    allItemsReturned = false;
                }

                // Xây dựng chi tiết hư hại cho lý do phạt
                if (originalDetail.getDamageDescription() != null &&
                        originalDetail.getDamageCompensationAmount() != null &&
                        originalDetail.getDamageCompensationAmount().compareTo(BigDecimal.ZERO) > 0) {
                    if (damageDetails.length() > 0) {
                        damageDetails.append("; ");
                    }
                    damageDetails.append(originalDetail.getBook().getTitle())
                            .append(" - ")
                            .append(originalDetail.getDamageDescription())
                            .append(" (")
                            .append(originalDetail.getDamageCompensationAmount())
                            .append("đ)");
                }
            }

            // Cập nhật trạng thái phiếu mượn
            if (allItemsReturned) {
                record.setBorrowStatus(BorrowStatus.RETURNED);
                record.setReturnDate(LocalDate.now());
            } else {
                record.setBorrowStatus(BorrowStatus.BORROWED); // Vẫn đang mượn nếu chưa trả hết
            }

            record.setReturnStatus(returnStatus);

            // Tính tiền phạt quá hạn
            BigDecimal lateFee = BigDecimal.ZERO;
            String lateReason = "";
            if (record.getDueDate() != null && LocalDate.now().isAfter(record.getDueDate())) {
                long lateDays = ChronoUnit.DAYS.between(record.getDueDate(), LocalDate.now());
                if (record.getBorrowStatus() != BorrowStatus.RETURNED
                        && record.getBorrowStatus() != BorrowStatus.LOST) {
                    record.setBorrowStatus(BorrowStatus.OVERDUE); // Only set to OVERDUE if not already returned/lost
                }
                lateFee = BigDecimal.valueOf(lateDays).multiply(FINE_PER_DAY);
                lateReason = "Trả sách trễ " + lateDays + " ngày";
            }

            // Tạo Fine nếu có phí
            BigDecimal totalAmount = lateFee.add(totalDamageCompensation); // Tổng tiền phạt (quá hạn + bồi thường)

            // LOGIC: Chỉ tạo/cập nhật Khoản phạt khi phiếu đã kết thúc
            if (totalAmount.compareTo(BigDecimal.ZERO) > 0 &&
                    (record.getBorrowStatus() == BorrowStatus.LOST
                            || record.getBorrowStatus() == BorrowStatus.RETURNED)) {
                // TRƯỜNG HỢP 2: Luôn tạo khoản phạt mới khi hoàn tất trả sách có phát sinh phí
                Fine fine = createNewFine(record);
                fine.setAmount(totalAmount);

                // Xây dựng lý do phạt chi tiết
                StringBuilder reason = new StringBuilder();
                if (!lateReason.isEmpty()) {
                    reason.append(lateReason);
                }
                if (damageDetails.length() > 0) {
                    if (reason.length() > 0) {
                        reason.append("; ");
                    }
                    reason.append("Hư hại: ").append(damageDetails.toString());
                }

                fine.setReason(reason.toString());
                em.merge(fine);
            }

            return em.merge(record);
        });
    }

    // Helper method to create a new Fine object
    private Fine createNewFine(BorrowRecord record) {
        Fine fine = new Fine();
        fine.setCode("FN" + System.currentTimeMillis());
        fine.setBorrowRecord(record);
        fine.setMember(record.getMember());
        fine.setPaymentStatus(PaymentStatus.UNPAID);
        fine.setCreatedDate(LocalDate.now());
        fine.setAmount(BigDecimal.ZERO); // Initialize with zero, will be updated
        fine.setReason(""); // Initialize with empty reason, will be updated
        return fine;
    }

    @Override
    public List<BorrowRecord> getLost() {
        // Mục đích: xử lý logic của hàm getLost.
        return borrowDAO.findLostBorrows();
    }
}
