package com.example.librarymis.view.panel.common;

import com.example.librarymis.view.component.UIHelper;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Template chung cho các màn hình CRUD (Create - Read - Update - Delete).
 * Bao gồm:
 * - Header (tiêu đề)
 * - Bảng dữ liệu (bên trái)
 * - Form nhập liệu (bên phải)
 * - Các nút thao tác (Thêm, Xóa, Làm mới...)
 */
public abstract class CrudPanelTemplate extends JPanel implements Reloadable {
    protected JTable table; // Bảng hiển thị dữ liệu
    protected DefaultTableModel tableModel; // Model của bảng để dễ dàng thao tác thêm/sửa/xóa dữ liệu
    protected JTextField txtSearch; // Ô nhập tìm kiếm
    protected JButton btnNew; // Nút làm mới form
    protected JButton btnSave; // Nút thêm / cập nhật
    protected JButton btnDelete; // Nút xóa
    protected JButton btnRefresh; // Nút reload dữ liệu
    protected JLabel lblFormMode; // Label hiển thị trạng thái form (create/edit)
    protected int selectedRow = -1; // Dòng đang chọn trong bảng

    /**
     * Khởi tạo layout tổng thể cho panel CRUD.
     *
     * @param title     Tiêu đề chính
     * @param subtitle  Tiêu đề phụ
     * @param columns   Danh sách cột của bảng
     * @param formPanel Panel form do class con truyền vào (quyết định layout form)
     */
    protected void initLayout(String title, String subtitle, String[] columns, JPanel formPanel) {
        // Mục đích: xử lý logic của hàm initLayout.
        setLayout(new BorderLayout(18, 18)); // Layout tổng thể, có khoảng cách
        setBackground(UIHelper.APP_BG); // Màu nền app
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Padding ngoài

        // Header

        add(UIHelper.pageHeader(title, subtitle), BorderLayout.NORTH);
        // Container trung tâm
        JPanel center = new JPanel(new BorderLayout(18, 18));
        center.setOpaque(false);

        // Bên trái: Bảng dữ liệu + toolbar
        JPanel tableCard = UIHelper.card(new BorderLayout(14, 14)); // Card chứa bảng
        JPanel tableToolbar = new JPanel(new BorderLayout(12, 0)); // Toolbar trên bảng
        tableToolbar.setOpaque(false);

        // Meta info (tiêu đề bảng)
        JPanel leftMeta = new JPanel();
        leftMeta.setOpaque(false);
        leftMeta.setLayout(new BoxLayout(leftMeta, BoxLayout.Y_AXIS));
        leftMeta.add(UIHelper.sectionTitle("Danh sách dữ liệu"));
        leftMeta.add(Box.createVerticalStrut(4));
        leftMeta.add(UIHelper.subtitle("Tìm kiếm, xem nhanh và chọn bản ghi để cập nhật"));

        // Thanh tìm kiếm
        JPanel searchRow = new JPanel(new BorderLayout(10, 0));
        searchRow.setOpaque(false);
        txtSearch = UIHelper.textField(); // Ô nhập tìm kiếm
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập từ khóa để tìm nhanh...");
        btnRefresh = UIHelper.secondaryButton("Làm mới"); // Reload
        JButton btnSearch = UIHelper.primaryButton("Tìm kiếm"); // Tìm kiếm

        // Nhóm nút bên phải
        JPanel searchActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchActions.setOpaque(false);
        searchActions.add(btnRefresh);
        searchActions.add(btnSearch);
        searchRow.add(txtSearch, BorderLayout.CENTER);
        searchRow.add(searchActions, BorderLayout.EAST);

        // Gộp toolbar
        JPanel toolbarWrap = new JPanel(new BorderLayout(0, 12));
        toolbarWrap.setOpaque(false);
        toolbarWrap.add(leftMeta, BorderLayout.NORTH);
        toolbarWrap.add(searchRow, BorderLayout.CENTER);
        tableCard.add(toolbarWrap, BorderLayout.NORTH);

        // TABLE MODEL
        tableModel = new DefaultTableModel(columns, 0) {
            // Mục đích: xử lý logic của hàm DefaultTableModel.
            @Override
            public boolean isCellEditable(int row, int column) {
                // Mục đích: xử lý logic của hàm isCellEditable.
                return false; // Không cho chỉnh sửa trực tiếp trên bảng
            }
        };
        // TABLE
        table = UIHelper.table(tableModel);
        tableCard.add(UIHelper.wrapTable(table), BorderLayout.CENTER);

        // FORM (RIGHT)
        JPanel formCard = UIHelper.card(new BorderLayout(14, 14));
        // Header của form
        JPanel formHeader = new JPanel();
        formHeader.setOpaque(false);
        formHeader.setLayout(new BoxLayout(formHeader, BoxLayout.Y_AXIS));
        formHeader.add(UIHelper.sectionTitle("Thông tin chi tiết"));
        formHeader.add(Box.createVerticalStrut(4));
        formHeader.add(UIHelper.subtitle("Điền thông tin để thêm mới hoặc chỉnh sửa bản ghi"));
        formCard.add(formHeader, BorderLayout.NORTH);
        // Form scroll (trường hợp form dài hơn chiều cao panel)
        JScrollPane formScroll = new JScrollPane(formPanel);
        // formPanel do class con truyền vào → layout của nó quyết định giao diện form
        formScroll.setBorder(null);
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        formScroll.getVerticalScrollBar().setUnitIncrement(16); // Cuộn mượt hơn

        formCard.add(formScroll, BorderLayout.CENTER);

        // Action buttons
        JPanel actionWrap = new JPanel(new BorderLayout(12, 0));
        actionWrap.setOpaque(false);

        // Label trạng thái form
        lblFormMode = new JLabel();
        lblFormMode.setOpaque(true);
        lblFormMode.setBackground(new Color(239, 246, 255));
        lblFormMode.setForeground(new Color(29, 78, 216));
        lblFormMode.setBorder(new EmptyBorder(8, 12, 8, 12));
        lblFormMode.setFont(lblFormMode.getFont().deriveFont(Font.BOLD, 13f));

        // Nhóm nút bên phải
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        btnNew = UIHelper.secondaryButton("Làm mới");
        btnDelete = UIHelper.secondaryButton("Xóa");
        btnSave = UIHelper.primaryButton("Thêm");
        actions.add(btnNew);
        actions.add(btnDelete);
        actions.add(btnSave);

        actionWrap.add(lblFormMode, BorderLayout.WEST);
        actionWrap.add(actions, BorderLayout.EAST);
        formCard.add(actionWrap, BorderLayout.SOUTH);

        // SPLIT PANE: Chia đôi giữa bảng và form
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableCard, formCard);
        splitPane.setResizeWeight(getSplitRatio());
        // Tỷ lệ chia: mặc định 0.6 (60% table - 40% form)

        splitPane.setBorder(null);
        splitPane.setOpaque(false);
        center.add(splitPane, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        // EVENTS
        btnSearch.addActionListener(e -> safeRun(() -> search(txtSearch.getText())));
        btnRefresh.addActionListener(e -> safeRun(this::refresh));
        btnNew.addActionListener(e -> safeRun(this::clearForm));
        btnSave.addActionListener(e -> safeRun(this::saveEntity));
        btnDelete.addActionListener(e -> safeRun(this::deleteEntity));

        // Khi chọn dòng trong bảng
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                selectedRow = table.convertRowIndexToModel(table.getSelectedRow());
                safeRun(() -> fillFormFromSelectedRow(selectedRow));
            }
        });

        setCreateMode(); // Mặc định là mode thêm mới
    }

    /**
     * Trả về tỷ lệ chia mặc định của JSplitPane.
     * Có thể override ở class con.
     */
    protected double getSplitRatio() {
        // Mục đích: trả về tỷ lệ mặc định cho JSplitPane.
        return 0.6;
    }

    /**
     * Wrapper để chạy logic an toàn, tránh crash UI.
     */
    protected void safeRun(Runnable task) {
        // Mục đích: xử lý logic của hàm safeRun.
        try {
            // Mục đích: xử lý logic của hàm safeRun.
            task.run();
        } catch (Exception e) {
            UIHelper.error(this, e.getMessage());
        }
    }

    protected void resetTableColumnWidth() {
        if (table == null)
            return;

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        int columnCount = table.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(150); // width mặc định
        }

        table.doLayout(); // refresh lại layout
    }

    /**
     * Reload dữ liệu khi panel được hiển thị lại.
     */
    @Override
    public void reloadData() {
        // Mục đích: làm mới dữ liệu khi được hiển thị lại.
        safeRun(() -> {
            refresh();
            resetTableColumnWidth(); // 👈 thêm dòng này
        });
    }

    /**
     * Xóa toàn bộ dữ liệu trong bảng.
     */
    protected void clearTable() {
        // Mục đích: xử lý logic của hàm clearTable.
        tableModel.setRowCount(0);
    }

    /**
     * Chuyển form về chế độ tạo mới.
     */
    protected void setCreateMode() {
        // Mục đích: xử lý logic của hàm setCreateMode.
        if (lblFormMode != null)
            lblFormMode.setText("Đang tạo mới bản ghi");
        if (btnSave != null)
            btnSave.setText("Thêm");
    }

    /**
     * Chuyển form sang chế độ chỉnh sửa.
     */
    protected void setEditMode() {
        // Mục đích: xử lý logic của hàm setEditMode.
        if (lblFormMode != null)
            lblFormMode.setText("Đang chỉnh sửa bản ghi đã chọn");
        if (btnSave != null)
            btnSave.setText("Cập nhật");
    }

    /**
     * Hiển thị thông báo thành công.
     */
    protected void showMessage(String msg) {
        // Mục đích: hiển thị thông báo thành công.
        JOptionPane.showMessageDialog(this, msg, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Hiển thị thông báo lỗi.
     */
    protected void showError(String msg) {
        // Mục đích: hiển thị thông báo lỗi.
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    // ABSTRACT METHODS - Các phương thức này do class con implement để xử lý logic
    // cụ thể cho từng loại dữ liệu
    protected abstract void refresh(); // Load lại dữ liệu

    protected abstract void search(String keyword); // Tìm kiếm

    protected abstract void saveEntity(); // Thêm / cập nhật

    protected abstract void deleteEntity(); // Xóa

    protected abstract void clearForm(); // Reset form

    protected abstract void fillFormFromSelectedRow(int row); // Đổ dữ liệu lên form
}
