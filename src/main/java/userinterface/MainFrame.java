package userinterface;

import database.StaffDB;
import model.Staff;
import util.AppConstants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

public class MainFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private StaffDB staffDAO;

    public MainFrame() {
        staffDAO = new StaffDB();
        setTitle(AppConstants.APP_TITLE + " | Login");
        setSize(640, 440);
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

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(300, 28));
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(300, 28));
        formPanel.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
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

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
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

class MainMenuFrame extends JFrame {

    private static final Color BG_SIDEBAR = new Color(10, 10, 10);
    private static final Color BG_CONTENT = new Color(22, 22, 22);
    private static final Color BG_CARD = new Color(28, 28, 28);
    private static final Color BG_HERO = new Color(26, 26, 26);
    private static final Color YELLOW = new Color(245, 196, 0);
    private static final Color YELLOW_MID = new Color(245, 196, 0, 110);
    private static final Color TEXT_WHITE = new Color(232, 232, 232);
    private static final Color TEXT_MUTED = new Color(140, 140, 140);
    private static final Color TEXT_DIM = new Color(70, 70, 70);
    private static final Color DIVIDER = new Color(32, 32, 32);
    private static final Color SIDEBAR_HOVER = new Color(22, 22, 22);
    private static final Color RED_NAV = new Color(180, 65, 65);
    private static final Color RED_HOVER = new Color(215, 70, 70);
    private static final Color GREEN_ONLINE = new Color(72, 199, 116);

    private static final int SIDEBAR_W = 280;
    private static final int NAV_H = 56;
    private static final int LOGO_SIZE = 240;
    private static final int AVATAR_SIZE = 44;

    private static final String LOGO_IMAGE_PATH = "/assets/Logo_official.png";

    private final Staff currentStaff;
    private JLabel clockLabel;

    public MainMenuFrame(Staff staff) {
        this.currentStaff = staff;

        setTitle(AppConstants.APP_TITLE + " | Main Menu");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 750);
        setLocationRelativeTo(null);
        setResizable(false);

        UIManager.put("OptionPane.background", BG_CARD);
        UIManager.put("Panel.background", BG_CARD);
        UIManager.put("OptionPane.messageForeground", TEXT_WHITE);
        UIManager.put("Button.background", YELLOW);
        UIManager.put("Button.foreground", Color.BLACK);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_SIDEBAR);
        setContentPane(root);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContent(), BorderLayout.CENTER);

        startClock();
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(SIDEBAR_W, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, DIVIDER));

        JPanel brand = new JPanel();
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setOpaque(false);
        brand.setBorder(BorderFactory.createEmptyBorder(28, 22, 24, 22));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel logoContainer = buildLogoContainer();
        logoContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.add(logoContainer);

        brand.add(Box.createVerticalStrut(14));

        JLabel sysName = new JLabel(AppConstants.APP_TITLE);
        sysName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sysName.setForeground(YELLOW);
        sysName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sysSub = new JLabel("Ordering & Billing System");
        sysSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sysSub.setForeground(TEXT_MUTED);
        sysSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sysSub.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));

        brand.add(sysName);
        brand.add(sysSub);
        sidebar.add(brand);

        sidebar.add(sidebarDivider());
        sidebar.add(sectionLabel("NAVIGATION"));

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

        if ("manager".equalsIgnoreCase(currentStaff.getRole())) {
            sidebar.add(navItem("Menu Management", drawMenuIcon(), () -> {
                new MenuManagementFrame(currentStaff).setVisible(true);
                dispose();
            }));
        }

        sidebar.add(Box.createVerticalGlue());

        sidebar.add(sidebarDivider());
        sidebar.add(buildUserStrip());
        sidebar.add(sidebarDivider());
        sidebar.add(logoutItem());
        sidebar.add(Box.createVerticalStrut(14));

        return sidebar;
    }

    private JPanel buildLogoContainer() {
        BufferedImage logoImg = tryLoadLogo(LOGO_SIZE);

        if (logoImg != null) {
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            wrapper.setOpaque(false);
            wrapper.setMaximumSize(new Dimension(LOGO_SIZE, LOGO_SIZE));
            wrapper.add(new JLabel(new ImageIcon(logoImg)));
            return wrapper;
        }

        JPanel placeholder = new JPanel(new BorderLayout(0, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 196, 0, 80));
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND, 0, new float[] { 5, 4 }, 0));
                g2.drawOval(1, 1, LOGO_SIZE - 2, LOGO_SIZE - 2);
                g2.setColor(new Color(245, 196, 0, 15));
                g2.fillOval(1, 1, LOGO_SIZE - 2, LOGO_SIZE - 2);
                g2.dispose();
            }
        };
        placeholder.setOpaque(false);
        placeholder.setPreferredSize(new Dimension(LOGO_SIZE, LOGO_SIZE));
        placeholder.setMaximumSize(new Dimension(LOGO_SIZE, LOGO_SIZE));

        JPanel camIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 196, 0, 120));
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(2, 5, 20, 14, 3, 3);
                g2.drawOval(8, 8, 8, 8);
                g2.drawLine(8, 5, 10, 2);
                g2.drawLine(10, 2, 14, 2);
                g2.drawLine(14, 2, 16, 5);
                g2.dispose();
            }
        };
        camIcon.setOpaque(false);
        camIcon.setPreferredSize(new Dimension(LOGO_SIZE, 26));

        JLabel hint = new JLabel("Place logo at", SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        hint.setForeground(new Color(245, 196, 0, 100));

        JLabel path = new JLabel("assets/logo.png", SwingConstants.CENTER);
        path.setFont(new Font("Segoe UI", Font.BOLD, 9));
        path.setForeground(new Color(245, 196, 0, 150));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        path.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(hint);
        textPanel.add(path);

        placeholder.add(camIcon, BorderLayout.CENTER);
        placeholder.add(textPanel, BorderLayout.SOUTH);

        return placeholder;
    }

    private BufferedImage tryLoadLogo(int size) {
        try {
            java.net.URL url = MainMenuFrame.class.getResource(LOGO_IMAGE_PATH);
            if (url != null) {
                Image raw = ImageIO.read(url);
                if (raw != null) {
                    BufferedImage scaled = new BufferedImage(size, size,
                            BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = scaled.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, size, size));
                    g.drawImage(raw, 0, 0, size, size, null);
                    g.dispose();
                    return scaled;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(TEXT_DIM);
        lbl.setBorder(BorderFactory.createEmptyBorder(16, 22, 10, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel navItem(String label, BufferedImage icon, Runnable action) {
        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(4, NAV_H));
        indicator.setMaximumSize(new Dimension(4, NAV_H));
        indicator.setMinimumSize(new Dimension(4, NAV_H));
        indicator.setBackground(YELLOW);
        indicator.setOpaque(false);

        JPanel inner = new JPanel(new BorderLayout(14, 0));
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
        inner.add(new JLabel(new ImageIcon(scaleIcon(icon, 18, 18))),
                BorderLayout.WEST);

        JLabel textLbl = new JLabel(label);
        textLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLbl.setForeground(TEXT_MUTED);
        inner.add(textLbl, BorderLayout.CENTER);

        JPanel row = new JPanel(new BorderLayout(0, 0));
        row.setOpaque(true);
        row.setBackground(BG_SIDEBAR);
        row.setMaximumSize(new Dimension(SIDEBAR_W, NAV_H));
        row.setPreferredSize(new Dimension(SIDEBAR_W, NAV_H));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.add(indicator, BorderLayout.WEST);
        row.add(inner, BorderLayout.CENTER);

        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                row.setBackground(SIDEBAR_HOVER);
                indicator.setOpaque(true);
                textLbl.setForeground(TEXT_WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                row.setBackground(BG_SIDEBAR);
                indicator.setOpaque(false);
                textLbl.setForeground(TEXT_MUTED);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });
        return row;
    }

    private JPanel logoutItem() {
        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(4, NAV_H));
        indicator.setMaximumSize(new Dimension(4, NAV_H));
        indicator.setMinimumSize(new Dimension(4, NAV_H));
        indicator.setBackground(RED_HOVER);
        indicator.setOpaque(false);

        JPanel inner = new JPanel(new BorderLayout(14, 0));
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
        inner.add(new JLabel(new ImageIcon(scaleIcon(drawLogoutIcon(), 18, 18))),
                BorderLayout.WEST);

        JLabel textLbl = new JLabel("Log Out");
        textLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLbl.setForeground(RED_NAV);
        inner.add(textLbl, BorderLayout.CENTER);

        JPanel row = new JPanel(new BorderLayout(0, 0));
        row.setOpaque(true);
        row.setBackground(BG_SIDEBAR);
        row.setMaximumSize(new Dimension(SIDEBAR_W, NAV_H));
        row.setPreferredSize(new Dimension(SIDEBAR_W, NAV_H));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.add(indicator, BorderLayout.WEST);
        row.add(inner, BorderLayout.CENTER);

        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                row.setBackground(new Color(28, 14, 14));
                indicator.setOpaque(true);
                textLbl.setForeground(RED_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                row.setBackground(BG_SIDEBAR);
                indicator.setOpaque(false);
                textLbl.setForeground(RED_NAV);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleLogout();
            }
        });
        return row;
    }

    private JPanel buildUserStrip() {
        JPanel strip = new JPanel(new BorderLayout(14, 0));
        strip.setOpaque(false);
        strip.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        strip.setMaximumSize(new Dimension(SIDEBAR_W, 76));
        strip.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel avatarWrapper = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int dot = 10;
                g2.setColor(BG_SIDEBAR);
                g2.fillOval(AVATAR_SIZE - dot + 1, AVATAR_SIZE - dot + 1,
                        dot + 2, dot + 2);
                g2.setColor(GREEN_ONLINE);
                g2.fillOval(AVATAR_SIZE - dot + 2, AVATAR_SIZE - dot + 2,
                        dot, dot);
                g2.dispose();
            }
        };
        avatarWrapper.setOpaque(false);
        avatarWrapper.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        avatarWrapper.setMaximumSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        JLabel avatarLbl = new JLabel(new ImageIcon(buildAvatar(AVATAR_SIZE)));
        avatarLbl.setBounds(0, 0, AVATAR_SIZE, AVATAR_SIZE);
        avatarWrapper.add(avatarLbl);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel name = new JLabel(currentStaff.getFullName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 13));
        name.setForeground(TEXT_WHITE);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel role = new JLabel(" " + currentStaff.getRole().toUpperCase() + " ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                Color pill = "manager".equalsIgnoreCase(currentStaff.getRole())
                        ? new Color(245, 196, 0, 45)
                        : new Color(70, 130, 200, 45);
                g2.setColor(pill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        role.setFont(new Font("Segoe UI", Font.BOLD, 10));
        role.setForeground("manager".equalsIgnoreCase(currentStaff.getRole())
                ? YELLOW
                : new Color(100, 160, 230));
        role.setAlignmentX(Component.LEFT_ALIGNMENT);
        role.setOpaque(false);

        info.add(name);
        info.add(Box.createVerticalStrut(5));
        info.add(role);

        strip.add(avatarWrapper, BorderLayout.WEST);
        strip.add(info, BorderLayout.CENTER);
        return strip;
    }

    private JSeparator sidebarDivider() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(DIVIDER);
        sep.setBackground(BG_SIDEBAR);
        Dimension size = new Dimension(SIDEBAR_W, 1);
        sep.setPreferredSize(size);
        sep.setMinimumSize(size);
        sep.setMaximumSize(size);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sep;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout(0, 0));
        content.setBackground(BG_CONTENT);
        content.add(buildTopBar(), BorderLayout.NORTH);
        content.add(buildHero(), BorderLayout.CENTER);
        return content;
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_CONTENT);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER),
                BorderFactory.createEmptyBorder(13, 30, 13, 30)));

        JLabel crumb = new JLabel("Home  ›  Main Menu");
        crumb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        crumb.setForeground(TEXT_MUTED);

        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clockLabel.setForeground(TEXT_MUTED);
        updateClock();

        bar.add(crumb, BorderLayout.WEST);
        bar.add(clockLabel, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildHero() {
        JPanel hero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int glowSize = 420;
                g2.setPaint(new RadialGradientPaint(
                        new java.awt.geom.Point2D.Float(getWidth(), 0),
                        glowSize,
                        new float[] { 0f, 1f },
                        new Color[] { new Color(245, 196, 0, 22),
                                new Color(0, 0, 0, 0) }));
                g2.fillRect(getWidth() - glowSize, 0, glowSize, glowSize);
                g2.setColor(new Color(245, 196, 0, 16));
                for (int row = 0; row < 7; row++)
                    for (int col = 0; col < 7; col++)
                        g2.fillOval(32 + col * 22,
                                getHeight() - 160 + row * 22, 5, 5);
                g2.dispose();
            }
        };
        hero.setBackground(BG_HERO);
        hero.setLayout(new GridBagLayout());
        hero.setBorder(BorderFactory.createEmptyBorder(70, 70, 70, 70));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel greet = new JLabel("Good day, " + currentStaff.getFullName() + ".");
        greet.setFont(new Font("Segoe UI", Font.BOLD, 36));
        greet.setForeground(TEXT_WHITE);
        hero.add(greet, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        JLabel sub = new JLabel(
                "<html>Manage orders, track transactions, and keep the cafeteria running smoothly.</html>");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        sub.setForeground(TEXT_MUTED);
        hero.add(sub, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(36, 0, 36, 0);
        JPanel rule = new JPanel();
        rule.setBackground(YELLOW);
        rule.setPreferredSize(new Dimension(0, 2));
        hero.add(rule, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
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
        if ("manager".equalsIgnoreCase(currentStaff.getRole())) {
            actions.add(pillButton("Menu Management", false, () -> {
                new MenuManagementFrame(currentStaff).setVisible(true);
                dispose();
            }));
        }

        hero.add(actions, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(28, 0, 0, 0);
        String tipText = "manager".equalsIgnoreCase(currentStaff.getRole())
                ? "💡  As manager, you can add, edit, and manage menu items and stock via Menu Management."
                : "💡  Use New Order to start a transaction. Track progress in Order List.";
        JLabel tip = new JLabel("<html>" + tipText + "</html>");
        tip.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tip.setForeground(new Color(100, 100, 100));
        hero.add(tip, gbc);

        return hero;
    }

    private JButton pillButton(String text, boolean primary, Runnable action) {
        Color bg = primary ? YELLOW : BG_CARD;
        Color fg = primary ? new Color(20, 20, 20) : TEXT_WHITE;
        Color hoverBg = primary ? new Color(255, 215, 50) : new Color(40, 40, 40);

        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hoverBg : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (!primary) {
                    g2.setColor(new Color(55, 55, 55));
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(fg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(13, 26, 13, 26));
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private void startClock() {
        new javax.swing.Timer(1000, e -> updateClock()).start();
    }

    private void updateClock() {
        if (clockLabel != null)
            clockLabel.setText(java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern(
                            "EEEE, MMMM d, yyyy   hh:mm:ss a")));
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?",
                "Log Out", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            new MainFrame().setVisible(true);
            dispose();
        }
    }

    private BufferedImage buildAvatar(int size) {
        String letter = currentStaff.getFullName().trim().isEmpty() ? "?"
                : String.valueOf(currentStaff.getFullName().trim().charAt(0)).toUpperCase();
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

    private BufferedImage scaleIcon(BufferedImage src, int w, int h) {
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        return out;
    }

    private BufferedImage navIcon(java.util.function.Consumer<Graphics2D> draw) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(TEXT_MUTED);
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        draw.accept(g);
        g.dispose();
        return img;
    }

    private BufferedImage drawOrderIcon() {
        return navIcon(g -> {
            g.drawRoundRect(2, 1, 12, 14, 3, 3);
            g.drawLine(5, 5, 11, 5);
            g.drawLine(5, 8, 11, 8);
            g.drawLine(5, 11, 9, 11);
        });
    }

    private BufferedImage drawListIcon() {
        return navIcon(g -> {
            g.fillOval(2, 4, 3, 3);
            g.drawLine(7, 5, 14, 5);
            g.fillOval(2, 9, 3, 3);
            g.drawLine(7, 10, 14, 10);
            g.fillOval(2, 13, 3, 3);
            g.drawLine(7, 14, 12, 14);
        });
    }

    private BufferedImage drawHistIcon() {
        return navIcon(g -> {
            g.drawOval(1, 1, 14, 14);
            g.drawLine(8, 4, 8, 8);
            g.drawLine(8, 8, 11, 11);
        });
    }

    private BufferedImage drawMenuIcon() {
        return navIcon(g -> {
            g.drawRoundRect(1, 1, 12, 14, 3, 3);
            g.drawLine(4, 5, 10, 5);
            g.drawLine(4, 8, 10, 8);
            g.drawLine(4, 11, 8, 11);
        });
    }

    private BufferedImage drawLogoutIcon() {
        return navIcon(g -> {
            g.setColor(RED_NAV);
            g.drawRoundRect(1, 2, 8, 12, 2, 2);
            g.drawLine(7, 8, 15, 8);
            g.drawLine(12, 5, 15, 8);
            g.drawLine(12, 11, 15, 8);
        });
    }
}