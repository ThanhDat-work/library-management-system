# Library MIS - Java Swing MVC + Hibernate/JPA

Ứng dụng quản lý thư viện theo mô hình **Java Swing MVC + Hibernate/JPA** với giao diện hiện đại bằng **FlatLaf**.

## Tính năng chính

- Đăng nhập thủ thư
- Quản lý sách
- Quản lý thành viên
- Quản lý mượn / trả sách
- Quản lý thể loại
- Quản lý nhà xuất bản
- Quản lý thủ thư
- Quản lý khoản phạt
- Quản lý thanh toán
- Báo cáo thống kê + xuất PDF

## Công nghệ

- Java 17
- Swing
- Hibernate ORM / JPA
- H2 Database
- FlatLaf
- OpenPDF

## Tài khoản mẫu

- `admin / admin123`
- `thuthu / 123456`

## Chạy dự án

### 1. Yêu cầu
- JDK 17+
- Maven 3.9+

### 2. Chạy
```bash
mvn clean compile
mvn exec:java
```

Hoặc đóng gói:
```bash
mvn clean package
java -jar target/librarymis-1.0.0.jar
```

## Cấu trúc

- `config`: JPA, seed dữ liệu
- `model/entity`: thực thể JPA
- `model/dto`: DTO báo cáo
- `dao`: truy cập dữ liệu
- `service`: nghiệp vụ
- `controller`: điều phối giữa View và Service
- `view`: Swing UI
- `util`: tiện ích dùng chung

## CSDL

Dự án đang dùng **H2 file database** mặc định trong `persistence.xml`.

Nếu muốn chuyển sang MySQL/PostgreSQL:
- đổi JDBC URL, driver, username, password trong `src/main/resources/META-INF/persistence.xml`
- thêm dependency JDBC tương ứng trong `pom.xml`

## Ghi chú

- Hệ thống tự động seed dữ liệu lần đầu chạy.
- Khi trả sách trễ, hệ thống tự động tạo khoản phạt.
- Báo cáo có thể xuất PDF trực tiếp từ giao diện.

## Demo
<img width="1644" height="1000" alt="Demo_QuanLyThuVien" src="https://github.com/user-attachments/assets/0df9677e-849e-45b3-9274-2e77d7fb0914" />

