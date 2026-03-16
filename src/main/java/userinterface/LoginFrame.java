package userinterface;

import database.StaffDB;
import model.Staff;
import util.AppConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         statusLabel;
    private StaffDB        staffDAO;

    public LoginFrame() {
        staffDAO = new StaffDB();
        initUI();
    }

    private void initUI() {
        // ── Window settings ──
        setTitle(AppConstants.APP_TITLE + " — Login");
        setSize(560, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Main panel ──
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ── Title label ──
        JLabel titleLabel = new JLabel(AppConstants.APP_TITLE, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(245, 196, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // ── Form panel ──
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 245));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(245, 196, 0), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(6, 6, 6, 6);
        gbc.fill    = GridBagConstraints.NONE;
        gbc.weightx = 0;

        // ── Username row ──
        gbc.gridx  = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx  = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(300, 28));
        formPanel.add(usernameField, gbc);

        // ── Password row ──
        gbc.gridx  = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx  = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(300, 28));
        formPanel.add(passwordField, gbc);

        // ── Login button —
        gbc.gridx   = 1; gbc.gridy = 2;
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.insets  = new Insets(14, 6, 4, 6);
        JButton loginButton = new JButton("Log In");
        loginButton.setPreferredSize(new Dimension(300, 32));
        loginButton.setBackground(new Color(245, 196, 0));
        loginButton.setForeground(new Color(30, 30, 30));
        loginButton.setFont(new Font("Arial", Font.BOLD, 13));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(loginButton, gbc);

        // ── Status / error label ──
        gbc.gridy  = 3;
        gbc.fill   = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(4, 6, 4, 6);
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        formPanel.add(statusLabel, gbc);

        // ── Assemble ──
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel,  BorderLayout.CENTER);
        add(mainPanel);

        // ── Login button action ──
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
            MainMenuFrame mainMenu = new MainMenuFrame(loggedInStaff);
            mainMenu.setVisible(true);
            dispose();
        } else {
            statusLabel.setText("Invalid username or password.");
            passwordField.setText("");
        }
    }
}