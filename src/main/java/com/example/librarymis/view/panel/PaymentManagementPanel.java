package com.example.librarymis.view.panel;

import com.example.librarymis.controller.FineController;
import com.example.librarymis.controller.LibrarianController;
import com.example.librarymis.controller.MemberController;
import com.example.librarymis.controller.PaymentController;
import com.example.librarymis.model.entity.Fine;
import com.example.librarymis.model.entity.Librarian;
import com.example.librarymis.model.entity.Member;
import com.example.librarymis.model.entity.Payment;
import com.example.librarymis.model.enumtype.PaymentStatus;
import com.example.librarymis.util.DateUtil;
import com.example.librarymis.util.ValidationUtil;
import com.example.librarymis.view.component.UIHelper;
import com.example.librarymis.view.panel.common.CrudPanelTemplate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.util.Random;

public class PaymentManagementPanel extends CrudPanelTemplate {
    private final PaymentController controller = new PaymentController();
    private final MemberController memberController = new MemberController();
    private final LibrarianController librarianController = new LibrarianController();
    private final FineController fineController = new FineController();

    private final JTextField txtCode = new JTextField();
    private final JComboBox<Member> cmbMember = new JComboBox<>();
    private final JComboBox<Librarian> cmbLibrarian = new JComboBox<>();
    private final JComboBox<Fine> cmbFine = new JComboBox<>();
    private final JTextField txtAmount = new JTextField("0");
    private final JTextField txtPaymentDate = new JTextField(DateUtil.format(LocalDate.now()));
    private final JComboBox<String> cmbMethod = new JComboBox<>(new String[] { "CASH", "TRANSFER", "CARD" });
    private final JTextField txtStatus = new JTextField(PaymentStatus.PAID.toString());
    private final JTextArea txtNote = new JTextArea(4, 20);

    private final List<Payment> displayedData = new ArrayList<>();
    private Payment current;

    // Biến cho nền đốm sáng
    private List<Particle> particles;
    private Timer timer;

    public PaymentManagementPanel() {
        // Tắt nền mặc định của thẻ cha
        setOpaque(false);
        setBackground(new Color(245, 250, 255));

        loadReferenceData();
        cmbFine.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                Fine f = (Fine) value;
                String text = f == null ? "-- Không gắn khoản phạt --"
                        : String.format("[%s] %s - %,.2f VNĐ (%s)",
                                f.getCode(), f.getMember().getFullName(), f.getAmount(), f.getPaymentStatus());
                return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            }
        });
        
        cmbFine.addActionListener(e -> {
            Fine fine = (Fine) cmbFine.getSelectedItem();
            if (fine != null) {
                // Tự động đổ dữ liệu thành viên từ khoản phạt
                cmbMember.setSelectedItem(fine.getMember());
                // Tự động đổ số tiền cần thanh toán
                txtAmount.setText(String.valueOf(fine.getAmount()));
                // Tự động chọn thủ thư liên quan đến phiếu mượn gây ra khoản phạt này
                if (fine.getBorrowRecord() != null && fine.getBorrowRecord().getLibrarian() != null) {
                    cmbLibrarian.setSelectedItem(fine.getBorrowRecord().getLibrarian());
                }
                // Tự động điền ghi chú dựa trên lý do phạt để tiện đối chiếu
                txtNote.setText("Thanh toán cho: " + (fine.getReason() != null ? fine.getReason() : fine.getCode()));
            }
        });

        txtStatus.setEditable(false); // Trạng thái thanh toán mặc định là PAID và không được sửa
        initLayout("Quản lý thanh toán", "Ghi nhận thu tiền phạt và các khoản thanh toán thư viện",
                new String[] { "ID", "Mã", "Thành viên", "Thủ thư", "Khoản phạt", "Số tiền", "Trạng thái", "Ngày" },
                buildForm());
        
        performInitialRefresh();

        // Khởi động đốm sáng bay lượn
        initParticles();
        startAnimation();

        // VŨ KHÍ HỦY DIỆT NỀN TRẮNG: Ép tàng hình và tròng Viền Kính Phát Sáng
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                makeTransparentAndApplyGlass(PaymentManagementPanel.this);
                PaymentManagementPanel.this.revalidate();
                PaymentManagementPanel.this.repaint();
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
    private void loadReferenceData() {
        cmbMember.removeAllItems();
        for (Member member : memberController.findAll()) {
            cmbMember.addItem(member);
        }
        cmbLibrarian.removeAllItems();
        for (Librarian librarian : librarianController.findAll()) {
            cmbLibrarian.addItem(librarian);
        }
        cmbFine.removeAllItems();
        cmbFine.addItem(null);
        for (Fine fine : fineController.findAll()) {
            cmbFine.addItem(fine);
        }
    }

    private JPanel buildForm() {
        txtNote.setLineWrap(true);
        txtNote.setWrapStyleWord(true);

        // Gom form vào wrapper Bắc để form không bị giãn thưa xuống dưới
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setOpaque(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = baseGbc();
        addRow(form, gbc, 0, "Mã thanh toán", txtCode);
        addRow(form, gbc, 1, "Thành viên", cmbMember);
        addRow(form, gbc, 2, "Thủ thư", cmbLibrarian);
        addRow(form, gbc, 3, "Khoản phạt", cmbFine);
        addRow(form, gbc, 4, "Số tiền", txtAmount);
        addRow(form, gbc, 5, "Ngày thanh toán", txtPaymentDate);
        addRow(form, gbc, 6, "Phương thức", cmbMethod);
        addRow(form, gbc, 7, "Trạng thái", txtStatus);
        addRow(form, gbc, 8, "Ghi chú", UIHelper.wrapArea(txtNote));
        
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
        for (Payment payment : controller.findAll()) {
            if (q.isBlank()
                    || payment.getCode().toLowerCase().contains(q)
                    || payment.getMember().getFullName().toLowerCase().contains(q)
                    || payment.getLibrarian().getFullName().toLowerCase().contains(q)) {
                displayedData.add(payment);
            }
        }
        render(displayedData);
    }

    private void render(List<Payment> list) {
        clearTable();
        for (Payment p : list) {
            tableModel.addRow(new Object[] {
                    p.getId(), p.getCode(),
                    p.getMember() != null ? p.getMember().getFullName() : "",
                    p.getLibrarian() != null ? p.getLibrarian().getFullName() : "",
                    p.getFine() != null ? p.getFine().getCode() : "",
                    p.getAmount(), p.getStatus(), DateUtil.format(p.getPaymentDate())
            });
        }
    }

    @Override
    protected void saveEntity() {
        Payment payment = current == null ? new Payment() : current;
        Fine selectedFine = (Fine) cmbFine.getSelectedItem();
        BigDecimal inputAmount = ValidationUtil.parseBigDecimal(txtAmount.getText().trim(), "Số tiền không hợp lệ");

        // Ràng buộc: Nếu có khoản phạt, số tiền phải khớp tuyệt đối
        if (selectedFine != null) {
            if (inputAmount.compareTo(selectedFine.getAmount()) != 0) {
                UIHelper.error(this, "Số tiền thanh toán phải khớp chính xác với khoản phạt: " +
                        String.format("%,.2f", selectedFine.getAmount()) + " VNĐ");
                return;
            }
            // Tự động cập nhật trạng thái Fine sang PAID và cập nhật ngày
            selectedFine.setPaymentStatus(PaymentStatus.PAID);
            fineController.save(selectedFine);
        }

        String code = txtCode.getText().trim();
        payment.setCode(code.isBlank() ? null : code);
        payment.setMember((Member) cmbMember.getSelectedItem());
        payment.setLibrarian((Librarian) cmbLibrarian.getSelectedItem());
        payment.setFine(selectedFine);
        payment.setAmount(inputAmount);
        payment.setPaymentDate(DateUtil.parse(txtPaymentDate.getText().trim()));
        payment.setMethod(String.valueOf(cmbMethod.getSelectedItem()));
        payment.setStatus(PaymentStatus.PAID);
        payment.setNote(txtNote.getText().trim());

        controller.save(payment);
        UIHelper.info(this, "Lưu thanh toán thành công");
        refresh();
    }

    @Override
    protected void deleteEntity() {
        if (current == null || current.getId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn khoản thanh toán cần xóa");
        }
        if (UIHelper.confirm(this, "Xóa khoản thanh toán đã chọn?")) {
            // Tự động cập nhật lại trạng thái khoản phạt thành UNPAID khi xóa thanh toán
            if (current.getFine() != null) {
                Fine f = current.getFine();
                f.setPaymentStatus(PaymentStatus.UNPAID);
                fineController.save(f);
            }

            controller.delete(current.getId());
            refresh();
        }
    }

    @Override
    protected void clearForm() {
        current = null;
        txtCode.setText("");
        txtAmount.setText("0");
        txtPaymentDate.setText(DateUtil.format(LocalDate.now()));
        txtNote.setText("");
        if (cmbMember.getItemCount() > 0)
            cmbMember.setSelectedIndex(0);
        if (cmbLibrarian.getItemCount() > 0)
            cmbLibrarian.setSelectedIndex(0);
        cmbFine.setSelectedItem(null);
        cmbMethod.setSelectedIndex(0);
        txtStatus.setText(PaymentStatus.PAID.toString());
        table.clearSelection();
        setCreateMode();
    }

    @Override
    protected void fillFormFromSelectedRow(int row) {
        current = displayedData.get(row);
        setEditMode();
        txtCode.setText(current.getCode());
        cmbMember.setSelectedItem(current.getMember());
        cmbLibrarian.setSelectedItem(current.getLibrarian());
        cmbFine.setSelectedItem(current.getFine());
        txtAmount.setText(String.valueOf(current.getAmount()));
        txtPaymentDate.setText(DateUtil.format(current.getPaymentDate()));
        cmbMethod.setSelectedItem(current.getMethod());
        txtStatus.setText(current.getStatus().toString());
        txtNote.setText(current.getNote());
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