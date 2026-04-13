package com.example.librarymis.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.example.librarymis.controller.AuthController;
import com.example.librarymis.model.entity.Librarian;
import com.example.librarymis.view.component.UIHelper;
import com.formdev.flatlaf.FlatClientProperties;

public class LoginView extends JFrame {
    private final AuthController authController = new AuthController();

    // Đã xóa giá trị mặc định ("admin", "admin123") để ô nhập liệu trống trơn khi
    // mở
    private final JTextField txtUsername = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();

    public LoginView() {
        setTitle("Library MIS - Đăng nhập");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);

        // Giữ lại 3 nút Phóng to/Thu nhỏ/Tắt của hệ thống
        getRootPane().putClientProperty(FlatClientProperties.USE_WINDOW_DECORATIONS, true);
        getRootPane().putClientProperty("JRootPane.titleBarBackground", new Color(10, 12, 30));
        getRootPane().putClientProperty("JRootPane.titleBarForeground", Color.WHITE);

        // Panel nền chòm sao
        ConstellationPanel backgroundPanel = new ConstellationPanel();
        backgroundPanel.setLayout(new GridBagLayout());

        // Canh giữa hộp Login Glass
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;

        backgroundPanel.add(buildGlowingGlassCard(), gbc);

        setContentPane(backgroundPanel);
    }

    private JPanel buildGlowingGlassCard() {
        GlowingGlassPanel card = new GlowingGlassPanel(30);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(50, 50, 50, 50));
        card.setPreferredSize(new Dimension(420, 440));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        // Tiêu đề
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel title = new JLabel("Đăng nhập");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 40));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(0, 0, 40, 0);
        card.add(title, gbc);

        // Ô Tên đăng nhập
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 25, 0);
        card.add(createUnderlineFieldRow("Tên đăng nhập", txtUsername), gbc);

        // Ô Mật khẩu
        gbc.gridy++;
        card.add(createUnderlineFieldRow("Mật khẩu", txtPassword), gbc);

        // Đẩy nút bấm xuống dưới
        gbc.gridy++;
        gbc.weighty = 1.0;
        card.add(Box.createVerticalGlue(), gbc);
        gbc.weighty = 0.0;

        // Nút Đăng nhập
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 15, 0);
        GradientButton btnLogin = new GradientButton("Đăng nhập");
        btnLogin.setPreferredSize(new Dimension(0, 45));
        btnLogin.addActionListener(e -> login());
        card.add(btnLogin, gbc);
        getRootPane().setDefaultButton(btnLogin);

        // Footer
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel footerText = new JLabel("<html><u>Chưa có tài khoản?</u></html>");
        footerText.setForeground(new Color(220, 220, 220));
        footerText.setFont(new Font("SansSerif", Font.PLAIN, 13));
        footerText.setHorizontalAlignment(SwingConstants.CENTER);
        footerText.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        footerText.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                javax.swing.JOptionPane.showMessageDialog(LoginView.this,
                        "Vui lòng liên hệ thủ thư tại quầy để được hỗ trợ tạo tài khoản mới. Trân trọng!",
                        "Thông báo",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
        });
        card.add(footerText, gbc);

        return card;
    }

    private JPanel createUnderlineFieldRow(String labelText, JTextField textField) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);
        row.setBackground(new Color(0, 0, 0, 0));

        JLabel label = new JLabel(labelText);
        label.setForeground(new Color(240, 240, 240));
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        textField.setOpaque(false);
        textField.setBackground(new Color(0, 0, 0, 0));
        textField.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        textField.setForeground(Color.WHITE);
        textField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        textField.setCaretColor(Color.WHITE);

        textField.putClientProperty(FlatClientProperties.STYLE, "background: null; border: null;");

        if (textField instanceof JPasswordField passwordField) {
            passwordField.setEchoChar('•');
        }

        // Dòng kẻ dưới
        JPanel underline = new JPanel();
        underline.setBackground(Color.WHITE);
        underline.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        underline.setPreferredSize(new Dimension(0, 1));
        underline.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(label);
        row.add(textField);
        row.add(underline);

        return row;
    }

    private void login() {
        try {
            Librarian librarian = authController
                    .login(txtUsername.getText().trim(), new String(txtPassword.getPassword()).trim())
                    .orElseThrow(() -> new IllegalArgumentException("Sai tài khoản hoặc mật khẩu"));
            com.example.librarymis.util.SecurityContext.setCurrentUser(librarian);
            SwingUtilities.invokeLater(() -> {
                new MainView(librarian).setVisible(true);
                dispose();
            });
        } catch (Exception e) {
            UIHelper.error(this, e.getMessage());
        }
    }

    // =========================================================
    // CODE VẼ NỀN CHÒM SAO (ĐÃ LÀM NHẠT VÀ ÍT ĐI)
    // =========================================================
    private static class ConstellationPanel extends JPanel {
        private final List<Node> nodes;
        // Đã giảm số lượng đốm sáng từ 120 xuống 65 để mạng lưới thưa hơn
        private final int NUM_NODES = 65;
        private final int CONNECT_DIST = 140;

        public ConstellationPanel() {
            setOpaque(true);
            nodes = new ArrayList<>();

            Timer timer = new Timer(20, e -> {
                int w = getWidth();
                int h = getHeight();
                if (w == 0 || h == 0 || nodes.isEmpty())
                    return;

                for (Node n : nodes) {
                    n.x += n.vx;
                    n.y += n.vy;
                    if (n.x < 0)
                        n.x = w;
                    if (n.x > w)
                        n.x = 0;
                    if (n.y < 0)
                        n.y = h;
                    if (n.y > h)
                        n.y = 0;
                }
                repaint();
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (nodes.isEmpty() && getWidth() > 0) {
                Random rand = new Random();
                for (int i = 0; i < NUM_NODES; i++) {
                    double vx = (rand.nextDouble() - 0.5) * 1.2; // Bay chậm lại chút cho mượt
                    double vy = (rand.nextDouble() - 0.5) * 1.2;
                    nodes.add(new Node(rand.nextInt(getWidth()), rand.nextInt(getHeight()), vx, vy));
                }
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // GradientPaint gp = new GradientPaint(0, 0, new Color(10, 12, 30), getWidth(), getHeight(),
            //         new Color(25, 20, 50));
            // g2.setPaint(gp);
            // g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(new Color(3, 58, 115));
            g2.fillRect(0, 0, getWidth(), getHeight());

            for (int i = 0; i < nodes.size(); i++) {
                Node n1 = nodes.get(i);

                // LÀM NHẠT ĐỐM SÁNG: Giảm opacity từ 200 xuống 80
                g2.setColor(new Color(11, 111, 163, 80));
                g2.fillOval((int) n1.x - 2, (int) n1.y - 2, 4, 4);

                for (int j = i + 1; j < nodes.size(); j++) {
                    Node n2 = nodes.get(j);
                    double dist = Math.hypot(n1.x - n2.x, n1.y - n2.y);
                    if (dist < CONNECT_DIST) {
                        // LÀM NHẠT DÂY NỐI: Giảm max opacity từ 255 xuống 60
                        int alpha = (int) (60 * (1.0 - (dist / CONNECT_DIST)));
                        alpha = Math.max(0, Math.min(255, alpha));
                        g2.setColor(new Color(135,206,250, alpha));
                        g2.setStroke(new BasicStroke(0.6f)); // Nét mỏng lại
                        g2.drawLine((int) n1.x, (int) n1.y, (int) n2.x, (int) n2.y);
                    }
                }
            }
            g2.dispose();
        }

        private static class Node {
            double x, y, vx, vy;

            Node(double x, double y, double vx, double vy) {
                this.x = x;
                this.y = y;
                this.vx = vx;
                this.vy = vy;
            }
        }
    }

    // =========================================================
    // CODE VẼ HỘP KÍNH CÓ ÁNH SÁNG TỎA RA (GLOWING GLASS)
    // =========================================================
    private static class GlowingGlassPanel extends JPanel {
        private final int cornerRadius;
        private final int glowSize = 15;

        public GlowingGlassPanel(int cornerRadius) {
            this.cornerRadius = cornerRadius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = glowSize;
            int y = glowSize;
            int w = getWidth() - glowSize * 2;
            int h = getHeight() - glowSize * 2;

            for (int i = 0; i < glowSize; i++) {
                g2.setColor(new Color(135,206,250, 30 / (i + 1)));
                g2.fillRoundRect(x - i, y - i, w + i * 2, h + i * 2, cornerRadius + i, cornerRadius + i);
            }

            RoundRectangle2D shape = new RoundRectangle2D.Double(x, y, w, h, cornerRadius, cornerRadius);

            g2.setColor(new Color(135,206,250, 30));
            g2.fill(shape);

            g2.setColor(new Color(135,206,250, 180));
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(shape);

            g2.dispose();
        }
    }

    // =========================================================
    // NÚT BẤM BÓNG BẨY
    // =========================================================
    private static class GradientButton extends JButton {
        public GradientButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("SansSerif", Font.BOLD, 16));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(0, 0, new Color(111, 203, 226), 0, getHeight(), new Color(11, 111, 163));
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());

            g2.setColor(new Color(255, 255, 255, 50));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());

            g2.dispose();
            super.paintComponent(g);
        }
    }
}