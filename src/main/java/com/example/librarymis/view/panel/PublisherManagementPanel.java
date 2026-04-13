package com.example.librarymis.view.panel;

import com.example.librarymis.controller.PublisherController;
import com.example.librarymis.model.entity.Publisher;
import com.example.librarymis.view.component.UIHelper;
import com.example.librarymis.view.panel.common.CrudPanelTemplate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PublisherManagementPanel extends CrudPanelTemplate {
    private final PublisherController controller = new PublisherController();
    private final JTextField txtName = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JTextField txtPhone = new JTextField();
    private final JTextField txtAddress = new JTextField();
    private final List<Publisher> displayedData = new ArrayList<>();
    private Publisher current;

    // Biến cho nền đốm sáng
    private List<Particle> particles;
    private Timer timer;

    public PublisherManagementPanel() {
        // Tắt nền mặc định của thẻ cha
        setOpaque(false);
        setBackground(new Color(245, 250, 255));

        initLayout("Quản lý nhà xuất bản", "Theo dõi thông tin nhà xuất bản và đơn vị phát hành", new String[]{"ID", "Tên NXB", "Email", "Điện thoại", "Địa chỉ"}, buildForm());
        
        // Gọi hàm trung gian để tránh gạch vàng
        performInitialRefresh();

        // Khởi động đốm sáng bay lượn
        initParticles();
        startAnimation();

        // VŨ KHÍ HỦY DIỆT NỀN TRẮNG: Ép tàng hình và tròng Viền Kính Phát Sáng
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                makeTransparentAndApplyGlass(PublisherManagementPanel.this);
                PublisherManagementPanel.this.revalidate();
                PublisherManagementPanel.this.repaint();
            }
        });
    }

    private void performInitialRefresh() {
        refresh();
    }

    // =========================================================
    // THUẬT TOÁN QUÉT SÂU: ÉP TRONG SUỐT VÀ TÌM KHUNG ĐỂ TRÒNG KÍNH
    // =========================================================
    private void makeTransparentAndApplyGlass(Container parent) {
        for (Component c : parent.getComponents()) {
            
            boolean isInputOrButton = c instanceof JTextField || c instanceof JTextArea || 
                                      c instanceof JComboBox || c instanceof JButton || 
                                      c instanceof JCheckBox || c instanceof JLabel;
            
            // Ép tàng hình diệt tận gốc nền trắng
            if (!isInputOrButton && c instanceof JComponent) {
                JComponent jc = (JComponent) c;
                jc.setOpaque(false);
                jc.setBackground(new Color(0, 0, 0, 0)); 
            }
            
            // Trị riêng JScrollPane
            if (c instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) c;
                sp.getViewport().setOpaque(false);
                sp.getViewport().setBackground(new Color(0, 0, 0, 0));
            }
            
            // Trị riêng JTable
            if (c instanceof JTable) {
                JTable tbl = (JTable) c;
                tbl.setOpaque(false);
                tbl.setBackground(new Color(0, 0, 0, 0));
            }
            
            // Tròng viền kính phát sáng vào các khung bự
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
        // Đặt form vào thẻ wrapper hướng Bắc (NORTH) để các ô nhập không bị giãn thưa ra giữa
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setOpaque(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = baseGbc();
        addRow(form, gbc, 0, "Tên nhà xuất bản", txtName);
        addRow(form, gbc, 1, "Email", txtEmail);
        addRow(form, gbc, 2, "Điện thoại", txtPhone);
        addRow(form, gbc, 3, "Địa chỉ", txtAddress);
        
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
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        
        JLabel lbl = UIHelper.fieldLabel(label);
        lbl.setForeground(new Color(40, 50, 60)); // Chữ đậm dễ đọc trên nền sáng
        panel.add(lbl, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(comp, gbc);
    }

    @Override
    protected void refresh() {
        displayedData.clear();
        displayedData.addAll(controller.findAll());
        render(displayedData);
        clearForm();
    }

    @Override
    protected void search(String keyword) {
        displayedData.clear();
        displayedData.addAll(controller.search(keyword));
        render(displayedData);
    }

    private void render(List<Publisher> list) {
        clearTable();
        for (Publisher p : list) {
            tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getEmail(), p.getPhone(), p.getAddress()});
        }
    }

    @Override
    protected void saveEntity() {
        Publisher publisher = current == null ? new Publisher() : current;
        publisher.setName(txtName.getText().trim());
        publisher.setEmail(txtEmail.getText().trim());
        publisher.setPhone(txtPhone.getText().trim());
        publisher.setAddress(txtAddress.getText().trim());
        controller.save(publisher);
        UIHelper.info(this, "Lưu nhà xuất bản thành công");
        refresh();
    }

    @Override
    protected void deleteEntity() {
        if (current == null || current.getId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn nhà xuất bản cần xóa");
        }
        if (UIHelper.confirm(this, "Xóa nhà xuất bản đã chọn?")) {
            controller.delete(current.getId());
            refresh();
        }
    }

    @Override
    protected void clearForm() {
        current = null;
        txtName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        table.clearSelection();
        setCreateMode();
    }

    @Override
    protected void fillFormFromSelectedRow(int row) {
        current = displayedData.get(row);
        setEditMode();
        txtName.setText(current.getName());
        txtEmail.setText(current.getEmail());
        txtPhone.setText(current.getPhone());
        txtAddress.setText(current.getAddress());
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

            // 1. VẼ ÁNH SÁNG TỎA RA (GLOW EFFECT)
            for (int i = 0; i < glowSize; i++) {
                g2.setColor(new Color(0, 102, 204, 30 / (i + 1))); 
                g2.fillRoundRect(bx - i, by - i, bw + i * 2, bh + i * 2, radius + i, radius + i);
            }
            
            // 2. VẼ LỚP KÍNH CHÍNH (Màu sữa 190)
            g2.setColor(new Color(255, 255, 255, 190)); 
            g2.fillRoundRect(bx, by, bw, bh, radius, radius);
            
            // 3. VẼ VIỀN KÍNH BÓNG BẨY (220)
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