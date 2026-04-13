package com.example.librarymis.view.panel;

import com.example.librarymis.controller.MemberController;
import com.example.librarymis.controller.MembershipTypeController;
import com.example.librarymis.model.entity.Member;
import com.example.librarymis.model.entity.MembershipType;
import com.example.librarymis.util.DateUtil;
import com.example.librarymis.view.component.UIHelper;
import com.example.librarymis.view.panel.common.CrudPanelTemplate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MemberManagementPanel extends CrudPanelTemplate {
    private final MemberController controller = new MemberController();
    private final MembershipTypeController membershipTypeController = new MembershipTypeController();

    private final JTextField txtFullName = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JTextField txtPhone = new JTextField();
    private final JTextField txtAddress = new JTextField();
    private final JTextField txtJoinDate = new JTextField(DateUtil.format(LocalDate.now()));
    private final JTextField txtExpiryDate = new JTextField(DateUtil.format(LocalDate.now().plusYears(1)));
    private final JComboBox<MembershipType> cmbMembershipType = new JComboBox<>();
    private final JCheckBox chkActive = new JCheckBox("Thẻ còn hiệu lực", true);

    private final List<Member> displayedData = new ArrayList<>();
    private Member current;

    // Biến cho nền đốm sáng
    private List<Particle> particles;
    private Timer timer;

    public MemberManagementPanel() {
        // Tắt nền mặc định của thẻ cha
        setOpaque(false);
        setBackground(new Color(245, 250, 255));

        loadMembershipTypes();
        initLayout("Quản lý thành viên", "Theo dõi bạn đọc, loại thẻ và thời hạn sử dụng", new String[]{"ID", "Họ tên", "Email", "Điện thoại", "Loại thẻ", "Hết hạn", "Trạng thái"}, buildForm());
        
        // Gọi hàm trung gian để không báo vàng
        performInitialRefresh();

        // Khởi động đốm sáng bay lượn
        initParticles();
        startAnimation();

        // VŨ KHÍ HỦY DIỆT NỀN TRẮNG: Ép tàng hình và tròng Viền Kính Phát Sáng
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                makeTransparentAndApplyGlass(MemberManagementPanel.this);
                // Ép giao diện vẽ lại một lần nữa để xóa sạch nền trắng
                MemberManagementPanel.this.revalidate();
                MemberManagementPanel.this.repaint();
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
            
            // 1. Loại trừ các thành phần nhập liệu & nút bấm để giữ nền trắng dễ nhìn chữ
            boolean isInputOrButton = c instanceof JTextField || c instanceof JTextArea || 
                                      c instanceof JComboBox || c instanceof JButton || 
                                      c instanceof JCheckBox || c instanceof JLabel;
            
            // 2. ÉP TÀNG HÌNH DIỆT TẬN GỐC NỀN TRẮNG 
            if (!isInputOrButton && c instanceof JComponent) {
                JComponent jc = (JComponent) c;
                jc.setOpaque(false);
                jc.setBackground(new Color(0, 0, 0, 0)); 
            }
            
            // 3. Trị riêng JScrollPane
            if (c instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) c;
                sp.getViewport().setOpaque(false);
                sp.getViewport().setBackground(new Color(0, 0, 0, 0));
            }
            
            // 4. Ép luôn cái Bảng (JTable) tàng hình để thấy đốm sáng xuyên qua
            if (c instanceof JTable) {
                JTable tbl = (JTable) c;
                tbl.setOpaque(false);
                tbl.setBackground(new Color(0, 0, 0, 0));
            }
            
            // 5. Tròng viền kính phát sáng vào các khung bự
            if (c instanceof JPanel) {
                JPanel panel = (JPanel) c;
                javax.swing.border.Border b = panel.getBorder();
                boolean hasRealBorder = b != null && !(b instanceof EmptyBorder) && !(b instanceof GlowingGlassBorder);
                boolean isLargeCard = panel.getWidth() > 250 && panel.getHeight() > 200 && !hasGlassAncestor(panel);
                
                if (hasRealBorder || isLargeCard) {
                    panel.setBorder(new GlowingGlassBorder());
                }
            }
            
            // 6. Quét đệ quy chui sâu vào mọi ngóc ngách
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
    private void loadMembershipTypes() {
        cmbMembershipType.removeAllItems();
        for (MembershipType type : membershipTypeController.findAll()) {
            cmbMembershipType.addItem(type);
        }
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = baseGbc();
        addRow(panel, gbc, 0, "Họ tên", txtFullName);
        addRow(panel, gbc, 1, "Email", txtEmail);
        addRow(panel, gbc, 2, "Điện thoại", txtPhone);
        addRow(panel, gbc, 3, "Địa chỉ", txtAddress);
        addRow(panel, gbc, 4, "Ngày tham gia", txtJoinDate);
        addRow(panel, gbc, 5, "Ngày hết hạn", txtExpiryDate);
        addRow(panel, gbc, 6, "Loại thẻ", cmbMembershipType);
        gbc.gridx = 1; gbc.gridy = 7; gbc.weightx = 1;
        panel.add(chkActive, gbc);
        return panel;
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
        lbl.setForeground(new Color(40, 50, 60)); // Chữ đậm dễ đọc trên nền kính
        panel.add(lbl, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(comp, gbc);
    }

    @Override
    protected void refresh() {
        loadMembershipTypes();
        displayedData.clear();
        displayedData.addAll(controller.findAll());
        clearTable();
        for (Member m : displayedData) {
            tableModel.addRow(new Object[]{m.getId(), m.getFullName(), m.getEmail(), m.getPhone(),
                    m.getMembershipType() != null ? m.getMembershipType().getName() : "",
                    DateUtil.format(m.getExpiryDate()), m.isActive() ? "Đang hoạt động" : "Khóa"});
        }
        clearForm();
    }

    @Override
    protected void search(String keyword) {
        displayedData.clear();
        displayedData.addAll(controller.search(keyword));
        clearTable();
        for (Member m : displayedData) {
            tableModel.addRow(new Object[]{m.getId(), m.getFullName(), m.getEmail(), m.getPhone(),
                    m.getMembershipType() != null ? m.getMembershipType().getName() : "",
                    DateUtil.format(m.getExpiryDate()), m.isActive() ? "Đang hoạt động" : "Khóa"});
        }
    }

    @Override
    protected void saveEntity() {
        Member member = current == null ? new Member() : current;
        member.setFullName(txtFullName.getText().trim());
        member.setEmail(txtEmail.getText().trim());
        member.setPhone(txtPhone.getText().trim());
        member.setAddress(txtAddress.getText().trim());
        member.setJoinDate(DateUtil.parse(txtJoinDate.getText().trim()));
        member.setExpiryDate(DateUtil.parse(txtExpiryDate.getText().trim()));
        member.setMembershipType((MembershipType) cmbMembershipType.getSelectedItem());
        member.setActive(chkActive.isSelected());
        controller.save(member);
        UIHelper.info(this, "Lưu thành viên thành công");
        refresh();
    }

    @Override
    protected void deleteEntity() {
        if (current == null || current.getId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn thành viên cần xóa");
        }
        if (UIHelper.confirm(this, "Bạn có muốn xóa thành viên này?")) {
            controller.delete(current.getId());
            refresh();
        }
    }

    @Override
    protected void clearForm() {
        current = null;
        txtFullName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        txtJoinDate.setText(DateUtil.format(LocalDate.now()));
        txtExpiryDate.setText(DateUtil.format(LocalDate.now().plusYears(1)));
        if (cmbMembershipType.getItemCount() > 0) cmbMembershipType.setSelectedIndex(0);
        chkActive.setSelected(true);
        table.clearSelection();
        setCreateMode();
    }

    @Override
    protected void fillFormFromSelectedRow(int row) {
        current = displayedData.get(row);
        setEditMode();
        txtFullName.setText(current.getFullName());
        txtEmail.setText(current.getEmail());
        txtPhone.setText(current.getPhone());
        txtAddress.setText(current.getAddress());
        txtJoinDate.setText(DateUtil.format(current.getJoinDate()));
        txtExpiryDate.setText(DateUtil.format(current.getExpiryDate()));
        cmbMembershipType.setSelectedItem(current.getMembershipType());
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

            // 1. VẼ ÁNH SÁNG TỎA RA (GLOW EFFECT) - Màu 30
            for (int i = 0; i < glowSize; i++) {
                g2.setColor(new Color(0, 102, 204, 30 / (i + 1))); 
                g2.fillRoundRect(bx - i, by - i, bw + i * 2, bh + i * 2, radius + i, radius + i);
            }
            
            // 2. VẼ LỚP KÍNH CHÍNH - Màu trắng sữa Alpha 190
            g2.setColor(new Color(255, 255, 255, 190)); 
            g2.fillRoundRect(bx, by, bw, bh, radius, radius);
            
            // 3. VẼ VIỀN KÍNH BÓNG BẨY - Màu viền 220
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