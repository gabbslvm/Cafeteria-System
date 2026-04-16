package userinterface;

import database.StaffDB;
import model.Staff;
import util.AppConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StaffManagementFrame extends JFrame {

    private static final Color BG_DARK    = new Color(30, 30, 30),
                               BG_DARKER  = new Color(20, 20, 20),
                               BG_PANEL   = new Color(40, 40, 40),
                               ACCENT     = new Color(245, 196, 0),
                               TEXT_MAIN  = new Color(240, 240, 240),
                               TEXT_MUTED = new Color(150, 150, 150),
                               RED_BTN    = new Color(180, 60, 60),
                               GREEN_BTN  = new Color(50, 160, 80),
                               BLUE_BTN   = new Color(60, 110, 200);

    private static final String[] ROLES = { "Cashier", "Manager" };

    private final Staff currentStaff;
    private final StaffDB staffDB = new StaffDB();
    private DefaultTableModel tableModel;
    private JTable staffTable;
    private List<Staff> staffList;

    public StaffManagementFrame(Staff staff) {
        this.currentStaff = staff;
        initUI();
        loadStaff();
    }

    private void initUI() {
        setTitle(AppConstants.APP_TITLE + " | Staff Management");
        setSize(740, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.add(header(), BorderLayout.NORTH);
        root.add(content(), BorderLayout.CENTER);
        root.add(footer(), BorderLayout.SOUTH);
        add(root);
    }

    private JPanel header() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_DARKER);
        p.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("Staff Management");
        title.setFont(new Font("Arial", Font.BOLD, 17));
        title.setForeground(ACCENT);
        JLabel sub = new JLabel("Logged in as: " + currentStaff.getFullName()
                + "  |  Add, edit, or remove staff accounts");
        sub.setFont(new Font("Arial", Font.PLAIN, 11));
        sub.setForeground(TEXT_MUTED);
        p.add(title, BorderLayout.NORTH);
        p.add(sub, BorderLayout.SOUTH);
        return p;
    }

    private JPanel content() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));

        String[] cols = { "ID", "Full Name", "Username", "Role" };
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        staffTable = new JTable(tableModel);
        styleTable(staffTable);
        staffTable.getColumnModel().getColumn(0).setMaxWidth(50);
        staffTable.getColumnModel().getColumn(3).setMaxWidth(100);

        JScrollPane scroll = new JScrollPane(staffTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(55, 55, 55)));
        scroll.getViewport().setBackground(BG_PANEL);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private void styleTable(JTable t) {
        t.setBackground(BG_PANEL);
        t.setForeground(TEXT_MAIN);
        t.setFont(new Font("Arial", Font.PLAIN, 13));
        t.setRowHeight(34);
        t.setGridColor(new Color(55, 55, 55));
        t.setSelectionBackground(new Color(60, 60, 60));
        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        t.getTableHeader().setBackground(BG_DARKER);
        t.getTableHeader().setForeground(TEXT_MUTED);
        t.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
    }

    private JPanel footer() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 8));
        p.setBackground(BG_DARKER);

        JButton back = btn("← Back", new Color(60, 60, 60));
        back.addActionListener(e -> { new MainMenuFrame(currentStaff).setVisible(true); dispose(); });

        JButton add = btn("Add Staff", GREEN_BTN);
        add.addActionListener(e -> openForm(null));

        JButton edit = btn("Edit", BLUE_BTN);
        edit.addActionListener(e -> {
        int row = staffTable.getSelectedRow();
        if (row < 0) { warn("Select a staff member to edit."); return; }
        openForm(staffList.get(row));
    });

        JButton delete = btn("Delete", RED_BTN);
        delete.addActionListener(e -> {
        int row = staffTable.getSelectedRow();
        if (row < 0) { warn("Select a staff member to delete."); return; }
        deleteStaff(staffList.get(row));
    });

        JButton refresh = btn("Refresh", new Color(80, 80, 80));
        refresh.addActionListener(e -> loadStaff());

        p.add(back); p.add(add); p.add(edit); p.add(delete); p.add(refresh);
        return p;
    }

    private void loadStaff() {
        staffList = staffDB.getAllStaff();
        tableModel.setRowCount(0);
        for (Staff s : staffList)
            tableModel.addRow(new Object[] {
                s.getStaffId(), s.getFullName(), s.getUsername(), s.getRole()
            });
    }

    private void openForm(Staff existing) {
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog(this, isEdit ? "Edit Staff" : "Add Staff", true);
        dialog.setSize(380, 310);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel(isEdit ? "Edit — " + existing.getFullName() : "New Staff Account");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JTextField nameField = field(isEdit ? existing.getFullName() : "");
        JTextField userField = field(isEdit ? existing.getUsername()  : "");
        JPasswordField passField = passField(isEdit ? existing.getPassword() : "");
        JComboBox<String> roleBox = new JComboBox<>(ROLES);
        styleCombo(roleBox);
        if (isEdit) roleBox.setSelectedItem(existing.getRole());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BG_DARK);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 4, 5, 4);
        g.fill = GridBagConstraints.HORIZONTAL;
        addRow(form, g, 0, "Full Name:", nameField);
        addRow(form, g, 1, "Username:",  userField);
        addRow(form, g, 2, "Password:",  passField);
        addRow(form, g, 3, "Role:",      roleBox);

        JButton save = btn(isEdit ? "Save Changes" : "Add Staff", GREEN_BTN);
        save.addActionListener(e -> {
            String name = nameField.getText().trim();
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword()).trim();
            String role = (String) roleBox.getSelectedItem();

            if (name.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                warn("All fields are required."); return;
            }
            int excludeId = isEdit ? existing.getStaffId() : -1;
            if (staffDB.usernameExists(user, excludeId)) {
                warn("Username \"" + user + "\" is already taken."); return;
            }

            Staff s = isEdit
                ? new Staff(existing.getStaffId(), user, pass, name, role)
                : new Staff(0, user, pass, name, role);

            if (isEdit) staffDB.updateStaff(s);
            else        staffDB.addStaff(s);

            dialog.dispose();
            loadStaff();
            JOptionPane.showMessageDialog(this,
                "Staff " + (isEdit ? "updated" : "added") + " successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton cancel = btn("Cancel", new Color(60, 60, 60));
        cancel.addActionListener(e -> dialog.dispose());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnRow.setBackground(BG_DARK);
        btnRow.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        btnRow.add(cancel); btnRow.add(save);

        root.add(title, BorderLayout.NORTH);
        root.add(form,  BorderLayout.CENTER);
        root.add(btnRow, BorderLayout.SOUTH);
        dialog.add(root);
        dialog.setVisible(true);
    }

    private void deleteStaff(Staff s) {
        if (s.getStaffId() == currentStaff.getStaffId()) {
            warn("You cannot delete your own account."); return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "<html>Delete <b>" + s.getFullName() + "</b>? This cannot be undone.</html>",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            staffDB.deleteStaff(s.getStaffId());
            loadStaff();
            JOptionPane.showMessageDialog(this, "Staff deleted successfully.",
                "Deleted", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    private void addRow(JPanel form, GridBagConstraints g, int row, String label, JComponent field) {
        g.gridy = row; g.gridx = 0; g.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(TEXT_MUTED);
        form.add(lbl, g);
        g.gridx = 1; g.weightx = 0.65;
        form.add(field, g);
    }

    private JTextField field(String value) {
        JTextField f = new JTextField(value);
        f.setBackground(BG_PANEL); f.setForeground(TEXT_MAIN);
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return f;
    }

    private JPasswordField passField(String value) {
        JPasswordField f = new JPasswordField(value);
        f.setBackground(BG_PANEL); f.setForeground(TEXT_MAIN);
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return f;
    }

    private void styleCombo(JComboBox<String> box) {
        box.setBackground(BG_PANEL); box.setForeground(TEXT_MAIN);
        box.setFont(new Font("Arial", Font.PLAIN, 13));
    }

    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        return b;
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}