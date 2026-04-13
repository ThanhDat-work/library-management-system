package com.example.librarymis.view.panel;

import com.example.librarymis.controller.BorrowController;
import com.example.librarymis.model.entity.BorrowDetail;
import com.example.librarymis.model.entity.BorrowRecord;
import com.example.librarymis.util.DateUtil;
import com.example.librarymis.view.component.UIHelper;
import com.example.librarymis.view.panel.common.CrudPanelTemplate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReturningPanel extends CrudPanelTemplate {
    private final BorrowController controller = new BorrowController();

    private final JButton btnReturnBorrow = UIHelper.primaryButton("Xác nhận trả sách");
    private final JButton btnMarkAsLost = UIHelper.dangerButton("Báo mất toàn phiếu");

    private final JTextField txtReturnDate = new JTextField(DateUtil.format(LocalDate.now()));
    private final JLabel lblOverdueDays = new JLabel("Số ngày quá hạn: 0");
    private final JLabel lblLateFee = new JLabel("Tiền phạt quá hạn: 0 VNĐ");
    private final JLabel lblTotalPenalty = new JLabel("TỔNG THU: 0 VNĐ");
    private BigDecimal currentLateFeeValue = BigDecimal.ZERO;

    private final DefaultTableModel detailModel = new DefaultTableModel(
            new String[] { "ID", "Tên sách", "SL Mượn", "SL Trả", "Mô tả hư hại", "Tiền bồi thường" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column >= 3;
        }
    };
    private final JTable detailTable = UIHelper.table(detailModel);

    private final List<BorrowRecord> displayedData = new ArrayList<>();
    private BorrowRecord current;

    // Biến cho nền đốm sáng
    private List<Particle> particles;
    private Timer timer;

    public ReturningPanel() {
        setOpaque(false);
        setBackground(new Color(245, 250, 255));

        btnReturnBorrow.addActionListener(e -> safeRun(this::returnSelectedBorrow));
        btnMarkAsLost.addActionListener(e -> safeRun(this::markAsLost));

        detailModel.addTableModelListener(e -> updatePenaltyTotal());

        initLayout(
                "Quản lý trả sách",
                "Xử lý hoàn trả, ghi nhận hư hại và tính phí phạt quá hạn",
                new String[] { "ID", "Mã phiếu", "Thành viên", "Thủ thư", "Ngày mượn", "Hạn trả", "Trạng thái",
                        "Sách chưa trả" },
                buildForm());

        if (btnSave != null) btnSave.setVisible(false);
        if (btnDelete != null) btnDelete.setVisible(false);
        if (btnNew != null) btnNew.setVisible(false); 

        if (btnSave != null && btnSave.getParent() != null) {
            Container bottomActionPanel = btnSave.getParent();
            bottomActionPanel.add(btnMarkAsLost);
            bottomActionPanel.add(btnReturnBorrow);
        }

        refresh();

        initParticles();
        startAnimation();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                makeTransparentAndApplyGlass(ReturningPanel.this);
                ReturningPanel.this.revalidate();
                ReturningPanel.this.repaint();
            }
        });
    }

    private JPanel buildForm() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);

        // Dùng GridBagLayout xếp dọc gọn gàng để form không bị phình to
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(5, 5, 10, 5));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblOverdueDays.setForeground(new Color(40, 50, 60));
        lblLateFee.setForeground(new Color(40, 50, 60));
        lblTotalPenalty.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTotalPenalty.setForeground(new Color(220, 53, 69)); // Đỏ sang trọng

        // Hàng 1: Ngày trả
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel lblDate = UIHelper.fieldLabel("Ngày thực trả:");
        lblDate.setForeground(new Color(40, 50, 60));
        infoPanel.add(lblDate, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        infoPanel.add(txtReturnDate, gbc);

        // Hàng 2: Số ngày quá hạn
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        infoPanel.add(lblOverdueDays, gbc);

        // Hàng 3: Tiền phạt
        gbc.gridy = 2;
        infoPanel.add(lblLateFee, gbc);

        // Hàng 4: Tổng thu
        gbc.gridy = 3;
        infoPanel.add(lblTotalPenalty, gbc);

        JPanel detailPanel = new JPanel(new BorderLayout(8, 8));
        detailPanel.setOpaque(false);
        detailPanel.setBorder(BorderFactory.createTitledBorder("Cập nhật tình trạng trả từng đầu sách"));
        
        // TRẢ BẢNG VỀ TRẠNG THÁI MẶC ĐỊNH CHO PHÉP KÉO THẢ TỰ DO
        detailTable.setRowHeight(28); // Chỉ giữ lại chiều cao dòng cho đẹp mắt

        JScrollPane scrollPane = new JScrollPane(detailTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        detailPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(detailPanel, BorderLayout.CENTER);

        return mainPanel;
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
    @Override
    protected void saveEntity() {}

    @Override
    protected void deleteEntity() {}

    @Override
    protected void clearForm() {
        if (detailTable.isEditing()) {
            detailTable.getCellEditor().stopCellEditing();
        }

        current = null;
        if (detailModel.getRowCount() > 0) {
            detailModel.setRowCount(0);
        }

        lblOverdueDays.setText("Số ngày quá hạn: 0");
        lblLateFee.setText("Tiền phạt quá hạn: 0.00 VNĐ");
        lblTotalPenalty.setText("TỔNG THU: 0.00 VNĐ");
        currentLateFeeValue = BigDecimal.ZERO; 
        setCreateMode();
    }

    @Override
    protected void refresh() {
        clearTable();
        displayedData.clear();
        displayedData.addAll(controller.findActive());

        for (BorrowRecord r : displayedData) {
            String titles = r.getDetails().stream()
                    .map(d -> d.getBook().getTitle())
                    .reduce((a, b) -> a + ", " + b).orElse("");

            tableModel.addRow(new Object[] {
                    r.getId(), r.getCode(), r.getMember().getFullName(), r.getLibrarian().getFullName(),
                    DateUtil.format(r.getBorrowDate()), DateUtil.format(r.getDueDate()),
                    r.getBorrowStatus(), titles
            });
        }
        clearForm();
    }

    @Override
    protected void search(String keyword) {
        clearTable();
        for (BorrowRecord r : displayedData) {
            if (r.getCode().contains(keyword)
                    || r.getMember().getFullName().toLowerCase().contains(keyword.toLowerCase())) {
                String titles = r.getDetails().stream()
                        .map(d -> d.getBook().getTitle())
                        .reduce((a, b) -> a + ", " + b).orElse("");
                tableModel.addRow(new Object[] {
                        r.getId(), r.getCode(), r.getMember().getFullName(), r.getLibrarian().getFullName(),
                        DateUtil.format(r.getBorrowDate()), DateUtil.format(r.getDueDate()),
                        r.getBorrowStatus(), titles
                });
            }
        }
    }

    @Override
    protected void fillFormFromSelectedRow(int row) {
        if (row < 0 || row >= displayedData.size())
            return;
        current = displayedData.get(row);
        txtReturnDate.setText(DateUtil.format(LocalDate.now()));

        long days = ChronoUnit.DAYS.between(current.getDueDate(), LocalDate.now());
        if (days < 0) days = 0;

        BigDecimal finePerDay = new BigDecimal("5000.00");
        BigDecimal totalLateFee = BigDecimal.valueOf(days).multiply(finePerDay);
        this.currentLateFeeValue = totalLateFee; 

        lblOverdueDays.setText("Số ngày quá hạn: " + days);
        lblLateFee.setText("Tiền phạt quá hạn: " + String.format("%,.2f", totalLateFee) + " VNĐ");

        detailModel.setRowCount(0);
        for (BorrowDetail d : current.getDetails()) {
            detailModel.addRow(new Object[] {
                    d.getBook().getId(),
                    d.getBook().getTitle(),
                    d.getQuantity(),
                    d.getReturnedQuantity(), 
                    d.getDamageDescription() != null ? d.getDamageDescription() : "", 
                    d.getDamageCompensationAmount() != null ? d.getDamageCompensationAmount() : 0 
            });
        }
        updatePenaltyTotal();
        setEditMode();
    }

    private void updatePenaltyTotal() {
        if (current == null) return;

        BigDecimal compensationSum = BigDecimal.ZERO; 
        for (int i = 0; i < detailModel.getRowCount(); i++) {
            try {
                Object val = detailModel.getValueAt(i, 5);
                if (val != null) {
                    String cleanVal = val.toString().replaceAll("[^0-9.]", "");
                    if (!cleanVal.isEmpty()) {
                        compensationSum = compensationSum.add(new BigDecimal(cleanVal));
                    }
                }
            } catch (Exception ignored) { }
        }

        BigDecimal lateFee = this.currentLateFeeValue; 
        BigDecimal total = lateFee.add(compensationSum);
        lblTotalPenalty.setText("TỔNG THU: " + String.format("%,.2f", total) + " VNĐ");
    }

    private void returnSelectedBorrow() {
        if (current == null) {
            showError("Vui lòng chọn phiếu mượn!");
            return;
        }

        if (!UIHelper.confirm(this, "Xác nhận hoàn tất thủ tục trả sách và thu phí?"))
            return;

        try {
            List<BorrowDetail> updatedDetails = new ArrayList<>();
            for (int i = 0; i < detailModel.getRowCount(); i++) {
                Long bookId = (Long) detailModel.getValueAt(i, 0);
                int returnedQty = Integer.parseInt(detailModel.getValueAt(i, 3).toString());
                String damageNote = detailModel.getValueAt(i, 4).toString();
                BigDecimal compensation = new BigDecimal(detailModel.getValueAt(i, 5).toString().replace(",", ""));

                BorrowDetail d = current.getDetails().stream()
                        .filter(dt -> dt.getBook().getId().equals(bookId))
                        .findFirst().orElseThrow();

                d.setReturnedQuantity(returnedQty);
                d.setDamageDescription(damageNote);
                d.setDamageCompensationAmount(compensation);
                updatedDetails.add(d);
            }

            controller.returnBook(current.getId(), updatedDetails);
            showMessage("Đã xử lý trả sách thành công!");
            refresh();
        } catch (Exception ex) {
            showError("Lỗi: " + ex.getMessage());
        }
    }

    private void markAsLost() {
        if (current == null) {
            showError("Vui lòng chọn một phiếu mượn để báo mất!");
            return;
        }

        if (UIHelper.confirm(this,
                "Bạn có chắc chắn muốn đánh dấu mất toàn bộ sách trong phiếu này? \nThao tác này sẽ giảm số lượng sách trong kho và có thể phát sinh phí bồi thường.")) {
            try {
                List<BorrowDetail> lostDetails = new ArrayList<>();

                for (BorrowDetail d : current.getDetails()) {
                    int unreturned = d.getQuantity() - d.getReturnedQuantity();
                    if (unreturned <= 0) continue;

                    String input = JOptionPane.showInputDialog(this,
                            "Nhập tiền bồi thường cho sách: " + d.getBook().getTitle() + "\n(Số lượng mất: "
                                    + unreturned + ")",
                            "Bồi thường sách mất",
                            JOptionPane.QUESTION_MESSAGE);

                    if (input == null) return;

                    BigDecimal amount = BigDecimal.ZERO;
                    try {
                        if (!input.trim().isEmpty()) {
                            amount = new BigDecimal(input.trim());
                        }
                        if (amount.compareTo(BigDecimal.ZERO) < 0) {
                            throw new NumberFormatException("Số tiền bồi thường không thể âm.");
                        }
                    } catch (NumberFormatException e) {
                        showError("Số tiền bồi thường không hợp lệ: " + e.getMessage());
                        return;
                    }

                    BorrowDetail update = new BorrowDetail();
                    update.setBook(d.getBook());
                    update.setDamageCompensationAmount(amount);
                    lostDetails.add(update);
                }

                controller.markAsLost(current.getId(), lostDetails);
                showMessage("Đã cập nhật trạng thái mất sách và ghi nhận bồi thường (nếu có).");
                refresh();
            } catch (Exception ex) {
                showError("Lỗi đánh dấu mất sách: " + ex.getMessage());
            }
        }
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