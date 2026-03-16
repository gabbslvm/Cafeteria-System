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
        mainPanel.setBackground(new Color(30, 30, 30));

        // ── Header ──
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(20, 20, 20));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel titleLabel = new JLabel(AppConstants.APP_TITLE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 17));
        titleLabel.setForeground(new Color(245, 196, 0));

        JLabel welcomeLabel = new JLabel("Welcome, " + currentStaff.getFullName()
                                         + "  |  " + currentStaff.getRole().toUpperCase());
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        welcomeLabel.setForeground(new Color(200, 200, 200));

        headerPanel.add(titleLabel,   BorderLayout.NORTH);
        headerPanel.add(welcomeLabel, BorderLayout.SOUTH);

        // ── Button panel ──
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBackground(new Color(30, 30, 30));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JButton newOrderBtn  = createMenuButton("🛒  New Order",       new Color(245, 196, 0));
        JButton orderListBtn = createMenuButton("📋  Order List",      new Color(245, 196, 0));
        JButton historyBtn   = createMenuButton("📊  Transaction History", new Color(245, 196, 0));
        JButton logoutBtn    = createMenuButton("🔓  Log Out",         new Color(180, 60, 60));

        // Manager-only: Menu Management button
        if ("manager".equalsIgnoreCase(currentStaff.getRole())) {
            JButton menuMgmtBtn = createMenuButton("⚙️  Menu Management", new Color(245, 196, 0));
            buttonPanel.setLayout(new GridLayout(5, 1, 10, 10));
            buttonPanel.add(newOrderBtn);
            buttonPanel.add(orderListBtn);
            buttonPanel.add(historyBtn);
            buttonPanel.add(menuMgmtBtn);
            buttonPanel.add(logoutBtn);
            // Wire menu management
            menuMgmtBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Menu Management — coming soon!"));
        } else {
            buttonPanel.add(newOrderBtn);
            buttonPanel.add(orderListBtn);
            buttonPanel.add(historyBtn);
            buttonPanel.add(logoutBtn);
        }

        // ── Wire buttons ──
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
        boolean isYellow = bgColor.equals(new Color(245, 196, 0)) || bgColor.equals(new Color(200, 160, 0));
        btn.setForeground(isYellow ? new Color(20, 20, 20) : Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return btn;
    }
}