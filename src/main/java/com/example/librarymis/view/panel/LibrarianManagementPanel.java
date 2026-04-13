package com.example.librarymis.view.panel;

import com.example.librarymis.controller.LibrarianController;
import com.example.librarymis.model.entity.Librarian;
import com.example.librarymis.util.SecurityContext;
import com.example.librarymis.view.component.UIHelper;
import com.example.librarymis.view.panel.common.CrudPanelTemplate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LibrarianManagementPanel extends CrudPanelTemplate {
    private final LibrarianController controller = new LibrarianController();

    private final JTextField txtUsername = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JTextField txtFullName = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JComboBox<String> cmbRole = new JComboBox<>(new String[] { "ADMIN", "STAFF" });
    private final JCheckBox chkActive = new JCheckBox("Đang hoạt động", true);

    private final List<Librarian> displayedData = new ArrayList<>();
    private Librarian current;

    private boolean isAuthorized = false;

    // Biến cho nền đốm sáng
    private List<Particle> particles;
    private Timer timer;

    public LibrarianManagementPanel() {
        // Tắt nền mặc định, thiết lập nền đốm sáng ngay từ đầu (áp dụng cho cả lúc bị khóa)
        setOpaque(false);
        setBackground(new Color(245, 250, 255));
        initParticles();
        startAnimation();

        // Lắng nghe sự kiện hiển thị để kiểm tra quyền truy cập
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (!isAuthorized) {
                    handleSecurityAccess();
                }
            }
        });
    }

    private void handleSecurityAccess() {
        if (SecurityContext.isAdmin()) {
            initializePanel();
            return;
        }

        while (true) {
            JPasswordField pf = new JPasswordField();
            Object[] message = {
                    "Mục Quản lý thủ thư yêu cầu quyền Admin hoặc Mã xác thực (Passcode):",
                    pf
            };

            int option = JOptionPane.showConfirmDialog(this, message,
                    "Xác thực bảo mật", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                String input = new String(pf.getPassword());
                if ("admin".equals(input)) { 
                    initializePanel();
                    break;
                } else {
                    int retryOption = JOptionPane.showOptionDialog(this,
                            "Mã xác thực (Passcode) không chính xác!\n\nHướng dẫn: Vui lòng liên hệ Quản trị viên (Admin) để lấy mã truy cập phần nhân sự.",
                            "Lỗi xác thực",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE,
                            null,
                            new String[] { "Thử lại", "Hủy" },
                            "Thử lại");

                    if (retryOption != JOptionPane.YES_OPTION) {
                        showLockedState();
                        break;
                    }
                }
            } else {
                showLockedState();
                break;
            }
        }
    }

    private void initializePanel() {
        isAuthorized = true;
        this.removeAll();
        
        initLayout("Quản lý thủ thư", "Tạo tài khoản đăng nhập và phân quyền nội bộ",
                new String[] { "ID", "Username", "Họ tên", "Email", "Vai trò", "Trạng thái" }, buildForm());
        
        if (tableModel != null) {
            refresh();
        }
        
        // VŨ KHÍ HỦY DIỆT NỀN TRẮNG: Ép tàng hình và tròng Viền Kính Phát Sáng sau khi load UI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                makeTransparentAndApplyGlass(LibrarianManagementPanel.this);
                LibrarianManagementPanel.this.revalidate();
                LibrarianManagementPanel.this.repaint();
            }
        });
    }

    private void showLockedState() {
        this.removeAll();
        setLayout(new BorderLayout());
        JLabel lblMessage = new JLabel("TRUY CẬP BỊ TỪ CHỐI - YÊU CẦU QUYỀN ADMIN", SwingConstants.CENTER);
        lblMessage.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblMessage.setForeground(Color.RED);
        add(lblMessage, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    // =========================================================
    // THUẬT TOÁN QUÉT SÂU: ÉP TRONG SUỐT VÀ TÌM KHUNG ĐỂ TRÒNG KÍNH
    // =========================================================
    private void makeTransparentAndApplyGlass(Container parent) {
        for (Component c : parent.getComponents()) {
            
            boolean isInputOrButton = c instanceof JTextField || c instanceof JTextArea || 
                                      c instanceof JComboBox || c instanceof JButton || 
                                      c instanceof JCheckBox || c instanceof JLabel;
            
            if (!isInputOrButton && c instanceof JComponent) {
                JComponent jc = (JComponent) c;
                jc.setOpaque(false);
                jc.setBackground(new Color(0, 0, 0, 0)); 
            }
            
            if (c instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) c;
                sp.getViewport().setOpaque(false);
                sp.getViewport().setBackground(new Color(0, 0, 0, 0));
            }
            
            if (c instanceof JTable) {
                JTable tbl = (JTable) c;
                tbl.setOpaque(false);
                tbl.setBackground(new Color(0, 0, 0, 0));
            }
            
            if (c instanceof JPanel) {
                JPanel panel = (JPanel) c;
                javax.swing.border.Border b = panel.getBorder();
                boolean hasRealBorder = b != null && !(b instanceof EmptyBorder) && !(b instanceof GlowingGlassBorder);
                boolean isLargeCard = panel.getWidth() > 250 && panel.getHeight() > 100 && !hasGlassAncestor(panel);
                
                if (hasRealBorder || isLargeCard) {
                    panel.setBorder(new GlowingGlassBorder());
                }
            }
            
            if (c instanceof Container) {
                makeTransparentAndApplyGlass((Container) c);
            }
        }
    }

    private boolean hasGlassAncestor(Component c) {
        Container p = c.getParent();
        while (p != null) {
            if (p instanceof JPanel) {
                if (((JPanel) p).getBorder() instanceof GlowingGlassBorder) {
                    return true;
                }
            }
            p = p.getParent();
        }
        return false;
    }

    // =========================================================
    // CÁC HÀM LOGIC GỐC (GIỮ NGUYÊN 100%)
    // =========================================================
    private JPanel buildForm() {
        // Gom nhóm lên trên để form nhập liệu không bị giãn ra quá đà
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setOpaque(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = baseGbc();
        addRow(form, gbc, 0, "Tên đăng nhập", txtUsername);
        addRow(form, gbc, 1, "Mật khẩu", txtPassword);
        addRow(form, gbc, 2, "Họ tên", txtFullName);
        addRow(form, gbc, 3, "Email", txtEmail);
        addRow(form, gbc, 4, "Vai trò", cmbRole);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1;
        form.add(chkActive, gbc);
        
        wrapper.add(form, BorderLayout.NORTH);
        return wrapper;
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, Component comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        
        JLabel lbl = UIHelper.fieldLabel(label);
        lbl.setForeground(new Color(40, 50, 60)); // Chữ đậm dễ đọc
        panel.add(lbl, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(comp, gbc);
    }

    @Override
    protected void refresh() {
        if (tableModel == null) return;
        
        tableModel.setRowCount(0);
        displayedData.clear();
        displayedData.addAll(controller.findAll());
        clearTable();
        for (Librarian l : displayedData) {
            tableModel.addRow(new Object[] { l.getId(), l.getUsername(), l.getFullName(), l.getEmail(), l.getRole(),
                    l.isActive() ? "Hoạt động" : "Khóa" });
        }
        clearForm();
    }

    @Override
    protected void search(String keyword) {
        displayedData.clear();
        displayedData.addAll(controller.search(keyword));
        clearTable();
        for (Librarian l : displayedData) {
            tableModel.addRow(new Object[] { l.getId(), l.getUsername(), l.getFullName(), l.getEmail(), l.getRole(),
                    l.isActive() ? "Hoạt động" : "Khóa" });
        }
    }

    @Override
    protected void saveEntity() {
        Librarian librarian = current == null ? new Librarian() : current;
        librarian.setUsername(txtUsername.getText().trim());
        String password = new String(txtPassword.getPassword()).trim();
        librarian.setPassword(password.isBlank() && current != null ? current.getPassword() : password);
        librarian.setFullName(txtFullName.getText().trim());
        librarian.setEmail(txtEmail.getText().trim());
        librarian.setRole(String.valueOf(cmbRole.getSelectedItem()));
        librarian.setActive(chkActive.isSelected());
        controller.save(librarian);
        UIHelper.info(this, "Lưu thủ thư thành công");
        refresh();
    }

    @Override
    protected void deleteEntity() {
        if (current == null || current.getId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn thủ thư cần xóa");
        }
        if (UIHelper.confirm(this, "Bạn muốn xóa thủ thư này?")) {
            controller.delete(current.getId());
            refresh();
        }
    }

    @Override
    protected void clearForm() {
        current = null;
        txtUsername.setText("");
        txtPassword.setText("");
        txtFullName.setText("");
        txtEmail.setText("");
        cmbRole.setSelectedIndex(1);
        chkActive.setSelected(true);
        table.clearSelection();
        setCreateMode();
    }

    @Override
    protected void fillFormFromSelectedRow(int row) {
        current = displayedData.get(row);
        setEditMode();
        txtUsername.setText(current.getUsername());
        txtPassword.setText("");
        txtFullName.setText(current.getFullName());
        txtEmail.setText(current.getEmail());
        cmbRole.setSelectedItem(current.getRole());
        chkActive.setSelected(current.isActive());
    }

    // =========================================================
    // CODE VẼ ĐỐM SÁNG XANH DƯƠNG BAY LƯỢN (LÀM NỀN TĨNH)
    // =========================================================
    private void initParticles() {
        particles = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < 70; i++) {
            particles.add(new Particle(r.nextInt(1500), r.nextInt(1000)));
        }
    }

    private void startAnimation() {
        timer = new Timer(30, e -> {
            for (Particle p : particles) {
                p.x += p.vx;
                p.y += p.vy;
                if (p.x < 0 || p.x > getWidth()) p.vx *= -1;
                if (p.y < 0 || p.y > getHeight()) p.vy *= -1;
            }
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Particle p : particles) {
            g2d.setColor(new Color(0, 102, 204, p.alpha));
            g2d.fill(new java.awt.geom.Ellipse2D.Double(p.x, p.y, p.size, p.size));

            for (Particle other : particles) {
                double dist = Math.hypot(p.x - other.x, p.y - other.y);
                if (dist < 130) {
                    g2d.setColor(new Color(0, 102, 204, (int) (45 * (1 - dist / 130))));
                    g2d.drawLine((int) p.x, (int) p.y, (int) other.x, (int) other.y);
                }
            }
        }
        g2d.dispose();
    }

    private class Particle {
        double x, y, vx, vy, size;
        int alpha;
        Particle(double x, double y) {
            this.x = x; this.y = y;
            Random r = new Random();
            this.vx = (r.nextDouble() - 0.5) * 1.5;
            this.vy = (r.nextDouble() - 0.5) * 1.5;
            this.size = r.nextInt(4) + 2;
            this.alpha = r.nextInt(100) + 50;
        }
    }

    // =========================================================
    // LỚP CORE: TẠO HIỆU ỨNG KÍNH MỜ CÓ HÀO QUANG DẠNG VIỀN
    // =========================================================
    private class GlowingGlassBorder extends javax.swing.border.AbstractBorder {
        private final int radius = 25;
        private final int glowSize = 8; 

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int bx = x + glowSize;
            int by = y + glowSize;
            int bw = width - glowSize * 2 - 1;
            int bh = height - glowSize * 2 - 1;

            for (int i = 0; i < glowSize; i++) {
                g2.setColor(new Color(0, 102, 204, 30 / (i + 1))); 
                g2.fillRoundRect(bx - i, by - i, bw + i * 2, bh + i * 2, radius + i, radius + i);
            }
            
            g2.setColor(new Color(255, 255, 255, 190)); 
            g2.fillRoundRect(bx, by, bw, bh, radius, radius);
            
            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(new Color(255, 255, 255, 220));
            g2.drawRoundRect(bx, by, bw, bh, radius, radius);
            
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(glowSize + 15, glowSize + 15, glowSize + 15, glowSize + 15);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(glowSize + 15, glowSize + 15, glowSize + 15, glowSize + 15);
            return insets;
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}