package com.example.librarymis.view.component;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Utility class hỗ trợ tạo UI component đồng bộ style
 * → đảm bảo toàn bộ app có cùng design system
 */
public final class UIHelper {
    // COLOR SYSTEM (Design System)
    public static final Color APP_BG = new Color(244, 247, 251);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color BORDER = new Color(226, 232, 240);
    public static final Color TEXT = new Color(15, 23, 42);
    public static final Color MUTED = new Color(100, 116, 139);
    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color PRIMARY_DARK = new Color(29, 78, 216);
    public static final Color SIDEBAR = new Color(15, 23, 42);
    public static final Color SIDEBAR_HOVER = new Color(30, 41, 59);
    public static final Color SUCCESS_BG = new Color(239, 246, 255);

    /**
     * Constructor private
     */
    private UIHelper() {
        // Mục đích: xử lý logic của hàm UIHelper.
    }

    /**
     * Tạo panel dạng card với layout tùy chọn
     */
    public static JPanel card(LayoutManager layout) {
        // Mục đích: xử lý logic của hàm card.
        JPanel panel = new JPanel(layout);
        panel.setBackground(CARD_BG);
        // Áp dụng border chuẩn card
        panel.setBorder(createCardBorder(18));
        return panel;
    }

    /**
     * Tạo border chuẩn card (bo góc + padding)
     */
    public static Border createCardBorder(int padding) {
        // Mục đích: xử lý logic của hàm createCardBorder.
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(padding, padding, padding, padding));
    }

    /**
     * Button chính (Primary action)
     */
    public static JButton primaryButton(String text) {
        // Mục đích: xử lý logic của hàm primaryButton.
        JButton button = baseButton(text);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_DARK, 1, true),
                new EmptyBorder(10, 16, 10, 16)));
        return button;
    }

    /**
     * Button phụ (Secondary)
     */
    public static JButton secondaryButton(String text) {
        // Mục đích: xử lý logic của hàm secondaryButton.
        JButton button = baseButton(text);
        button.setBackground(Color.WHITE);
        button.setForeground(TEXT);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(10, 16, 10, 16)));
        return button;
    }

    /**
     * Button nguy hiểm (Delete, cảnh báo)
     */
    public static JButton dangerButton(String text) {
        JButton button = baseButton(text);
        // Màu nền đỏ (Red) cảnh báo
        button.setBackground(new Color(220, 38, 38));
        button.setForeground(Color.WHITE);
        // Viền đỏ sẫm hơn một chút
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(185, 28, 28), 1, true),
                new EmptyBorder(10, 16, 10, 16)));
        return button;
    }

    /**
     * Base button dùng chung
     */
    private static JButton baseButton(String text) {
        // Mục đích: xử lý logic của hàm baseButton.
        JButton button = new JButton(text);
        button.setFocusPainted(false); // bỏ viền focus
        button.putClientProperty("JButton.buttonType", "roundRect"); // hỗ trợ flatlaf
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(button.getFont().deriveFont(Font.BOLD, 13f));
        return button;
    }

    /**
     * TextField chuẩn UI
     */
    public static JTextField textField() {
        // Mục đích: xử lý logic của hàm textField.
        JTextField field = new JTextField();
        field.putClientProperty("JTextField.placeholderText", "");
        field.putClientProperty("JComponent.roundRect", true);
        return field;
    }

    /**
     * PasswordField chuẩn UI
     */
    public static JPasswordField passwordField() {
        // Mục đích: xử lý logic của hàm passwordField.
        JPasswordField field = new JPasswordField();
        field.putClientProperty("JTextField.placeholderText", "");
        field.putClientProperty("JComponent.roundRect", true);
        return field;
    }

    /**
     * Tạo JTable với style chuẩn
     */
    public static JTable table(javax.swing.table.TableModel model) {
        // Mục đích: xử lý logic của hàm table.
        JTable table = new JTable(model);
        table.setRowHeight(38);
        table.setFillsViewportHeight(true);
        // chỉ chọn 1 dòng tại một thời điểm
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // style grid
        table.setGridColor(new Color(241, 245, 249));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        // màu selection
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT);
        table.setBackground(Color.WHITE);
        table.setForeground(TEXT);

        // HEADER STYLE
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT);
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        // CELL RENDERER
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        // padding cho cell
        renderer.setBorder(new EmptyBorder(0, 10, 0, 10));
        table.setDefaultRenderer(Object.class, renderer);
        return table;
    }

    /**
     * Title lớn
     */
    public static JLabel title(String text) {
        // Mục đích: xử lý logic của hàm title.
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 24f));
        return label;
    }

    /**
     * Subtitle
     */
    public static JLabel subtitle(String text) {
        // Mục đích: xử lý logic của hàm subtitle.
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(label.getFont().deriveFont(14f));
        return label;
    }

    /**
     * Section title
     */
    public static JLabel sectionTitle(String text) {
        // Mục đích: xử lý logic của hàm sectionTitle.
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        return label;
    }

    /**
     * Label cho field input
     */
    public static JLabel fieldLabel(String text) {
        // Mục đích: xử lý logic của hàm fieldLabel.
        JLabel label = new JLabel(text);
        label.setForeground(new Color(51, 65, 85));
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        return label;
    }

    /**
     * Header của page (title + subtitle)
     */
    public static JPanel pageHeader(String title, String subtitle) {
        // Mục đích: xử lý logic của hàm pageHeader.
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(UIHelper.title(title));
        left.add(Box.createVerticalStrut(6));
        left.add(UIHelper.subtitle(subtitle));
        panel.add(left, BorderLayout.WEST);
        return panel;
    }

    /**
     * Wrap JTable vào JScrollPane
     */
    public static JScrollPane wrapTable(JTable table) {
        // Mục đích: xử lý logic của hàm wrapTable.
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    /**
     * Wrap JTextArea vào JScrollPane
     */
    public static JScrollPane wrapArea(JTextArea area) {
        // Mục đích: xử lý logic của hàm wrapArea.
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    /**
     * Hiển thị thông báo info
     */
    public static void info(Component parent, String message) {
        // Mục đích: xử lý logic của hàm info.
        JOptionPane.showMessageDialog(parent, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Hiển thị thông báo lỗi
     */
    public static void error(Component parent, String message) {
        // Mục đích: xử lý logic của hàm error.
        JOptionPane.showMessageDialog(parent, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Hiển thị confirm dialog (Yes/No)
     */
    public static boolean confirm(Component parent, String message) {
        // Mục đích: xử lý logic của hàm confirm.
        return JOptionPane.showConfirmDialog(parent, message, "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
