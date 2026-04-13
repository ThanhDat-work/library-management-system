package com.example.librarymis.view.panel;

import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.example.librarymis.controller.BorrowController;
import com.example.librarymis.controller.FineController;
import com.example.librarymis.controller.MemberController;
import com.example.librarymis.model.entity.BorrowRecord;
import com.example.librarymis.model.entity.Fine;
import com.example.librarymis.model.entity.Member;
import com.example.librarymis.model.enumtype.PaymentStatus;
import com.example.librarymis.util.DateUtil;
import com.example.librarymis.util.ValidationUtil;
import com.example.librarymis.view.component.UIHelper;
import com.example.librarymis.view.panel.common.CrudPanelTemplate;

public class FineManagementPanel extends CrudPanelTemplate {
    private final FineController controller = new FineController();
    private final MemberController memberController = new MemberController();
    private final BorrowController borrowController = new BorrowController();

    private final JTextField txtCode = new JTextField();
    private final JComboBox<Member> cmbMember = new JComboBox<>();
    private final JComboBox<BorrowRecord> cmbBorrowRecord = new JComboBox<>();
    private final JTextField txtAmount = new JTextField("0");
    private final JTextField txtStatus = new JTextField();
    private final JTextField txtCreatedDate = new JTextField(DateUtil.format(LocalDate.now()));
    private final JTextArea txtReason = new JTextArea(4, 20);

    private final List<Fine> displayedData = new ArrayList<>();
    private Fine current;
    private boolean isFillingForm = false;

    // Biến cho nền đốm sáng
    private List<Particle> particles;
    private Timer timer;

    public FineManagementPanel() {
        // Tắt nền mặc định của thẻ cha để vẽ đốm sáng
        setOpaque(false);
        setBackground(new Color(245, 250, 255));

        loadReferenceData();
        cmbMember.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                String text = (value instanceof Member m) ? m.getFullName() : "--Chọn Thành viên--";
                return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            }
        });
        cmbBorrowRecord.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                String text = (value instanceof BorrowRecord br) ? br.getCode() : "--Chọn Phiếu mượn--";
                return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            }
        });
        cmbBorrowRecord.addActionListener(e -> {
            if (isFillingForm)
                return;
            BorrowRecord record = (BorrowRecord) cmbBorrowRecord.getSelectedItem();

            if (record == null) {
                clearFormFields();
                return;
            }

            Fine existingFine = displayedData.stream()
                    .filter(f -> f.getBorrowRecord() != null && f.getBorrowRecord().getId().equals(record.getId()))
                    .findFirst()
                    .orElse(null);

            if (existingFine != null) {
                isFillingForm = true;
                try {
                    current = existingFine;
                    txtCode.setText(current.getCode());
                    txtAmount.setText(current.getAmount().toString());
                    txtStatus.setText(current.getPaymentStatus().toString());
                    txtCreatedDate.setText(DateUtil.format(current.getCreatedDate()));
                    txtReason.setText(current.getReason());
                    cmbMember.setSelectedItem(current.getMember());
                } finally {
                    isFillingForm = false;
                }
            } else {
                clearFormFields();
                if (record.getMember() != null) {
                    cmbMember.setSelectedItem(record.getMember());
                }
            }
        });

        txtCode.setEditable(false);
        txtAmount.setEditable(false);
        txtStatus.setEditable(false);
        txtCreatedDate.setEditable(false);
        txtReason.setEditable(false);
        cmbBorrowRecord.setEnabled(true);
        cmbMember.setEnabled(true);

        initLayout("Quản lý khoản phạt", "Xử lý tiền phạt phát sinh từ trả trễ hoặc vi phạm quy định",
                new String[] { "ID", "Mã phạt", "Thành viên", "Phiếu mượn", "Số tiền", "Trạng thái", "Ngày tạo" },
                buildForm());
        
        performInitialRefresh();

        // Khởi động đốm sáng bay lượn
        initParticles();
        startAnimation();

        // VŨ KHÍ HỦY DIỆT NỀN TRẮNG: Ép tàng hình và tròng Viền Kính Phát Sáng
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                makeTransparentAndApplyGlass(FineManagementPanel.this);
                FineManagementPanel.this.revalidate();
                FineManagementPanel.this.repaint();
            }
        });
    }

    private void performInitialRefresh() {
        refresh();
    }

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

    private void clearFormFields() {
        txtCode.setText("");
        txtAmount.setText("0");
        txtStatus.setText("");
        txtReason.setText("");
        txtCreatedDate.setText(DateUtil.format(LocalDate.now()));
        cmbMember.setSelectedItem(null);
    }

    private void loadReferenceData() {
        cmbMember.removeAllItems();
        cmbMember.addItem(null);
        for (Member member : memberController.findAll()) {
            cmbMember.addItem(member);
        }
        cmbBorrowRecord.removeAllItems();
        cmbBorrowRecord.addItem(null);
        for (BorrowRecord record : borrowController.findAll()) {
            cmbBorrowRecord.addItem(record);
        }
    }

    private JPanel buildForm() {
        txtReason.setLineWrap(true);
        txtReason.setWrapStyleWord(true);

        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setOpaque(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = baseGbc();
        addRow(form, gbc, 0, "Mã phạt", txtCode);
        addRow(form, gbc, 1, "Thành viên", cmbMember);
        addRow(form, gbc, 2, "Phiếu mượn", cmbBorrowRecord);
        addRow(form, gbc, 3, "Số tiền", txtAmount);
        addRow(form, gbc, 4, "Trạng thái", txtStatus);
        addRow(form, gbc, 5, "Ngày tạo", txtCreatedDate);
        addRow(form, gbc, 6, "Lý do", UIHelper.wrapArea(txtReason));
        
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
        lbl.setForeground(new Color(40, 50, 60)); 
        panel.add(lbl, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(comp, gbc);
    }

    @Override
    protected void refresh() {
        loadReferenceData();
        displayedData.clear();
        displayedData.addAll(controller.findAll());
        render(displayedData);
        clearForm();
    }

    @Override
    protected void search(String keyword) {
        String q = keyword == null ? "" : keyword.trim().toLowerCase();
        displayedData.clear();
        for (Fine fine : controller.findAll()) {
            if (q.isBlank()
                    || (fine.getCode() != null && fine.getCode().toLowerCase().contains(q))
                    || (fine.getMember() != null && fine.getMember().getFullName().toLowerCase().contains(q))
                    || (fine.getBorrowRecord() != null && fine.getBorrowRecord().getCode().toLowerCase().contains(q))) {
                displayedData.add(fine);
            }
        }
        render(displayedData);
    }

    private void render(List<Fine> list) {
        clearTable();
        for (Fine f : list) {
            tableModel.addRow(new Object[] {
                    f.getId(), f.getCode(),
                    f.getMember() != null ? f.getMember().getFullName() : "",
                    f.getBorrowRecord() != null ? f.getBorrowRecord().getCode() : "",
                    f.getAmount(), f.getPaymentStatus(), DateUtil.format(f.getCreatedDate())
            });
        }
    }

    @Override
    protected void saveEntity() {
        BorrowRecord selectedRecord = (BorrowRecord) cmbBorrowRecord.getSelectedItem();
        ValidationUtil.require(selectedRecord != null, "Chưa có Phiếu mượn. Vui lòng chọn một phiếu mượn hợp lệ.");
        ValidationUtil.require(cmbMember.getSelectedItem() != null, "Chưa có Thành viên.");

        Fine fine = current == null ? new Fine() : current;
        String code = txtCode.getText().trim();

        if (fine.getId() == null && code.isBlank()) {
            fine.setCode("FN" + System.currentTimeMillis());
        } else {
            fine.setCode(code.isBlank() ? null : code);
        }

        fine.setMember((Member) cmbMember.getSelectedItem());
        fine.setBorrowRecord((BorrowRecord) cmbBorrowRecord.getSelectedItem());
        fine.setAmount(ValidationUtil.parseBigDecimal(txtAmount.getText().trim(), "Số tiền không hợp lệ"));
        fine.setPaymentStatus(current == null ? PaymentStatus.UNPAID : current.getPaymentStatus());
        fine.setCreatedDate(DateUtil.parse(txtCreatedDate.getText().trim()));
        fine.setReason(txtReason.getText().trim());
        controller.save(fine);
        UIHelper.info(this, "Lưu khoản phạt thành công");
        refresh();
    }

    @Override
    protected void deleteEntity() {
        if (current == null || current.getId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn khoản phạt cần xóa");
        }
        if (UIHelper.confirm(this, "Bạn muốn xóa khoản phạt này?")) {
            controller.delete(current.getId());
            refresh();
        }
    }

    @Override
    protected void clearForm() {
        isFillingForm = true; 
        try {
            current = null;
            txtCode.setText("");
            txtAmount.setText("0"); 
            txtCreatedDate.setText(DateUtil.format(LocalDate.now()));
            txtReason.setText("");
            cmbMember.setSelectedIndex(0);
            cmbBorrowRecord.setSelectedIndex(0);
            txtStatus.setText(PaymentStatus.UNPAID.toString());
            table.clearSelection();
            setCreateMode();
        } finally {
            isFillingForm = false;
        }
    }

    @Override
    protected void fillFormFromSelectedRow(int row) {
        isFillingForm = true;
        try {
            current = displayedData.get(row);
            txtCode.setText(current.getCode());
            txtAmount.setText(String.valueOf(current.getAmount()));
            txtStatus.setText(current.getPaymentStatus().toString());
            txtCreatedDate.setText(DateUtil.format(current.getCreatedDate()));
            txtReason.setText(current.getReason());
            cmbMember.setSelectedItem(current.getMember());
            cmbBorrowRecord.setSelectedItem(current.getBorrowRecord());
            setEditMode();
        } finally {
            isFillingForm = false;
        }
    }

    // --- Animation & Border Core ---
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