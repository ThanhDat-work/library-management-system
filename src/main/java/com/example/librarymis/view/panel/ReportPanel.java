package com.example.librarymis.view.panel;

import com.example.librarymis.controller.ReportController;
import com.example.librarymis.model.dto.BookBorrowDTO;
import com.example.librarymis.model.dto.FineReportDTO;
import com.example.librarymis.model.dto.MemberBorrowDTO;
import com.example.librarymis.model.dto.MonthlyBorrowDTO;
import com.example.librarymis.util.PdfExportUtil;
import com.example.librarymis.view.component.UIHelper;
import com.example.librarymis.view.panel.common.Reloadable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;

public class ReportPanel extends JPanel implements Reloadable {
    private final ReportController controller = new ReportController();
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable table = UIHelper.table(tableModel);
    private String currentTitle = "Báo cáo tổng hợp";
    private Runnable currentLoader = this::loadMonthlyBorrowReport;

    public ReportPanel() {
        // Mục đích: xử lý logic của hàm ReportPanel.
        setLayout(new BorderLayout(16, 16));
        setBackground(UIHelper.APP_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(UIHelper.pageHeader("Báo cáo & thống kê", "Truy xuất số liệu quản trị và xuất PDF nhanh"), BorderLayout.NORTH);

        JPanel toolbar = UIHelper.card(new BorderLayout(0, 12));
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UIHelper.sectionTitle("Chọn loại báo cáo"));
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(UIHelper.subtitle("Tải số liệu nhanh theo từng nhóm thống kê"));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonRow.setOpaque(false);
        JButton btnMonthly = UIHelper.secondaryButton("Mượn theo tháng");
        JButton btnTopBooks = UIHelper.secondaryButton("Sách mượn nhiều");
        JButton btnTopMembers = UIHelper.secondaryButton("Top thành viên");
        JButton btnFine = UIHelper.secondaryButton("Báo cáo phạt");
        JButton btnExport = UIHelper.primaryButton("Xuất PDF");

        buttonRow.add(btnMonthly);
        buttonRow.add(btnTopBooks);
        buttonRow.add(btnTopMembers);
        buttonRow.add(btnFine);
        buttonRow.add(btnExport);

        toolbar.add(titleBox, BorderLayout.NORTH);
        toolbar.add(buttonRow, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setOpaque(false);
        center.add(toolbar, BorderLayout.NORTH);
        JPanel tableCard = UIHelper.card(new BorderLayout(0, 12));
        tableCard.add(UIHelper.sectionTitle("Dữ liệu báo cáo"), BorderLayout.NORTH);
        tableCard.add(UIHelper.wrapTable(table), BorderLayout.CENTER);
        center.add(tableCard, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        btnMonthly.addActionListener(e -> safeRun(this::loadMonthlyBorrowReport));
        btnTopBooks.addActionListener(e -> safeRun(this::loadTopBooksReport));
        btnTopMembers.addActionListener(e -> safeRun(this::loadTopMembersReport));
        btnFine.addActionListener(e -> safeRun(this::loadFineReport));
        btnExport.addActionListener(e -> safeRun(this::exportPdf));

        loadMonthlyBorrowReport();
    }

    private void safeRun(Runnable task) {
        // Mục đích: xử lý logic của hàm safeRun.
        try {
            // Mục đích: xử lý logic của hàm safeRun.
            task.run();
        } catch (Exception e) {
            UIHelper.error(this, e.getMessage());
        }
    }

    private void loadMonthlyBorrowReport() {
        // Mục đích: xử lý logic của hàm loadMonthlyBorrowReport.
        currentLoader = this::loadMonthlyBorrowReport;
        currentTitle = "Báo cáo mượn sách theo tháng";
        tableModel.setDataVector(new Object[][]{}, new String[]{"Tháng", "Số phiếu mượn", "Tổng số sách"});
        for (MonthlyBorrowDTO dto : controller.monthlyBorrows()) {
            tableModel.addRow(new Object[]{dto.getMonthLabel(), dto.getTotalRecords(), dto.getTotalBooks()});
        }
    }

    private void loadTopBooksReport() {
        // Mục đích: xử lý logic của hàm loadTopBooksReport.
        currentLoader = this::loadTopBooksReport;
        currentTitle = "Báo cáo sách được mượn nhiều nhất";
        tableModel.setDataVector(new Object[][]{}, new String[]{"Tên sách", "Tổng lượt mượn"});
        for (BookBorrowDTO dto : controller.topBooks()) {
            tableModel.addRow(new Object[]{dto.getBookTitle(), dto.getTotalBorrowed()});
        }
    }

    private void loadTopMembersReport() {
        // Mục đích: xử lý logic của hàm loadTopMembersReport.
        currentLoader = this::loadTopMembersReport;
        currentTitle = "Báo cáo top thành viên";
        tableModel.setDataVector(new Object[][]{}, new String[]{"Thành viên", "Số lượt mượn", "Tổng tiền phạt"});
        for (MemberBorrowDTO dto : controller.topMembers()) {
            tableModel.addRow(new Object[]{dto.getMemberName(), dto.getTotalBorrowed(), dto.getTotalFines()});
        }
    }

    private void loadFineReport() {
        // Mục đích: xử lý logic của hàm loadFineReport.
        currentLoader = this::loadFineReport;
        currentTitle = "Báo cáo khoản phạt";
        tableModel.setDataVector(new Object[][]{}, new String[]{"Thành viên", "Phiếu mượn", "Số tiền", "Trạng thái"});
        for (FineReportDTO dto : controller.fineReports()) {
            tableModel.addRow(new Object[]{dto.getMemberName(), dto.getBorrowCode(), dto.getAmount(), dto.getPaymentStatus()});
        }
    }

    private void exportPdf() {
        // Mục đích: xử lý logic của hàm exportPdf.
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(currentTitle.replaceAll("\\s+", "_") + ".pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = PdfExportUtil.exportTable(table, currentTitle, chooser.getSelectedFile());
            UIHelper.info(this, "Đã xuất PDF tại: " + file.getAbsolutePath());
        }
    }

    @Override
    public void reloadData() {
        // Làm mới báo cáo dựa trên lựa chọn hiện tại.
        safeRun(currentLoader);
    }
}
