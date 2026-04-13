package com.example.librarymis.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.example.librarymis.model.entity.Librarian;
import com.example.librarymis.view.component.UIHelper;
import com.example.librarymis.view.panel.BookManagementPanel;
import com.example.librarymis.view.panel.BorrowingPanel;
import com.example.librarymis.view.panel.ReturningPanel;
import com.example.librarymis.view.panel.CategoryManagementPanel;
import com.example.librarymis.view.panel.DashboardPanel;
import com.example.librarymis.view.panel.FineManagementPanel;
import com.example.librarymis.view.panel.LibrarianManagementPanel;
import com.example.librarymis.view.panel.MemberManagementPanel;
import com.example.librarymis.view.panel.PaymentManagementPanel;
import com.example.librarymis.view.panel.PublisherManagementPanel;
import com.example.librarymis.view.panel.ReportPanel;
import com.example.librarymis.view.panel.common.Reloadable;

public class MainView extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final DashboardPanel dashboardPanel = new DashboardPanel();
    private final Map<String, JPanel> pages = new LinkedHashMap<>();
    private final Map<String, JButton> menuButtons = new LinkedHashMap<>();
    private final Librarian currentUser;
    private JLabel pageTitle;
    private String currentPage = "Dashboard";

    public MainView(Librarian currentUser) {
        // Mục đích: xử lý logic của hàm MainView.
        this.currentUser = currentUser;
        setTitle("Library MIS - Quản lý thư viện");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1460, 880);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIHelper.APP_BG);

        buildPages();
        add(buildSidebar(), BorderLayout.WEST);
        add(buildMainContent(), BorderLayout.CENTER);

        showPage("Dashboard");
    }

    private void buildPages() {
        // Mục đích: xử lý logic của hàm buildPages.
        pages.put("Dashboard", dashboardPanel);
        pages.put("Books", new BookManagementPanel());
        pages.put("Members", new MemberManagementPanel());
        pages.put("Borrows", new BorrowingPanel());
        pages.put("Returns", new ReturningPanel());
        pages.put("Categories", new CategoryManagementPanel());
        pages.put("Publishers", new PublisherManagementPanel());
        pages.put("Librarians", new LibrarianManagementPanel());
        pages.put("Payments", new PaymentManagementPanel());
        pages.put("Fines", new FineManagementPanel());
        pages.put("Reports", new ReportPanel());

        contentPanel.setOpaque(false);
        for (Map.Entry<String, JPanel> entry : pages.entrySet()) {
            contentPanel.add(entry.getValue(), entry.getKey());
        }
    }

    private Component buildSidebar() {
        // Mục đích: xử lý logic của hàm buildSidebar.
        JPanel sidebar = new JPanel(new BorderLayout(0, 18));
        sidebar.setPreferredSize(new Dimension(285, 0));
        sidebar.setBackground(UIHelper.SIDEBAR);
        sidebar.setBorder(new EmptyBorder(22, 18, 22, 18));

        JPanel brandWrap = new JPanel();
        brandWrap.setOpaque(false);
        brandWrap.setLayout(new BoxLayout(brandWrap, BoxLayout.Y_AXIS));
        JLabel brand = new JLabel("<html><div style='font-size:25px;color:#ffffff;font-weight:bold'>Library MIS</div>"
                + "<div style='font-size:12px;color:#94a3b8;margin-top:6px'>Quản trị thư viện trực quan và hiện đại</div></html>");
        brandWrap.add(brand);
        brandWrap.add(Box.createVerticalStrut(18));
        sidebar.add(brandWrap, BorderLayout.NORTH);

        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        addMenuButton(menu, "Dashboard", "Bảng điều khiển");
        addMenuButton(menu, "Books", "Quản lý sách");
        addMenuButton(menu, "Members", "Quản lý thành viên");
        addMenuButton(menu, "Borrows", "Mượn sách");
        addMenuButton(menu, "Returns", "Trả sách");
        addMenuButton(menu, "Categories", "Thể loại");
        addMenuButton(menu, "Publishers", "Nhà xuất bản");
        addMenuButton(menu, "Librarians", "Thủ thư");
        addMenuButton(menu, "Payments", "Thanh toán");
        addMenuButton(menu, "Fines", "Khoản phạt");
        addMenuButton(menu, "Reports", "Báo cáo");

        JScrollPane scrollPane = new JScrollPane(menu);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        sidebar.add(scrollPane, BorderLayout.CENTER);

        JPanel userCard = new JPanel();
        userCard.setBackground(new Color(30, 41, 59));
        userCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1, true),
                new EmptyBorder(14, 14, 14, 14)));
        userCard.setLayout(new BoxLayout(userCard, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(currentUser.getFullName());
        name.setForeground(Color.WHITE);
        name.setFont(name.getFont().deriveFont(Font.BOLD, 15f));
        JLabel role = new JLabel("Vai trò: " + currentUser.getRole());
        role.setForeground(new Color(148, 163, 184));
        JButton btnLogout = UIHelper.secondaryButton("Đăng xuất");
        btnLogout.addActionListener(e -> {
            new LoginView().setVisible(true);
            dispose();
        });

        userCard.add(name);
        userCard.add(Box.createVerticalStrut(4));
        userCard.add(role);
        userCard.add(Box.createVerticalStrut(12));
        userCard.add(btnLogout);

        sidebar.add(userCard, BorderLayout.SOUTH);
        return sidebar;
    }

    private void addMenuButton(JPanel menu, String pageKey, String label) {
        // Mục đích: xử lý logic của hàm addMenuButton.
        JButton button = new JButton(label);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setForeground(new Color(226, 232, 240));
        button.setBackground(UIHelper.SIDEBAR);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 0, 2, 0),
                new EmptyBorder(11, 14, 11, 14)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> showPage(pageKey));
        menu.add(button);
        menu.add(Box.createVerticalStrut(6));
        menuButtons.put(pageKey, button);
    }

    private Component buildMainContent() {
        // Mục đích: xử lý logic của hàm buildMainContent.
        JPanel wrapper = new JPanel(new BorderLayout(18, 18));
        wrapper.setBackground(UIHelper.APP_BG);
        wrapper.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(UIHelper.createCardBorder(18));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        pageTitle = new JLabel("Bảng điều khiển");
        pageTitle.setForeground(UIHelper.TEXT);
        pageTitle.setFont(pageTitle.getFont().deriveFont(Font.BOLD, 22f));
        JLabel tip = new JLabel("Xin chào, " + currentUser.getFullName() + ". Chúc bạn có một ngày làm việc hiệu quả.");
        tip.setForeground(UIHelper.MUTED);
        text.add(pageTitle);
        text.add(Box.createVerticalStrut(6));
        text.add(tip);

        JLabel badge = new JLabel("Hệ thống thư viện");
        badge.setOpaque(true);
        badge.setBackground(new Color(239, 246, 255));
        badge.setForeground(new Color(29, 78, 216));
        badge.setBorder(new EmptyBorder(8, 12, 8, 12));

        topBar.add(text, BorderLayout.WEST);
        topBar.add(badge, BorderLayout.EAST);
        wrapper.add(topBar, BorderLayout.NORTH);
        wrapper.add(contentPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private void showPage(String pageKey) {
        // Mục đích: xử lý logic của hàm showPage.
        currentPage = pageKey;
        JPanel panel = pages.get(pageKey);
        if (panel instanceof Reloadable reloadable) {
            reloadable.reloadData();
        }
        if (pageTitle != null) {
            JButton btn = menuButtons.get(pageKey);
            pageTitle.setText(btn != null ? btn.getText() : pageKey);
        }
        updateMenuState();
        cardLayout.show(contentPanel, pageKey);
    }

    private void updateMenuState() {
        // Mục đích: xử lý logic của hàm updateMenuState.
        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            // Mục đích: xử lý logic của hàm entrySet.
            boolean active = entry.getKey().equals(currentPage);
            JButton button = entry.getValue();
            button.setBackground(active ? UIHelper.SIDEBAR_HOVER : UIHelper.SIDEBAR);
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(active ? new Color(59, 130, 246) : UIHelper.SIDEBAR, 1, true),
                    new EmptyBorder(11, 14, 11, 14)));
        }
    }
}
