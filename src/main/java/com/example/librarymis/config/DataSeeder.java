package com.example.librarymis.config;

import com.example.librarymis.model.entity.*;
import com.example.librarymis.model.enumtype.BorrowStatus;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Class dùng để khởi tạo dữ liệu mẫu (seed data) cho hệ thống.
 * Thường chạy 1 lần khi ứng dụng start để có dữ liệu demo/test.
 */

public final class DataSeeder {
        /**
         * Constructor private để tránh việc tạo instance (vì class chỉ chứa static
         * method)
         */
        private DataSeeder() {
        }

        /**
         * Hàm chính để seed dữ liệu
         */
        public static void seed() {
                // Sử dụng transaction để đảm bảo toàn bộ dữ liệu được insert atomically
                JpaUtil.executeInTransaction(em -> {
                        // Nếu đã có Librarian rồi -> coi như đã seed trước đó -> không seed lại
                        if (hasAnyLibrarian(em)) {
                                return null;
                        }

                        // Membership types (4 mẫu)
                        MembershipType standard = new MembershipType("Standard", 3, 14, new BigDecimal("120000"),
                                        "Gói cơ bản cho sinh viên và bạn đọc phổ thông");
                        MembershipType premium = new MembershipType("Premium", 6, 30, new BigDecimal("250000"),
                                        "Gói nâng cao cho bạn đọc mượn nhiều");
                        MembershipType vip = new MembershipType("VIP", 10, 45, new BigDecimal("450000"),
                                        "Gói cao cấp cho thư viện doanh nghiệp và độc giả thân thiết");
                        MembershipType family = new MembershipType("Family", 8, 21, new BigDecimal("320000"),
                                        "Gói gia đình dùng chung cho nhiều thành viên");

                        // persist = đưa entity vào persistence context và insert DB
                        em.persist(standard);
                        em.persist(premium);
                        em.persist(vip);
                        em.persist(family);

                        // Categories (5 mẫu)
                        Category tech = new Category("Công nghệ", "Sách CNTT, lập trình, chuyển đổi số");
                        Category business = new Category("Kinh doanh", "Quản trị, marketing, tài chính");
                        Category literature = new Category("Văn học", "Tiểu thuyết, truyện ngắn, thơ");
                        Category science = new Category("Khoa học", "Vật lý, sinh học, khám phá khoa học");
                        Category softskill = new Category("Kỹ năng", "Giao tiếp, tư duy, phát triển bản thân");
                        em.persist(tech);
                        em.persist(business);
                        em.persist(literature);
                        em.persist(science);
                        em.persist(softskill);

                        // Publishers (5 mẫu)
                        Publisher nxbgd = new Publisher("NXB Giáo Dục", "support@nxbgd.vn", "0281234567",
                                        "Hồ Chí Minh");
                        Publisher tre = new Publisher("NXB Trẻ", "contact@nxbtre.vn", "0287654321", "TP.HCM");
                        Publisher kimDong = new Publisher("NXB Kim Đồng", "hello@nxbkimdong.vn", "02435667788",
                                        "Hà Nội");
                        Publisher laoDong = new Publisher("NXB Lao Động", "info@nxblaodong.vn", "02439887766",
                                        "Hà Nội");
                        Publisher vanHoa = new Publisher("NXB Văn Hóa", "care@vanhoa.vn", "02888886666", "Đà Nẵng");
                        em.persist(nxbgd);
                        em.persist(tre);
                        em.persist(kimDong);
                        em.persist(laoDong);
                        em.persist(vanHoa);

                        // Librarians (5 mẫu)
                        Librarian admin = new Librarian("admin", "admin123", "Quản trị hệ thống", "admin@library.local",
                                        "ADMIN");
                        Librarian staff = new Librarian("thuthu", "123456", "Nguyễn Thủ Thư", "staff@library.local",
                                        "STAFF");
                        Librarian staff2 = new Librarian("huong", "123456", "Phạm Thu Hương", "huong@library.local",
                                        "STAFF");
                        Librarian staff3 = new Librarian("minh", "123456", "Lê Quang Minh", "minh@library.local",
                                        "STAFF");
                        Librarian staff4 = new Librarian("lan", "123456", "Trần Mỹ Lan", "lan@library.local", "STAFF");
                        em.persist(admin);
                        em.persist(staff);
                        em.persist(staff2);
                        em.persist(staff3);
                        em.persist(staff4);

                        // Members (5 mẫu)
                        Member member1 = new Member("Trần Minh Khoa", "khoa@gmail.com", "0909000111", "Quận 1",
                                        LocalDate.now().minusMonths(5), LocalDate.now().plusMonths(7), standard);
                        Member member2 = new Member("Lê Thanh Mai", "mai@gmail.com", "0909000222", "Quận Bình Thạnh",
                                        LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(10), premium);
                        Member member3 = new Member("Nguyễn Gia Bảo", "bao@gmail.com", "0909000333", "Quận 7",
                                        LocalDate.now().minusMonths(8), LocalDate.now().plusMonths(4), standard);
                        Member member4 = new Member("Phạm Yến Nhi", "nhi@gmail.com", "0909000444", "Thủ Đức",
                                        LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(11), vip);
                        Member member5 = new Member("Đỗ Anh Tuấn", "tuan@gmail.com", "0909000555", "Gò Vấp",
                                        LocalDate.now().minusMonths(3), LocalDate.now().plusMonths(9), family);
                        em.persist(member1);
                        em.persist(member2);
                        em.persist(member3);
                        em.persist(member4);
                        em.persist(member5);

                        // Books (5 mẫu)
                        Book b1 = new Book("BK001", "Clean Code", "Robert C. Martin", "9780132350884",
                                        new BigDecimal("185000"),
                                        2020, 10, 10, "Sách nền tảng về clean code", tech, nxbgd);
                        Book b2 = new Book("BK002", "Atomic Habits", "James Clear", "9780735211292",
                                        new BigDecimal("165000"), 2019,
                                        8, 8, "Xây dựng thói quen hiệu quả", business, tre);
                        Book b3 = new Book("BK003", "Dế Mèn Phiêu Lưu Ký", "Tô Hoài", "9786042012345",
                                        new BigDecimal("98000"),
                                        2021, 15, 15, "Tác phẩm văn học thiếu nhi nổi tiếng", literature, nxbgd);
                        Book b4 = new Book("BK004", "Lược Sử Thời Gian", "Stephen Hawking", "9780553380163",
                                        new BigDecimal("210000"), 2018, 6, 6, "Kiến thức khoa học phổ thông hấp dẫn",
                                        science, laoDong);
                        Book b5 = new Book("BK005", "Đắc Nhân Tâm", "Dale Carnegie", "9786045891787",
                                        new BigDecimal("125000"),
                                        2022, 12, 12, "Kỹ năng giao tiếp và ứng xử kinh điển", softskill, tre);
                        em.persist(b1);
                        em.persist(b2);
                        em.persist(b3);
                        em.persist(b4);
                        em.persist(b5);

                        // Borrow records (5 mẫu)
                        BorrowRecord record1 = new BorrowRecord("BR0001", LocalDate.now().minusDays(10),
                                        LocalDate.now().plusDays(4), BorrowStatus.BORROWED, "Mượn demo 1", member1,
                                        staff);

                        // Tạo chi tiết mượn cho record1
                        BorrowDetail detail1 = new BorrowDetail(b1, 1, "Còn mới");
                        // Gắn detail vào record (quan hệ 1-n)
                        record1.addDetail(detail1);
                        // Giảm số lượng sách còn lại
                        b1.decreaseAvailable(1);
                        em.persist(record1);
                        em.merge(b1); // merge vì entity đã tồn tại và bị update

                        BorrowRecord record2 = new BorrowRecord("BR0002", LocalDate.now().minusDays(6),
                                        LocalDate.now().plusDays(8),
                                        BorrowStatus.BORROWED, "Mượn sách kỹ năng", member2, staff);
                        BorrowDetail detail2 = new BorrowDetail(b5, 1, "Bìa đẹp");
                        record2.addDetail(detail2);
                        b5.decreaseAvailable(1);
                        em.persist(record2);
                        em.merge(b5);

                        BorrowRecord record3 = new BorrowRecord("BR0003", LocalDate.now().minusDays(20),
                                        LocalDate.now().minusDays(5), BorrowStatus.BORROWED, "Mượn demo 3", member3,
                                        staff2);
                        BorrowDetail detail3 = new BorrowDetail(b2, 1, "Còn nguyên");
                        record3.addDetail(detail3);
                        b2.decreaseAvailable(1);
                        em.persist(record3);
                        em.merge(b2);

                        return null;
                });
        }

        /**
         * Kiểm tra xem đã có dữ liệu Librarian chưa
         * → dùng để tránh seed trùng dữ liệu
         */
        private static boolean hasAnyLibrarian(EntityManager em) {
                // JPQL query: đếm số lượng Librarian
                Long count = em.createQuery("select count(l) from Librarian l", Long.class).getSingleResult();
                // Nếu > 0 nghĩa là đã có dữ liệu
                return count != null && count > 0;
        }
}
