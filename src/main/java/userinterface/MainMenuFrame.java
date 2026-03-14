package userinterface;

import model.Staff;
import util.AppConstants;

import javax.swing.*;
import java.awt.*;

public class MainMenuFrame extends JFrame {

    private Staff currentStaff;

    public MainMenuFrame(Staff staff) {
        this.currentStaff = staff;
        initUI();
    }

    private void initUI() {
        setTitle(AppConstants.APP_TITLE + " — Main Menu");
        setSize(500, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Main panel ──
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        // ── Header ──
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(26, 60, 110));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel titleLabel = new JLabel(AppConstants.APP_TITLE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 17));
        titleLabel.setForeground(Color.WHITE);

        JLabel welcomeLabel = new JLabel("Welcome, " + currentStaff.getFullName()
                                         + "  |  " + currentStaff.getRole().toUpperCase());
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        welcomeLabel.setForeground(new Color(180, 200, 230));

        headerPanel.add(titleLabel,   BorderLayout.NORTH);
        headerPanel.add(welcomeLabel, BorderLayout.SOUTH);

        // ── Button panel ──
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JButton newOrderBtn  = createMenuButton("🛒  New Order",       new Color(46, 109, 180));
        JButton orderListBtn = createMenuButton("📋  Order List",      new Color(46, 109, 180));
        JButton historyBtn   = createMenuButton("📊  Transaction History", new Color(46, 109, 180));
        JButton logoutBtn    = createMenuButton("🔓  Log Out",         new Color(180, 60, 60));

        // Manager-only: Menu Management button
        if ("manager".equalsIgnoreCase(currentStaff.getRole())) {
            JButton menuMgmtBtn = createMenuButton("⚙️  Menu Management", new Color(80, 140, 80));
            buttonPanel.setLayout(new GridLayout(5, 1, 10, 10));
            buttonPanel.add(newOrderBtn);
            buttonPanel.add(orderListBtn);
            buttonPanel.add(historyBtn);
            buttonPanel.add(menuMgmtBtn);
            buttonPanel.add(logoutBtn);
            // Wire menu management (placeholder for now)
            menuMgmtBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Menu Management — coming soon!"));
        } else {
            buttonPanel.add(newOrderBtn);
            buttonPanel.add(orderListBtn);
            buttonPanel.add(historyBtn);
            buttonPanel.add(logoutBtn);
        }

        // ── Wire buttons (placeholders for now) ──
        newOrderBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Ordering screen — coming soon!"));
        orderListBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Order List screen — coming soon!"));
        historyBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Transaction History — coming soon!"));

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?", "Log Out", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        // ── Assemble ──
        mainPanel.add(headerPanel,  BorderLayout.NORTH);
        mainPanel.add(buttonPanel,  BorderLayout.CENTER);
        add(mainPanel);
    }

    private JButton createMenuButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return btn;
    }
}