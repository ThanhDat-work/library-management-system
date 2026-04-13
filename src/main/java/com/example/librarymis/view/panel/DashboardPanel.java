package com.example.librarymis.view.panel;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.example.librarymis.controller.BookController;
import com.example.librarymis.controller.BorrowController;
import com.example.librarymis.controller.FineController;
import com.example.librarymis.controller.MemberController;
import com.example.librarymis.view.panel.common.Reloadable;

public class DashboardPanel extends JPanel implements Reloadable {
    // Biến cho Animation đốm sáng
    private List<Particle> particles;
    private Timer timer;

    private final BookController bookController = new BookController();
    private final MemberController memberController = new MemberController();
    private final BorrowController borrowController = new BorrowController();
    private final FineController fineController = new FineController();

    // SỬ DỤNG KHUNG KÍNH TRONG SUỐT (GlassStatCard) DO TỰ THIẾT KẾ BÊN DƯỚI
    private final GlassStatCard bookCard = new GlassStatCard("Đầu sách");
    private final GlassStatCard memberCard = new GlassStatCard("Thành viên");
    private final GlassStatCard borrowCard = new GlassStatCard("Phiếu mượn đang hoạt động");
    private final GlassStatCard fineCard = new GlassStatCard("Phạt chưa thu");

    public DashboardPanel() {
        setLayout(new BorderLayout(25, 25)); // Tăng khoảng cách nhìn cho thoáng
        setBackground(new Color(245, 250, 255)); // Nền trắng hơi ám xanh siêu nhạt
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // ==========================================
        // 1. PHẦN TIÊU ĐỀ (HEADER)
        // ==========================================
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false); // Trong suốt để thấy nền đốm sáng
        
        JLabel lblTitle = new JLabel("Bảng điều khiển");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(new Color(20, 30, 60)); // Màu xanh đen sang trọng
        
        JLabel lblSub = new JLabel("Theo dõi nhanh hoạt động thư viện theo thời gian thực");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSub.setForeground(new Color(100, 110, 120));
        
        headerPanel.add(lblTitle);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(lblSub);
        add(headerPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. KHU VỰC THẺ THỐNG KÊ (BO GÓC, TRONG SUỐT)
        // ==========================================
        JPanel cardsContainer = new JPanel(new GridLayout(1, 4, 25, 25));
        cardsContainer.setOpaque(false);
        cardsContainer.add(bookCard);
        cardsContainer.add(memberCard);
        cardsContainer.add(borrowCard);
        cardsContainer.add(fineCard);

        // ==========================================
        // 3. KHU VỰC TỔNG QUAN (KHUNG KÍNH LỚN)
        // ==========================================
        GlassPanel introPanel = new GlassPanel(30); // Bo góc 30px cực mượt
        introPanel.setLayout(new BorderLayout(15, 15));
        introPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel introTop = new JPanel();
        introTop.setLayout(new BoxLayout(introTop, BoxLayout.Y_AXIS));
        introTop.setOpaque(false);
        
        JLabel lblIntroTitle = new JLabel("Tổng quan hệ thống");
        lblIntroTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblIntroTitle.setForeground(new Color(20, 30, 60));
        
        JLabel lblIntroSub = new JLabel("Những gì bạn có thể thao tác nhanh từ màn hình chính");
        lblIntroSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblIntroSub.setForeground(new Color(100, 110, 120));
        
        introTop.add(lblIntroTitle);
        introTop.add(Box.createVerticalStrut(5));
        introTop.add(lblIntroSub);

        JTextArea textArea = new JTextArea("""
                Chào mừng bạn đến với Library MIS.
                
                Điểm nổi bật của thiết kế mới:
                • Giao diện Glassmorphism (Kính mờ) hiện đại, sang trọng hoàn toàn mới.
                • Hiệu ứng đốm sáng mạng lưới xanh dương bay lượn 3D chìm dưới nền.
                • Các thẻ thống kê bo góc siêu mượt, trong suốt nhìn xuyên thấu.
                • Shadow ánh sáng xanh dương giúp UI nổi bật và có chiều sâu.
        """);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textArea.setForeground(new Color(50, 60, 70));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        introPanel.add(introTop, BorderLayout.NORTH);
        introPanel.add(textArea, BorderLayout.CENTER);

        // Gộp thẻ và Intro vào body
        JPanel bodyPanel = new JPanel(new BorderLayout(25, 25));
        bodyPanel.setOpaque(false);
        bodyPanel.add(cardsContainer, BorderLayout.NORTH);
        bodyPanel.add(introPanel, BorderLayout.CENTER);

        add(bodyPanel, BorderLayout.CENTER);

        // Kích hoạt animation & nạp data
        initParticles();
        startAnimation();
        reload();
    }

    public final void reload() {
        bookCard.updateValue(String.valueOf(bookController.count()), "Tổng số sách trong thư viện");
        memberCard.updateValue(String.valueOf(memberController.count()), "Bạn đọc đang quản lý");
        borrowCard.updateValue(String.valueOf(borrowController.countActive()), "Các phiếu đang mượn/trễ hạn");
        fineCard.updateValue(String.valueOf(fineController.countUnpaid()), "Các khoản chưa thanh toán");
    }

    @Override
    public void reloadData() {
        reload();
    }

    // =========================================================
    // CODE VẼ ĐỐM SÁNG XANH DƯƠNG BAY LƯỢN (ĐƯỢC ĐẶT LÀM NỀN)
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
        super.paintComponent(g); // Vẽ nền tĩnh
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ mạng lưới xanh dương nhạt (Light Blue)
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
    // LỚP CORE: TẠO HIỆU ỨNG KÍNH MỜ CÓ HÀO QUANG (GLOW)
    // =========================================================
    private class GlassPanel extends JPanel {
        private final int radius;
        private final int glowSize = 8; // Kích thước vệt sáng tỏa ra (Có thể tăng lên 10-12 nếu thích tỏa bự)

        public GlassPanel(int radius) {
            this.radius = radius;
            setOpaque(false); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Chừa một khoảng lùi vào bằng đúng glowSize để ánh sáng không bị cắt viền
            int x = glowSize;
            int y = glowSize;
            int w = getWidth() - glowSize * 2 - 1;
            int h = getHeight() - glowSize * 2 - 1;

            // 1. VẼ ÁNH SÁNG TỎA RA (GLOW EFFECT)
            // Dùng vòng lặp vẽ các khung to dần nhưng mờ dần để tạo viền sáng mịn màng
            for (int i = 0; i < glowSize; i++) {
                // Màu xanh dương (0, 102, 204), độ mờ giảm dần theo lớp
                g2.setColor(new Color(0, 102, 204, 30 / (i + 1))); 
                g2.fillRoundRect(x - i, y - i, w + i * 2, h + i * 2, radius + i, radius + i);
            }
            
            // 2. VẼ LỚP KÍNH CHÍNH (Trắng trong suốt)
            g2.setColor(new Color(255, 255, 255, 190)); 
            g2.fillRoundRect(x, y, w, h, radius, radius);
            
            // 3. VẼ VIỀN KÍNH SẮC NÉT (Mỏng, màu trắng đục)
            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(new Color(255, 255, 255, 220));
            g2.drawRoundRect(x, y, w, h, radius, radius);
            
            g2.dispose();
            super.paintComponent(g); // Vẽ chữ lên trên cùng
        }
    }

    // =========================================================
    // THẺ THỐNG KÊ (KẾT THỪA LỚP KÍNH MỜ BÊN TRÊN)
    // =========================================================
    private class GlassStatCard extends GlassPanel {
        private final JLabel lblValue;
        private final JLabel lblDesc;

        public GlassStatCard(String titleText) {
            super(25); // Bo góc 25px
            setLayout(new BorderLayout(10, 10));
            setBorder(new EmptyBorder(20, 20, 20, 20));

            JLabel lblTitle = new JLabel(titleText);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblTitle.setForeground(new Color(0, 102, 204)); // Tiêu đề xanh dương

            lblValue = new JLabel("0");
            lblValue.setFont(new Font("Segoe UI", Font.BOLD, 42)); // Font số siêu to
            lblValue.setForeground(new Color(20, 30, 60));

            lblDesc = new JLabel("...");
            lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblDesc.setForeground(new Color(100, 110, 120));

            add(lblTitle, BorderLayout.NORTH);
            add(lblValue, BorderLayout.CENTER);
            add(lblDesc, BorderLayout.SOUTH);
        }

        public void updateValue(String val, String desc) {
            lblValue.setText(val);
            lblDesc.setText(desc);
        }
    }
}