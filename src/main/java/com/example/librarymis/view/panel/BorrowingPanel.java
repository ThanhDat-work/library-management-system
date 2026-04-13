package com.example.librarymis.view.panel;

import com.example.librarymis.controller.BookController;
import com.example.librarymis.controller.BorrowController;
import com.example.librarymis.controller.LibrarianController;
import com.example.librarymis.controller.MemberController;
import com.example.librarymis.model.entity.Book;
import com.example.librarymis.model.entity.BorrowDetail;
import com.example.librarymis.model.entity.BorrowRecord;
import com.example.librarymis.model.entity.Librarian;
import com.example.librarymis.model.entity.Member;
import com.example.librarymis.model.enumtype.BorrowStatus;
import com.example.librarymis.util.DateUtil;
import com.example.librarymis.util.ValidationUtil;
import com.example.librarymis.view.component.UIHelper;
import com.example.librarymis.view.panel.common.CrudPanelTemplate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BorrowingPanel extends CrudPanelTemplate {
    private final BorrowController controller = new BorrowController();
    private final MemberController memberController = new MemberController();
    private final LibrarianController librarianController = new LibrarianController();
    private final BookController bookController = new BookController();

    private final JTextField txtCode = new JTextField();
    private final JComboBox<Member> cmbMember = new JComboBox<>();
    private final JComboBox<Librarian> cmbLibrarian = new JComboBox<>();
    private final JTextField txtBorrowDate = new JTextField(DateUtil.format(LocalDate.now()));
    private final JTextField txtDueDate = new JTextField("");
    private final JTextArea txtNotes = new JTextArea(3, 20);

    private final JComboBox<Book> cmbBook = new JComboBox<>();
    private final JSpinner spQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
    private final JTextField txtDetailNote = new JTextField();
    private final JButton btnAddDetail = UIHelper.secondaryButton("Thêm sách");
    private final JButton btnRemoveDetail = UIHelper.secondaryButton("Bỏ dòng");

    private final DefaultTableModel detailModel = new DefaultTableModel(
            new String[] { "ID Sách", "Tên sách", "Số lượng", "Ghi chú dòng" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable detailTable = UIHelper.table(detailModel);

    private final List<BorrowRecord> displayedData = new ArrayList<>();
    private BorrowRecord current;

    // Biến cho nền đốm sáng
    private List<Particle> particles;
    private Timer timer;

    public BorrowingPanel() {
        // Tắt nền mặc định của thẻ cha
        setOpaque(false);
        setBackground(new Color(245, 250, 255));

        loadReferenceData();
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);

        btnAddDetail.addActionListener(e -> safeRun(this::addDetailRow));
        btnRemoveDetail.addActionListener(e -> safeRun(this::removeDetailRow));

        initLayout(
                "Quản lý mượn sách",
                "Lập phiếu mượn mới và theo dõi các phiếu đang mượn",
                new String[] { "ID", "Mã phiếu", "Thành viên", "Thủ thư", "Ngày mượn", "Hạn trả", "Trạng thái",
                        "Sách mượn" },
                buildForm());

        if (btnDelete != null)
            btnDelete.setVisible(false);
        if (btnNew != null)
            btnNew.setVisible(false);

        refresh();

        // Khởi động đốm sáng bay lượn
        initParticles();
        startAnimation();

        // VŨ KHÍ HỦY DIỆT NỀN TRẮNG: Ép tàng hình và tròng Viền Kính Phát Sáng
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                makeTransparentAndApplyGlass(BorrowingPanel.this);
                BorrowingPanel.this.revalidate();
                BorrowingPanel.this.repaint();
            }
        });
    }

    private JPanel buildForm() {
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setOpaque(false);

        // 1. Form thông tin chung
        JPanel topForm = new JPanel(new GridBagLayout());
        topForm.setOpaque(false);
        GridBagConstraints gbc = baseGbc();

        addRow(topForm, gbc, 0, "Mã phiếu (tùy chọn)", txtCode);
        addRow(topForm, gbc, 1, "Thành viên", cmbMember);
        addRow(topForm, gbc, 2, "Thủ thư", cmbLibrarian);
        addRow(topForm, gbc, 3, "Ngày mượn", txtBorrowDate);
        addRow(topForm, gbc, 4, "Hạn trả (yyyy-MM-dd)", txtDueDate);
        addRow(topForm, gbc, 5, "Ghi chú phiếu", UIHelper.wrapArea(txtNotes));

        // 2. Panel chi tiết sách
        JPanel detailPanel = new JPanel(new BorderLayout(8, 8));
        detailPanel.setOpaque(false);
        detailPanel.setBorder(BorderFactory.createTitledBorder("Danh sách sách chọn mượn"));

        JPanel quickAddPanel = new JPanel(new GridBagLayout());
        quickAddPanel.setOpaque(false);

        GridBagConstraints gbc_detail = new GridBagConstraints();
        gbc_detail.insets = new Insets(5, 5, 5, 5);
        gbc_detail.anchor = GridBagConstraints.WEST;
        gbc_detail.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Sách
        gbc_detail.gridx = 0;
        gbc_detail.gridy = 0;
        quickAddPanel.add(UIHelper.fieldLabel("Sách:"), gbc_detail);

        gbc_detail.gridx = 1;
        quickAddPanel.add(cmbBook, gbc_detail);

        // Row 2: Số lượng
        gbc_detail.gridx = 0;
        gbc_detail.gridy = 1;
        quickAddPanel.add(UIHelper.fieldLabel("SL:"), gbc_detail);

        gbc_detail.gridx = 1;
        quickAddPanel.add(spQuantity, gbc_detail);

        // Row 3: Ghi chú
        gbc_detail.gridx = 0;
        gbc_detail.gridy = 2;
        quickAddPanel.add(UIHelper.fieldLabel("Ghi chú:"), gbc_detail);

        gbc_detail.gridx = 1;
        quickAddPanel.add(txtDetailNote, gbc_detail);

        // Row 4: Button
        gbc_detail.gridx = 0;
        gbc_detail.gridy = 3;
        gbc_detail.gridwidth = 2;
        gbc_detail.anchor = GridBagConstraints.CENTER;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnAddDetail);
        btnPanel.add(btnRemoveDetail);

        quickAddPanel.add(btnPanel, gbc_detail);

        detailPanel.add(quickAddPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(detailTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        detailPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(topForm, BorderLayout.NORTH);
        mainPanel.add(detailPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    @Override
    protected double getSplitRatio() {
        return 0.3; // cân 50-50
    }

    // =========================================================
    // THUẬT TOÁN QUÉT SÂU: ÉP TRONG SUỐT VÀ TÌM KHUNG ĐỂ TRÒNG KÍNH
    // =========================================================
    private void makeTransparentAndApplyGlass(Container parent) {
        for (Component c : parent.getComponents()) {
            boolean isInputOrButton = c instanceof JTextField || c instanceof JTextArea ||
                    c instanceof JComboBox || c instanceof JButton ||
                    c instanceof JCheckBox || c instanceof JLabel ||
                    c instanceof JSpinner;

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
                boolean isLargeCard = panel.getWidth() > 250 && panel.getHeight() > 80 && !hasGlassAncestor(panel);

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
                if (((JPanel) p).getBorder() instanceof GlowingGlassBorder)
                    return true;
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
        memberController.activeMembers().forEach(cmbMember::addItem);
        cmbLibrarian.removeAllItems();
        librarianController.findAll().stream().filter(Librarian::isActive).forEach(cmbLibrarian::addItem);
        cmbBook.removeAllItems();
        bookController.findAll().stream()
                .filter(b -> b.isActive() && b.getAvailableQuantity() > 0)
                .forEach(cmbBook::addItem);
    }

    @Override
    protected void saveEntity() {
        if (current == null)
            current = new BorrowRecord();
        try {
            ValidationUtil.require(cmbMember.getSelectedItem() != null, "Vui lòng chọn thành viên");
            ValidationUtil.require(detailModel.getRowCount() > 0, "Phiếu mượn phải có ít nhất 1 cuốn sách");
            ValidationUtil.require(!txtDueDate.getText().isBlank(), "Vui lòng nhập hạn trả");

            current.setCode(txtCode.getText().trim().isBlank() ? null : txtCode.getText().trim());
            current.setMember((Member) cmbMember.getSelectedItem());
            current.setLibrarian((Librarian) cmbLibrarian.getSelectedItem());
            current.setBorrowDate(DateUtil.parse(txtBorrowDate.getText().trim()));
            current.setDueDate(DateUtil.parse(txtDueDate.getText().trim()));
            current.setNotes(txtNotes.getText().trim());

            if (current.getId() == null)
                current.setBorrowStatus(BorrowStatus.BORROWED);

            if (current.getId() == null) {
                current.getDetails().clear();
                for (int i = 0; i < detailModel.getRowCount(); i++) {
                    Long bookId = (Long) detailModel.getValueAt(i, 0);
                    int qty = (Integer) detailModel.getValueAt(i, 2);
                    String note = (String) detailModel.getValueAt(i, 3);
                    BorrowDetail d = new BorrowDetail();
                    d.setBook(bookController.findById(bookId));
                    d.setQuantity(qty);
                    d.setNote(note);
                    current.addDetail(d);
                }
            }
            controller.save(current);
            UIHelper.info(this, "Lưu phiếu mượn thành công!");
            refresh();
        } catch (Exception ex) {
            UIHelper.error(this, "Lỗi: " + ex.getMessage());
        }
    }

    @Override
    protected void refresh() {
        clearTable();
        displayedData.clear();
        displayedData.addAll(controller.findActive());
        for (BorrowRecord r : displayedData) {
            String bookTitles = r.getDetails().stream()
                    .map(d -> d.getBook() != null ? d.getBook().getTitle() : "")
                    .reduce((a, b) -> a + ", " + b).orElse("");
            tableModel.addRow(new Object[] {
                    r.getId(), r.getCode(), r.getMember().getFullName(), r.getLibrarian().getFullName(),
                    DateUtil.format(r.getBorrowDate()), DateUtil.format(r.getDueDate()),
                    r.getBorrowStatus(), bookTitles
            });
        }
        clearForm();
    }

    @Override
    protected void fillFormFromSelectedRow(int row) {
        current = displayedData.get(row);
        txtCode.setText(current.getCode());
        cmbMember.setSelectedItem(current.getMember());
        cmbLibrarian.setSelectedItem(current.getLibrarian());
        txtBorrowDate.setText(DateUtil.format(current.getBorrowDate()));
        txtDueDate.setText(DateUtil.format(current.getDueDate()));
        txtNotes.setText(current.getNotes());
        detailModel.setRowCount(0);
        for (BorrowDetail d : current.getDetails()) {
            detailModel
                    .addRow(new Object[] { d.getBook().getId(), d.getBook().getTitle(), d.getQuantity(), d.getNote() });
        }
        btnAddDetail.setEnabled(false);
        btnRemoveDetail.setEnabled(false);
        setEditMode();
    }

    @Override
    protected void clearForm() {
        current = null;
        txtCode.setText("");
        txtBorrowDate.setText(DateUtil.format(LocalDate.now()));
        txtDueDate.setText("");
        txtNotes.setText("");
        detailModel.setRowCount(0);
        btnAddDetail.setEnabled(true);
        btnRemoveDetail.setEnabled(true);
        setCreateMode();
    }

    private void addDetailRow() {
        Book book = (Book) cmbBook.getSelectedItem();
        if (book == null)
            return;
        int qty = (Integer) spQuantity.getValue();
        if (qty > book.getAvailableQuantity()) {
            UIHelper.error(this, "Kho chỉ còn " + book.getAvailableQuantity());
            return;
        }
        for (int i = 0; i < detailModel.getRowCount(); i++) {
            if (detailModel.getValueAt(i, 0).equals(book.getId())) {
                int oldQty = (Integer) detailModel.getValueAt(i, 2);
                detailModel.setValueAt(oldQty + qty, i, 2);
                return;
            }
        }
        detailModel.addRow(new Object[] { book.getId(), book.getTitle(), qty, txtDetailNote.getText() });
        txtDetailNote.setText("");
    }

    private void removeDetailRow() {
        int sel = detailTable.getSelectedRow();
        if (sel >= 0)
            detailModel.removeRow(sel);
        else
            UIHelper.error(this, "Vui lòng chọn một dòng sách để bỏ!");
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 0, 4, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row, String label, Component c) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0;
        p.add(UIHelper.fieldLabel(label), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        p.add(c, gbc);
    }

    @Override
    protected void deleteEntity() {
    }

    @Override
    protected void search(String keyword) {
    }

    // --- Animation & Border Core ---
    private void initParticles() {
        particles = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < 70; i++)
            particles.add(new Particle(r.nextInt(1500), r.nextInt(1000)));
    }

    private void startAnimation() {
        timer = new Timer(30, e -> {
            for (Particle p : particles) {
                p.x += p.vx;
                p.y += p.vy;
                if (p.x < 0 || p.x > getWidth())
                    p.vx *= -1;
                if (p.y < 0 || p.y > getHeight())
                    p.vy *= -1;
            }
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
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
            this.x = x;
            this.y = y;
            Random r = new Random();
            this.vx = (r.nextDouble() - 0.5) * 1.5;
            this.vy = (r.nextDouble() - 0.5) * 1.5;
            this.size = r.nextInt(4) + 2;
            this.alpha = r.nextInt(100) + 50;
        }
    }

    private class GlowingGlassBorder extends javax.swing.border.AbstractBorder {
        private final int radius = 25, glowSize = 8;

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int bx = x + glowSize, by = y + glowSize, bw = width - glowSize * 2 - 1, bh = height - glowSize * 2 - 1;
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
    }
}