package userinterface;

import database.StaffDB;
import model.Staff;
import util.AppConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

// ══════════════════════════════════════════════════════════════════════════════
//  LOGIN FRAME  (unchanged from your original)
// ══════════════════════════════════════════════════════════════════════════════
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private StaffDB staffDAO;

    public LoginFrame() {
        staffDAO = new StaffDB();
        setTitle(AppConstants.APP_TITLE + " | Login");
        setSize(560, 340);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel(AppConstants.APP_TITLE, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(245, 196, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 245));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(245, 196, 0), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(300, 28));
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(300, 28));
        formPanel.add(passwordField, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(14, 6, 4, 6);
        JButton loginButton = new JButton("Log In");
        loginButton.setPreferredSize(new Dimension(300, 32));
        loginButton.setBackground(new Color(245, 196, 0));
        loginButton.setForeground(new Color(30, 30, 30));
        loginButton.setFont(new Font("Arial", Font.BOLD, 13));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(loginButton, gbc);

        gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(4, 6, 4, 6);
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        formPanel.add(statusLabel, gbc);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        loginButton.addActionListener((ActionEvent e) -> handleLogin());
        getRootPane().setDefaultButton(loginButton);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }
        Staff loggedInStaff = staffDAO.authenticate(username, password);
        if (loggedInStaff != null) {
            statusLabel.setText("");
            new MainMenuFrame(loggedInStaff).setVisible(true);
            dispose();
        } else {
            statusLabel.setText("Invalid username or password.");
            passwordField.setText("");
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  MAIN MENU FRAME  — sidebar layout
// ══════════════════════════════════════════════════════════════════════════════
class MainMenuFrame extends JFrame {

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color BG_SIDEBAR    = new Color(10, 10, 10);
    private static final Color BG_CONTENT    = new Color(22, 22, 22);
    private static final Color BG_CARD       = new Color(28, 28, 28);
    private static final Color BG_HERO       = new Color(26, 26, 26);
    private static final Color YELLOW        = new Color(245, 196, 0);
    private static final Color YELLOW_MID    = new Color(245, 196, 0, 110);
    private static final Color TEXT_WHITE    = new Color(232, 232, 232);
    private static final Color TEXT_MUTED    = new Color(120, 120, 120);
    private static final Color TEXT_DIM      = new Color(55, 55, 55);
    private static final Color DIVIDER       = new Color(32, 32, 32);
    private static final Color SIDEBAR_HOVER = new Color(22, 22, 22);
    private static final Color RED_NAV       = new Color(160, 55, 55);
    private static final Color RED_HOVER     = new Color(200, 55, 55);
    private static final Color GREEN_ONLINE  = new Color(72, 199, 116);

    private static final int SIDEBAR_W = 220;

    private final Staff currentStaff;
    private JLabel clockLabel;

    public MainMenuFrame(Staff staff) {
        this.currentStaff = staff;

        setTitle(AppConstants.APP_TITLE + " | Main Menu");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1020, 620);
        setLocationRelativeTo(null);
        setResizable(false);

        // Theme pop-up dialogs to match dark palette
        UIManager.put("OptionPane.background",        BG_CARD);
        UIManager.put("Panel.background",             BG_CARD);
        UIManager.put("OptionPane.messageForeground", TEXT_WHITE);
        UIManager.put("Button.background",            YELLOW);
        UIManager.put("Button.foreground",            Color.BLACK);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_SIDEBAR);
        setContentPane(root);

        root.add(buildSidebar(),  BorderLayout.WEST);
        root.add(buildContent(),  BorderLayout.CENTER);

        startClock();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SIDEBAR
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(SIDEBAR_W, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, DIVIDER));

        // ── Brand ─────────────────────────────────────────────────────────────
        JPanel brand = new JPanel();
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setOpaque(false);
        brand.setBorder(BorderFactory.createEmptyBorder(26, 20, 22, 20));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logoLbl = new JLabel(new ImageIcon(buildLogo(46)));
        logoLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sysName = new JLabel(AppConstants.APP_TITLE);
        sysName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sysName.setForeground(YELLOW);
        sysName.setAlignmentX(Component.LEFT_ALIGNMENT);
        sysName.setBorder(BorderFactory.createEmptyBorder(10, 0, 2, 0));

        JLabel sysSub = new JLabel("Ordering & Billing System");
        sysSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sysSub.setForeground(TEXT_MUTED);
        sysSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        brand.add(logoLbl);
        brand.add(sysName);
        brand.add(sysSub);
        sidebar.add(brand);

        sidebar.add(sidebarDivider());

        // ── Nav section label ─────────────────────────────────────────────────
        sidebar.add(sectionLabel("NAVIGATION"));

        // ── Nav items — wired to real actions ─────────────────────────────────
        sidebar.add(navItem("New Order", drawOrderIcon(), () -> {
            new OrderingFrame(currentStaff).setVisible(true);
            dispose();
        }));
        sidebar.add(navItem("Order List", drawListIcon(), () -> {
            new OrderList(currentStaff).setVisible(true);
            dispose();
        }));
        sidebar.add(navItem("Transaction History", drawHistIcon(), () -> {
            new TransacHistory(currentStaff).setVisible(true);
            dispose();
        }));

        // Manager-only item
        if ("manager".equalsIgnoreCase(currentStaff.getRole())) {
            sidebar.add(navItem("Menu Management", drawMenuIcon(), () ->
                    JOptionPane.showMessageDialog(this,
                            "Menu Management — coming soon!",
                            "Menu Management", JOptionPane.INFORMATION_MESSAGE)));
        }

        sidebar.add(Box.createVerticalGlue());

        // ── User strip ────────────────────────────────────────────────────────
        sidebar.add(sidebarDivider());
        sidebar.add(buildUserStrip());
        sidebar.add(sidebarDivider());

        // ── Logout ────────────────────────────────────────────────────────────
        sidebar.add(logoutItem());
        sidebar.add(Box.createVerticalStrut(12));

        return sidebar;
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(TEXT_DIM);
        lbl.setBorder(BorderFactory.createEmptyBorder(14, 20, 8, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    // Standard nav row
    private JPanel navItem(String label, BufferedImage icon, Runnable action) {
        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(3, 44));
        indicator.setMaximumSize(new Dimension(3, 44));
        indicator.setMinimumSize(new Dimension(3, 44));
        indicator.setBackground(YELLOW);
        indicator.setOpaque(false);

        JPanel inner = new JPanel(new BorderLayout(12, 0));
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        inner.add(new JLabel(new ImageIcon(icon)), BorderLayout.WEST);

        JLabel textLbl = new JLabel(label);
        textLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textLbl.setForeground(TEXT_MUTED);
        inner.add(textLbl, BorderLayout.CENTER);

        JPanel row = new JPanel(new BorderLayout(0, 0));
        row.setOpaque(true);
        row.setBackground(BG_SIDEBAR);
        row.setMaximumSize(new Dimension(SIDEBAR_W, 44));
        row.setPreferredSize(new Dimension(SIDEBAR_W, 44));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.add(indicator, BorderLayout.WEST);
        row.add(inner, BorderLayout.CENTER);

        row.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                row.setBackground(SIDEBAR_HOVER);
                indicator.setOpaque(true);
                textLbl.setForeground(TEXT_WHITE);
            }
            @Override public void mouseExited(MouseEvent e) {
                row.setBackground(BG_SIDEBAR);
                indicator.setOpaque(false);
                textLbl.setForeground(TEXT_MUTED);
            }
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
        });
        return row;
    }

    // Logout row — red accent
    private JPanel logoutItem() {
        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(3, 44));
        indicator.setMaximumSize(new Dimension(3, 44));
        indicator.setMinimumSize(new Dimension(3, 44));
        indicator.setBackground(RED_HOVER);
        indicator.setOpaque(false);

        JPanel inner = new JPanel(new BorderLayout(12, 0));
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        inner.add(new JLabel(new ImageIcon(drawLogoutIcon())), BorderLayout.WEST);

        JLabel textLbl = new JLabel("Log Out");
        textLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textLbl.setForeground(RED_NAV);
        inner.add(textLbl, BorderLayout.CENTER);

        JPanel row = new JPanel(new BorderLayout(0, 0));
        row.setOpaque(true);
        row.setBackground(BG_SIDEBAR);
        row.setMaximumSize(new Dimension(SIDEBAR_W, 44));
        row.setPreferredSize(new Dimension(SIDEBAR_W, 44));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.add(indicator, BorderLayout.WEST);
        row.add(inner, BorderLayout.CENTER);

        row.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                row.setBackground(new Color(28, 14, 14));
                indicator.setOpaque(true);
                textLbl.setForeground(RED_HOVER);
            }
            @Override public void mouseExited(MouseEvent e) {
                row.setBackground(BG_SIDEBAR);
                indicator.setOpaque(false);
                textLbl.setForeground(RED_NAV);
            }
            @Override public void mouseClicked(MouseEvent e) { handleLogout(); }
        });
        return row;
    }

    // User identity strip above logout
    private JPanel buildUserStrip() {
        JPanel strip = new JPanel(new BorderLayout(12, 0));
        strip.setOpaque(false);
        strip.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        strip.setMaximumSize(new Dimension(SIDEBAR_W, 58));
        strip.setAlignmentX(Component.LEFT_ALIGNMENT);

        strip.add(new JLabel(new ImageIcon(buildAvatar(34))), BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel name = new JLabel(currentStaff.getFullName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 12));
        name.setForeground(TEXT_WHITE);

        JLabel role = new JLabel(currentStaff.getRole().toUpperCase());
        role.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        role.setForeground(TEXT_MUTED);

        info.add(name);
        info.add(Box.createVerticalStrut(2));
        info.add(role);
        strip.add(info, BorderLayout.CENTER);
        return strip;
    }

    private JSeparator sidebarDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(DIVIDER);
        sep.setBackground(BG_SIDEBAR);
        sep.setMaximumSize(new Dimension(SIDEBAR_W, 1));
        return sep;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CONTENT AREA
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout(0, 0));
        content.setBackground(BG_CONTENT);
        content.add(buildTopBar(),    BorderLayout.NORTH);
        content.add(buildHero(),      BorderLayout.CENTER);
        return content;
    }

    // Top breadcrumb + live clock bar
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_CONTENT);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER),
                BorderFactory.createEmptyBorder(10, 24, 10, 24)
        ));

        JLabel crumb = new JLabel("Home  ›  Main Menu");
        crumb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        crumb.setForeground(TEXT_MUTED);

        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clockLabel.setForeground(TEXT_MUTED);
        updateClock();

        bar.add(crumb,      BorderLayout.WEST);
        bar.add(clockLabel, BorderLayout.EAST);
        return bar;
    }

    // Hero welcome panel — subtle corner glow replaces the buggy stripes
    private JPanel buildHero() {
        JPanel hero = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // Soft radial glow in the top-right corner — intentional & subtle
                int glowSize = 320;
                g2.setPaint(new RadialGradientPaint(
                        new java.awt.geom.Point2D.Float(getWidth(), 0),
                        glowSize,
                        new float[]  { 0f,    1f   },
                        new Color[]  { new Color(245, 196, 0, 18), new Color(0, 0, 0, 0) }
                ));
                g2.fillRect(getWidth() - glowSize, 0, glowSize, glowSize);

                // Subtle dot grid — bottom left
                g2.setColor(new Color(245, 196, 0, 14));
                for (int row = 0; row < 5; row++)
                    for (int col = 0; col < 5; col++)
                        g2.fillOval(28 + col * 18, getHeight() - 112 + row * 18, 5, 5);

                g2.dispose();
            }
        };
        hero.setBackground(BG_HERO);
        hero.setLayout(new GridBagLayout());
        hero.setBorder(BorderFactory.createEmptyBorder(50, 54, 50, 54));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Greeting — uses real staff name
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel greet = new JLabel("Good day, " + currentStaff.getFullName() + ".");
        greet.setFont(new Font("Segoe UI", Font.BOLD, 34));
        greet.setForeground(TEXT_WHITE);
        hero.add(greet, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(8, 0, 0, 0);
        JLabel sub = new JLabel(
                "Manage orders, track transactions, and keep the cafeteria running smoothly.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(TEXT_MUTED);
        hero.add(sub, gbc);

        // Yellow rule
        gbc.gridy = 2;
        gbc.insets = new Insets(30, 0, 30, 0);
        JPanel rule = new JPanel();
        rule.setBackground(YELLOW);
        rule.setPreferredSize(new Dimension(0, 2));
        hero.add(rule, gbc);

        // Quick-action pill buttons — wired to real navigation
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actions.setOpaque(false);

        actions.add(pillButton("New Order", true, () -> {
            new OrderingFrame(currentStaff).setVisible(true);
            dispose();
        }));
        actions.add(pillButton("Order List", false, () -> {
            new OrderList(currentStaff).setVisible(true);
            dispose();
        }));
        actions.add(pillButton("Transaction History", false, () -> {
            new TransacHistory(currentStaff).setVisible(true);
            dispose();
        }));

        hero.add(actions, gbc);
        return hero;
    }

    // Pill-style button with real action
    private JButton pillButton(String text, boolean primary, Runnable action) {
        Color bg      = primary ? YELLOW              : BG_CARD;
        Color fg      = primary ? new Color(20,20,20) : TEXT_WHITE;
        Color hoverBg = primary ? new Color(255, 215, 50) : new Color(40, 40, 40);

        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hoverBg : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                if (!primary) {
                    g2.setColor(new Color(50, 50, 50));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        btn.addActionListener(e -> action.run());
        return btn;
    }


    private JPanel statTile(String label, String value, String sub, boolean accent) {
        JPanel tile = new JPanel();
        tile.setLayout(new BoxLayout(tile, BoxLayout.Y_AXIS));
        tile.setBackground(BG_CARD);
        tile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, DIVIDER),
                BorderFactory.createEmptyBorder(18, 24, 18, 24)
        ));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 20));
        val.setForeground(accent ? GREEN_ONLINE : TEXT_WHITE);
        val.setAlignmentX(Component.LEFT_ALIGNMENT);
        val.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 0));

        tile.add(lbl);
        tile.add(val);
        if (!sub.isEmpty()) {
            JLabel subLbl = new JLabel(sub);
            subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            subLbl.setForeground(TEXT_DIM);
            subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            tile.add(subLbl);
        }
        return tile;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  LIVE CLOCK
    // ══════════════════════════════════════════════════════════════════════════
    private void startClock() {
        new javax.swing.Timer(1000, e -> updateClock()).start();
    }

    private void updateClock() {
        if (clockLabel != null)
            clockLabel.setText(java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern(
                            "EEEE, MMMM d, yyyy   hh:mm:ss a")));
    }

    // ── Logout helper ─────────────────────────────────────────────────────────
    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?",
                "Log Out", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  GRAPHICS HELPERS — no external image files needed
    // ══════════════════════════════════════════════════════════════════════════
    private BufferedImage buildLogo(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(YELLOW);
        g.fillOval(0, 0, size, size);
        g.setColor(BG_SIDEBAR);
        float sw = size * 0.055f;
        g.setStroke(new BasicStroke(sw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int m = size / 4;
        g.drawOval(m, m + 2, size - m * 2, size - m * 2 - 2);
        g.drawLine(m + 2, m - 2, m + 2, m + 7);
        g.drawLine(m - 1, m - 2, m - 1, m + 4);
        g.drawLine(m + 5, m - 2, m + 5, m + 4);
        g.drawLine(m - 1, m + 4, m + 2, m + 7);
        g.drawLine(m + 5, m + 4, m + 2, m + 7);
        int kx = size - m - 2;
        g.drawLine(kx, m - 2, kx, size - m + 2);
        g.drawLine(kx, m - 2, kx + 3, m + 3);
        g.drawLine(kx + 3, m + 3, kx, m + 8);
        g.dispose();
        return img;
    }

    private BufferedImage buildAvatar(int size) {
        // Uses the first letter of the staff's first name
        String letter = currentStaff.getFullName().trim().isEmpty()
                ? "?" : String.valueOf(currentStaff.getFullName().trim().charAt(0)).toUpperCase();
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(YELLOW_MID);
        g.fillOval(0, 0, size, size);
        g.setColor(YELLOW);
        g.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(letter,
                (size - fm.stringWidth(letter)) / 2,
                (size - fm.getHeight()) / 2 + fm.getAscent());
        g.dispose();
        return img;
    }

    private BufferedImage navIcon(java.util.function.Consumer<Graphics2D> draw) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(TEXT_MUTED);
        g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        draw.accept(g);
        g.dispose();
        return img;
    }

    private BufferedImage drawOrderIcon() {
        return navIcon(g -> {
            g.drawRoundRect(2, 1, 12, 14, 3, 3);
            g.drawLine(5, 5, 11, 5); g.drawLine(5, 8, 11, 8); g.drawLine(5, 11, 9, 11);
        });
    }
    private BufferedImage drawListIcon() {
        return navIcon(g -> {
            g.fillOval(2, 4, 3, 3);  g.drawLine(7, 5,  14, 5);
            g.fillOval(2, 9, 3, 3);  g.drawLine(7, 10, 14, 10);
            g.fillOval(2, 13, 3, 3); g.drawLine(7, 14, 12, 14);
        });
    }
    private BufferedImage drawHistIcon() {
        return navIcon(g -> {
            g.drawOval(1, 1, 14, 14);
            g.drawLine(8, 4, 8, 8); g.drawLine(8, 8, 11, 11);
        });
    }
    private BufferedImage drawMenuIcon() {
        return navIcon(g -> {
            g.drawRoundRect(1, 1, 12, 14, 3, 3);
            g.drawLine(4, 5, 10, 5); g.drawLine(4, 8, 10, 8); g.drawLine(4, 11, 8, 11);
        });
    }
    private BufferedImage drawLogoutIcon() {
        return navIcon(g -> {
            g.setColor(RED_NAV);
            g.drawRoundRect(1, 2, 8, 12, 2, 2);
            g.drawLine(7, 8, 15, 8);
            g.drawLine(12, 5, 15, 8); g.drawLine(12, 11, 15, 8);
        });
    }
}