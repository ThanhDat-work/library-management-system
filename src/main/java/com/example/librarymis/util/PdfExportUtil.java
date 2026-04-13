package com.example.librarymis.util;

import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import javax.swing.JTable;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Utility class để export dữ liệu từ JTable sang file PDF
 * Sử dụng thư viện iText (lowagie)
 */
public final class PdfExportUtil {
    /**
     * Constructor private để ngăn tạo instance
     */
    private PdfExportUtil() {
    }

    /**
     * Export dữ liệu từ JTable ra file PDF
     *
     * @param table      JTable chứa dữ liệu
     * @param title      tiêu đề của file PDF
     * @param outputFile file output
     * @return file đã export
     */
    public static File exportTable(JTable table, String title, File outputFile) {
        // Mục đích: xử lý logic của hàm exportTable.
        try {
            // Tạo document PDF
            Document document = new Document();
            // Liên kết document với file output
            PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            // Mở document để ghi dữ liệu
            document.open();
            // Thêm tiêu đề
            document.add(new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            // Khoảng trắng
            document.add(new Paragraph(" "));

            // Tạo bảng PDF với số cột = JTable
            PdfPTable pdfTable = new PdfPTable(table.getColumnCount());
            // Tên cột
            for (int col = 0; col < table.getColumnCount(); col++) {
                pdfTable.addCell(new Phrase(table.getColumnName(col)));
            }
            // Dữ liệu từng ô
            for (int row = 0; row < table.getRowCount(); row++) {
                for (int col = 0; col < table.getColumnCount(); col++) {
                    Object value = table.getValueAt(row, col);
                    // Nếu null → tránh lỗi
                    pdfTable.addCell(new Phrase(value == null ? "" : value.toString()));
                }
            }
            // Thêm bảng vào document
            document.add(pdfTable);
            // Đóng document (rất quan trọng)
            document.close();
            return outputFile;
        } catch (Exception e) {
            // Wrap exception để dễ debug
            throw new RuntimeException("Không thể xuất PDF: " + e.getMessage(), e);
        }
    }
}
