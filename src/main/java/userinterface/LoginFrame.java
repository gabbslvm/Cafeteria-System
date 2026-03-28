package userinterface;

import database.StaffDB;
import model.Staff;
import util.AppConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private StaffDB staffDAO;

    public LoginFrame() {
        staffDAO = new StaffDB();
        setTitle(AppConstants.APP_TITLE + " — Login");
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
    private Staff currentStaff;

    public MainMenuFrame(Staff staff) {
        this.currentStaff = staff;
        setTitle(AppConstants.APP_TITLE + " — Main Menu");
        setSize(500, 420);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(20, 20, 20));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        JLabel titleLabel = new JLabel(AppConstants.APP_TITLE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 17));
        titleLabel.setForeground(new Color(245, 196, 0));
        JLabel welcomeLabel = new JLabel(
                "Welcome, " + currentStaff.getFullName() + "  |  " + currentStaff.getRole().toUpperCase());
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        welcomeLabel.setForeground(new Color(200, 200, 200));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(welcomeLabel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBackground(new Color(30, 30, 30));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JButton newOrderBtn = createMenuButton("New Order", new Color(245, 196, 0));
        JButton orderListBtn = createMenuButton("Order List", new Color(245, 196, 0));
        JButton historyBtn = createMenuButton("Transaction History", new Color(245, 196, 0));
        JButton logoutBtn = createMenuButton("Log Out", new Color(180, 60, 60));

        if ("manager".equalsIgnoreCase(currentStaff.getRole())) {
            JButton menuMgmtBtn = createMenuButton("⚙️  Menu Management", new Color(245, 196, 0));
            buttonPanel.setLayout(new GridLayout(5, 1, 10, 10));
            buttonPanel.add(newOrderBtn);
            buttonPanel.add(orderListBtn);
            buttonPanel.add(historyBtn);
            buttonPanel.add(menuMgmtBtn);
            buttonPanel.add(logoutBtn);
            menuMgmtBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Menu Management — coming soon!"));
        } else {
            buttonPanel.add(newOrderBtn);
            buttonPanel.add(orderListBtn);
            buttonPanel.add(historyBtn);
            buttonPanel.add(logoutBtn);
        }

        // ── FIXED: dispose() ensures only one window is open at a time ──
        newOrderBtn.addActionListener(e -> {
            new OrderingFrame(currentStaff).setVisible(true);
            dispose();
        });
        orderListBtn.addActionListener(e -> {
            new OrderList(currentStaff).setVisible(true);
            dispose();
        });
        historyBtn.addActionListener(e -> {
            new TransacHistory(currentStaff).setVisible(true);
            dispose();
        });
        logoutBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Log Out",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JButton createMenuButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(bgColor.equals(new Color(245, 196, 0)) ? new Color(20, 20, 20) : Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return btn;
    }
}