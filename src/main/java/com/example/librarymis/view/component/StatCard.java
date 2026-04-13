package com.example.librarymis.view.component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Component UI dạng "card" dùng để hiển thị thống kê (dashboard)
 * Ví dụ: Tổng sách, Tổng người dùng, v.v.
 */
public class StatCard extends JPanel {
    /**
     * Label hiển thị giá trị chính (ví dụ: 100, 500,...)
     */
    private final JLabel valueLabel = new JLabel("0");
    /**
     * Label tiêu đề (ví dụ: "Tổng sách")
     */
    private final JLabel titleLabel = new JLabel("Metric");
    /**
     * Label ghi chú (ví dụ: "Tăng 5% so với tháng trước")
     */
    private final JLabel noteLabel = new JLabel("");

    /**
     * Constructor tạo card
     *
     * @param title tiêu đề
     * @param value giá trị
     * @param note  ghi chú
     */
    public StatCard(String title, String value, String note) {
        // Layout theo trục dọc (Y_AXIS)
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // Màu nền trắng (card style)
        setBackground(Color.WHITE);
        // Border gồm:
        // - viền ngoài bo góc
        // - padding bên trong
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(18, 18, 18, 18)));

        // Badge nhỏ phía trên ("Tổng quan")
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badge.setOpaque(false);
        JLabel chip = new JLabel("Tổng quan");
        chip.setOpaque(true);
        // Background xanh nhạt
        chip.setBackground(new Color(239, 246, 255));
        // Text xanh đậm
        chip.setForeground(new Color(29, 78, 216));
        // Padding cho chip
        chip.setBorder(new EmptyBorder(4, 8, 4, 8));
        badge.add(chip);

        // Setup title
        titleLabel.setText(title);
        titleLabel.setForeground(new Color(100, 116, 139));
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        // Setup value (quan trọng nhất)
        valueLabel.setText(value);
        valueLabel.setForeground(new Color(15, 23, 42));
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 30f));
        // Setup note
        noteLabel.setText(note);
        noteLabel.setForeground(new Color(100, 116, 139));

        // Add component theo thứ tự hiển thị
        add(badge);
        add(Box.createVerticalStrut(14)); // khoảng cách
        add(titleLabel);
        add(Box.createVerticalStrut(10));
        add(valueLabel);
        add(Box.createVerticalStrut(8));
        add(noteLabel);
    }

    /**
     * Cập nhật giá trị hiển thị của card
     */
    public void updateValue(String value, String note) {
        // Mục đích: xử lý logic của hàm updateValue.
        valueLabel.setText(value);
        noteLabel.setText(note);
    }
}
