package com.example.librarymis.view.panel;

import com.example.librarymis.controller.BookController;
import com.example.librarymis.controller.CategoryController;
import com.example.librarymis.controller.PublisherController;
import com.example.librarymis.model.entity.Book;
import com.example.librarymis.model.entity.Category;
import com.example.librarymis.model.entity.Publisher;
import com.example.librarymis.util.ValidationUtil;
import com.example.librarymis.view.component.UIHelper;
import com.example.librarymis.view.panel.common.CrudPanelTemplate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BookManagementPanel extends CrudPanelTemplate {
    private final BookController controller = new BookController();
    private final CategoryController categoryController = new CategoryController();
    private final PublisherController publisherController = new PublisherController();

    // Giữ nguyên 100% các ô nhập liệu mặc định của ông
    private final JTextField txtCode = new JTextField();
    private final JTextField txtTitle = new JTextField();
    private final JTextField txtAuthor = new JTextField();
    private final JTextField txtIsbn = new JTextField();
    private final JTextField txtPrice = new JTextField("0");
    private final JTextField txtPublishYear = new JTextField();
    private final JTextField txtQuantity = new JTextField("1");
    private final JTextField txtAvailableQuantity = new JTextField("1");
    private final JComboBox<Category> cmbCategory = new JComboBox<>();
    private final JComboBox<Publisher> cmbPublisher = new JComboBox<>();
    private final JCheckBox chkActive = new JCheckBox("Đang kinh doanh / lưu hành", true);
    private final JTextArea txtDescription = new JTextArea(5, 20);

    private final List<Book> displayedData = new ArrayList<>();
    private Book current;

    // Biến cho nền đốm sáng
    private List<Particle> particles;
    private Timer timer;

    public BookManagementPanel() {
        // Tắt nền mặc định của thẻ cha
        setOpaque(false);
        setBackground(new Color(245, 250, 255)); 

        loadReferenceData();
        initLayout("Quản lý sách", "Thêm mới, cập nhật và theo dõi tồn kho đầu sách",
                new String[] { "ID", "Mã", "Tên sách", "Tác giả", "Thể loại", "NXB", "SL", "Khả dụng" }, buildForm());
        
        performInitialRefresh();

        // Khởi động đốm sáng bay lượn
        initParticles();
        startAnimation();

        // VŨ KHÍ HỦY DIỆT NỀN TRẮNG: Ép tàng hình và tròng Viền Kính Phát Sáng
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                makeTransparentAndApplyGlass(BookManagementPanel.this);
                // Ép giao diện vẽ lại một lần nữa để xóa sạch nền trắng
                BookManagementPanel.this.revalidate();
                BookManagementPanel.this.repaint();
            }
        });
    }

    private void performInitialRefresh() {
        refresh();
    }

    // =========================================================
    // THUẬT TOÁN QUÉT SÂU: ÉP TRONG SUỐT VÀ TÌM KHUNG ĐỂ TRÒNG KÍNH
    // (Dùng Java 8 Cổ Điển để VS Code câm nín, không báo vàng)
    // =========================================================
    private void makeTransparentAndApplyGlass(Container parent) {
        for (Component c : parent.getComponents()) {
            
            // 1. Loại trừ các thành phần nhập liệu & nút bấm để giữ nền trắng cho dễ nhìn chữ
            boolean isInputOrButton = c instanceof JTextField || c instanceof JTextArea || 
                                      c instanceof JComboBox || c instanceof JButton || 
                                      c instanceof JCheckBox || c instanceof JLabel;
            
            // 2. ÉP TÀNG HÌNH DIỆT TẬN GỐC NỀN TRẮNG (Cho Panel, SplitPane, Container...)
            if (!isInputOrButton && c instanceof JComponent) {
                JComponent jc = (JComponent) c;
                jc.setOpaque(false);
                jc.setBackground(new Color(0, 0, 0, 0)); // Mã màu trong suốt tuyệt đối trị FlatLaf
            }
            
            // 3. Trị riêng JScrollPane (Vì ruột của nó là JViewport rất lì lợm)
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

    // Kiểm tra chống lồng 2 lớp kính vào nhau
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
    // CÁC HÀM LOGIC GỐC CỦA ÔNG (GIỮ NGUYÊN 100%)
    // =========================================================
    private void loadReferenceData() {
        cmbCategory.removeAllItems();
        for (Category category : categoryController.findAll()) {
            cmbCategory.addItem(category);
        }
        cmbPublisher.removeAllItems();
        for (Publisher publisher : publisherController.findAll()) {
            cmbPublisher.addItem(publisher);
        }
    }

    private JPanel buildForm() {
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = baseGbc();
        addRow(form, gbc, 0, "Mã sách", txtCode);
        addRow(form, gbc, 1, "Tên sách", txtTitle);
        addRow(form, gbc, 2, "Tác giả", txtAuthor);
        addRow(form, gbc, 3, "ISBN", txtIsbn);
        addRow(form, gbc, 4, "Giá bán", txtPrice);
        addRow(form, gbc, 5, "Năm XB", txtPublishYear);
        addRow(form, gbc, 6, "Số lượng", txtQuantity);
        addRow(form, gbc, 7, "Khả dụng", txtAvailableQuantity);
        addRow(form, gbc, 8, "Thể loại", cmbCategory);
        addRow(form, gbc, 9, "NXB", cmbPublisher);
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.weightx = 1;
        form.add(chkActive, gbc);
        addRow(form, gbc, 11, "Mô tả", UIHelper.wrapArea(txtDescription));
        panel.add(form, BorderLayout.NORTH);
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
        displayedData.clear();
        displayedData.addAll(controller.search(keyword));
        render(displayedData);
    }

    private void render(List<Book> list) {
        clearTable();
        for (Book b : list) {
            tableModel.addRow(new Object[] {
                    b.getId(), b.getCode(), b.getTitle(), b.getAuthor(),
                    b.getCategory() != null ? b.getCategory().getName() : "",
                    b.getPublisher() != null ? b.getPublisher().getName() : "",
                    b.getQuantity(), b.getAvailableQuantity()
            });
        }
    }

    @Override
    protected void saveEntity() {
        Book book = current == null ? new Book() : current;
        book.setCode(txtCode.getText().trim());
        book.setTitle(txtTitle.getText().trim());
        book.setAuthor(txtAuthor.getText().trim());
        book.setIsbn(txtIsbn.getText().trim());
        book.setPrice(ValidationUtil.parseBigDecimal(txtPrice.getText().trim(), "Giá không hợp lệ"));
        String yearText = txtPublishYear.getText().trim();
        book.setPublishYear(yearText.isBlank() ? null : ValidationUtil.parseInt(yearText, "Năm xuất bản không hợp lệ"));
        int qty = ValidationUtil.parseInt(txtQuantity.getText().trim(), "Số lượng không hợp lệ");
        book.setQuantity(qty);
        String avText = txtAvailableQuantity.getText().trim();
        book.setAvailableQuantity(
                avText.isBlank() ? qty : ValidationUtil.parseInt(avText, "Số lượng khả dụng không hợp lệ"));
        book.setCategory((Category) cmbCategory.getSelectedItem());
        book.setPublisher((Publisher) cmbPublisher.getSelectedItem());
        book.setDescription(txtDescription.getText().trim());
        book.setActive(chkActive.isSelected());
        controller.save(book);
        UIHelper.info(this, "Lưu sách thành công");
        refresh();
    }

    @Override
    protected void deleteEntity() {
        if (current == null || current.getId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn sách cần xóa");
        }
        if (UIHelper.confirm(this, "Bạn có chắc muốn xóa sách đã chọn?")) {
            controller.delete(current.getId());
            refresh();
        }
    }

    @Override
    protected void clearForm() {
        current = null;
        txtCode.setText("");
        txtTitle.setText("");
        txtAuthor.setText("");
        txtIsbn.setText("");
        txtPrice.setText("0");
        txtPublishYear.setText("");
        txtQuantity.setText("1");
        txtAvailableQuantity.setText("1");
        if (cmbCategory.getItemCount() > 0)
            cmbCategory.setSelectedIndex(0);
        if (cmbPublisher.getItemCount() > 0)
            cmbPublisher.setSelectedIndex(0);
        txtDescription.setText("");
        chkActive.setSelected(true);
        table.clearSelection();
        setCreateMode();
    }

    @Override
    protected void fillFormFromSelectedRow(int row) {
        current = displayedData.get(row);
        setEditMode();
        txtCode.setText(current.getCode());
        txtTitle.setText(current.getTitle());
        txtAuthor.setText(current.getAuthor());
        txtIsbn.setText(current.getIsbn());
        txtPrice.setText(String.valueOf(current.getPrice()));
        txtPublishYear.setText(current.getPublishYear() == null ? "" : current.getPublishYear().toString());
        txtQuantity.setText(String.valueOf(current.getQuantity()));
        txtAvailableQuantity.setText(String.valueOf(current.getAvailableQuantity()));
        cmbCategory.setSelectedItem(current.getCategory());
        cmbPublisher.setSelectedItem(current.getPublisher());
        txtDescription.setText(current.getDescription());
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

            // 1. VẼ ÁNH SÁNG TỎA RA (GLOW EFFECT) - Chỉnh độ mờ về 30 y hệt Dashboard
            for (int i = 0; i < glowSize; i++) {
                g2.setColor(new Color(0, 102, 204, 30 / (i + 1))); 
                g2.fillRoundRect(bx - i, by - i, bw + i * 2, bh + i * 2, radius + i, radius + i);
            }
            
            // 2. VẼ LỚP KÍNH CHÍNH - Tăng Alpha lên 190 để có màu trắng sữa y hệt Dashboard
            g2.setColor(new Color(255, 255, 255, 190)); 
            g2.fillRoundRect(bx, by, bw, bh, radius, radius);
            
            // 3. VẼ VIỀN KÍNH BÓNG BẨY - Chỉnh viền về 220 y hệt Dashboard
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