package userinterface;

import database.MenuItemDB;
import model.MenuItem;
import model.Staff;
import util.AppConstants;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class MenuManagementFrame extends JFrame {

    private static final Color BG_DARK   = new Color(30, 30, 30),
            BG_DARKER  = new Color(20, 20, 20),
            BG_PANEL   = new Color(40, 40, 40),
            ACCENT     = new Color(245, 196, 0),
            TEXT_MAIN  = new Color(240, 240, 240),
            TEXT_MUTED = new Color(150, 150, 150),
            RED_BTN    = new Color(180, 60, 60),
            GREEN_BTN  = new Color(50, 160, 80),
            BLUE_BTN   = new Color(50, 120, 200);

    private final Staff currentStaff;
    private final MenuItemDB menuItemDB = new MenuItemDB();

    private JTable menuTable;
    private DefaultTableModel tableModel;
    private List<MenuItem> menuItems;

    // ── Form fields ──
    private JTextField nameField, priceField, stockField;
    private JComboBox<String> categoryCombo;
    private JCheckBox availableCheckBox;
    private JButton saveBtn;
    private int editingIndex = -1; // -1 = adding new, >= 0 = editing existing

    public MenuManagementFrame(Staff staff) {
        this.currentStaff = staff;
        initUI();
        loadMenu();
    }

    private void initUI() {
        setTitle(AppConstants.APP_TITLE + " | Menu Management");
        setSize(1050, 640);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.add(header(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        add(root);
    }

    // ══════════════════════════════════════════════════════════════════════
    // HEADER
    // ══════════════════════════════════════════════════════════════════════
    private JPanel header() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_DARKER);
        p.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("Menu Management");
        title.setFont(new Font("Arial", Font.BOLD, 17));
        title.setForeground(ACCENT);
        JLabel sub = new JLabel("Manager: " + currentStaff.getFullName()
                + "  |  Add, edit, delete menu items and manage stock.");
        sub.setFont(new Font("Arial", Font.PLAIN, 11));
        sub.setForeground(TEXT_MUTED);
        p.add(title, BorderLayout.NORTH);
        p.add(sub, BorderLayout.SOUTH);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════
    // CENTER — table on left, form on right
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setBackground(BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        p.add(buildTablePanel(), BorderLayout.CENTER);
        p.add(buildFormPanel(), BorderLayout.EAST);
        return p;
    }

    // ── Left: menu table ──────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_DARK);

        String[] cols = { "ID", "Name", "Category", "Price", "Stock", "Available" };
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        menuTable = new JTable(tableModel);
        styleTable(menuTable);

        // Column widths
        menuTable.getColumnModel().getColumn(0).setMaxWidth(50);
        menuTable.getColumnModel().getColumn(0).setMinWidth(50);
        menuTable.getColumnModel().getColumn(3).setMaxWidth(80);
        menuTable.getColumnModel().getColumn(3).setMinWidth(80);
        menuTable.getColumnModel().getColumn(4).setMaxWidth(70);
        menuTable.getColumnModel().getColumn(4).setMinWidth(70);
        menuTable.getColumnModel().getColumn(5).setMaxWidth(80);
        menuTable.getColumnModel().getColumn(5).setMinWidth(80);

        // Stock color renderer — red if low
        menuTable.getColumnModel().getColumn(4).setCellRenderer(new StockRenderer());
        // Available renderer
        menuTable.getColumnModel().getColumn(5).setCellRenderer(new AvailableRenderer());

        // Row click — populate form for editing
        menuTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateFormFromSelection();
        });

        JScrollPane scroll = new JScrollPane(menuTable);
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
        t.setSelectionBackground(new Color(70, 70, 30));
        t.setSelectionForeground(ACCENT);
        t.getTableHeader().setBackground(BG_DARKER);
        t.getTableHeader().setForeground(TEXT_MUTED);
        t.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    // ── Right: add/edit form ──────────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_DARKER);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 55, 55)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        p.setPreferredSize(new Dimension(260, 0));

        JLabel formTitle = new JLabel("Add / Edit Item");
        formTitle.setFont(new Font("Arial", Font.BOLD, 14));
        formTitle.setForeground(ACCENT);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(formTitle);
        p.add(Box.createVerticalStrut(14));

        // Name
        p.add(formLabel("Item Name:"));
        nameField = formTextField();
        p.add(nameField);
        p.add(Box.createVerticalStrut(10));

        // Category
        p.add(formLabel("Category:"));
        categoryCombo = new JComboBox<>(new String[]{ "Meal", "Snack", "Drink", "Combo", "Others" });
        categoryCombo.setBackground(BG_PANEL);
        categoryCombo.setForeground(TEXT_MAIN);
        categoryCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        categoryCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        categoryCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(categoryCombo);
        p.add(Box.createVerticalStrut(10));

        // Price
        p.add(formLabel("Price (₱):"));
        priceField = formTextField();
        p.add(priceField);
        p.add(Box.createVerticalStrut(10));

        // Stock
        p.add(formLabel("Stock:"));
        stockField = formTextField();
        p.add(stockField);
        p.add(Box.createVerticalStrut(10));

        // Available checkbox
        availableCheckBox = new JCheckBox("Available for ordering");
        availableCheckBox.setFont(new Font("Arial", Font.PLAIN, 12));
        availableCheckBox.setForeground(TEXT_MAIN);
        availableCheckBox.setBackground(BG_DARKER);
        availableCheckBox.setFocusPainted(false);
        availableCheckBox.setSelected(true);
        availableCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(availableCheckBox);
        p.add(Box.createVerticalStrut(16));

        // Save button
        saveBtn = new JButton("＋ Add Item");
        saveBtn.setBackground(GREEN_BTN);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 13));
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.setBorder(BorderFactory.createEmptyBorder(9, 0, 9, 0));
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> saveItem());
        p.add(saveBtn);
        p.add(Box.createVerticalStrut(8));

        // Clear/Cancel button
        JButton clearBtn = new JButton("✕ Clear Form");
        clearBtn.setBackground(new Color(60, 60, 60));
        clearBtn.setForeground(TEXT_MUTED);
        clearBtn.setFont(new Font("Arial", Font.BOLD, 12));
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        clearBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        clearBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        clearBtn.addActionListener(e -> clearForm());
        p.add(clearBtn);

        return p;
    }

    private JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN, 12));
        l.setForeground(TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField formTextField() {
        JTextField f = new JTextField();
        f.setBackground(BG_PANEL);
        f.setForeground(TEXT_MAIN);
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    // ══════════════════════════════════════════════════════════════════════
    // FOOTER — delete button
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 8));
        p.setBackground(BG_DARKER);

        JButton back = footerBtn("← Back to Menu", new Color(60, 60, 60));
        back.addActionListener(e -> {
            new MainMenuFrame(currentStaff).setVisible(true);
            dispose();
        });

        JButton deleteBtn = footerBtn("🗑  Delete Selected", RED_BTN);
        deleteBtn.addActionListener(e -> deleteItem());

        JButton refreshBtn = footerBtn("⟳  Refresh", BLUE_BTN);
        refreshBtn.addActionListener(e -> loadMenu());

        p.add(back);
        p.add(deleteBtn);
        p.add(refreshBtn);
        return p;
    }

    private JButton footerBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        return b;
    }

    // ══════════════════════════════════════════════════════════════════════
    // LOGIC
    // ══════════════════════════════════════════════════════════════════════
    private void loadMenu() {
        menuItems = menuItemDB.getAllItems();
        tableModel.setRowCount(0);
        for (MenuItem mi : menuItems) {
            tableModel.addRow(new Object[]{
                    mi.getMenuItemId(),
                    mi.getName(),
                    mi.getCategory(),
                    String.format("₱ %.2f", mi.getPrice()),
                    mi.getStock(),
                    mi.isAvailable() ? "Yes" : "No"
            });
        }
        clearForm();
    }

    private void populateFormFromSelection() {
        int selected = menuTable.getSelectedRow();
        if (selected < 0 || selected >= menuItems.size()) return;
        MenuItem mi = menuItems.get(selected);
        editingIndex = selected;
        nameField.setText(mi.getName());
        categoryCombo.setSelectedItem(mi.getCategory());
        priceField.setText(String.valueOf(mi.getPrice()));
        stockField.setText(String.valueOf(mi.getStock()));
        availableCheckBox.setSelected(mi.isAvailable());
        saveBtn.setText("💾  Save Changes");
        saveBtn.setBackground(BLUE_BTN);
    }

    private void saveItem() {
        // ── Validate inputs ──
        String name = nameField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        String priceText = priceField.getText().trim();
        String stockText = stockField.getText().trim();
        boolean available = availableCheckBox.isSelected();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Item name cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid price.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int stock;
        try {
            stock = Integer.parseInt(stockText);
            if (stock < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid stock number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (editingIndex == -1) {
            // ── CREATE: adding a new item ──
            MenuItem newItem = new MenuItem(0, name, category, price, available, stock);
            menuItemDB.addItem(newItem);
            JOptionPane.showMessageDialog(this,
                    "\"" + name + "\" has been added to the menu.",
                    "Item Added", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // ── UPDATE: editing existing item ──
            MenuItem existing = menuItems.get(editingIndex);
            existing.setName(name);
            existing.setCategory(category);
            existing.setPrice(price);
            existing.setStock(stock);
            existing.setIsAvailable(available);
            menuItemDB.updateItem(existing);
            JOptionPane.showMessageDialog(this,
                    "\"" + name + "\" has been updated.",
                    "Item Updated", JOptionPane.INFORMATION_MESSAGE);
        }
        loadMenu(); // refresh table from DB
    }

    private void deleteItem() {
        int selected = menuTable.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.",
                    "No Item Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        MenuItem mi = menuItems.get(selected);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete \"" + mi.getName() + "\" from the menu?\nThis cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            menuItemDB.deleteItem(mi.getMenuItemId());
            JOptionPane.showMessageDialog(this,
                    "\"" + mi.getName() + "\" has been deleted.",
                    "Item Deleted", JOptionPane.INFORMATION_MESSAGE);
            loadMenu();
        }
    }

    private void clearForm() {
        editingIndex = -1;
        nameField.setText("");
        categoryCombo.setSelectedIndex(0);
        priceField.setText("");
        stockField.setText("");
        availableCheckBox.setSelected(true);
        saveBtn.setText("＋ Add Item");
        saveBtn.setBackground(GREEN_BTN);
        menuTable.clearSelection();
    }

    // ══════════════════════════════════════════════════════════════════════
    // CUSTOM RENDERERS
    // ══════════════════════════════════════════════════════════════════════

    // Stock column — red if ≤5, yellow if ≤20, green otherwise
    private class StockRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            setBackground(sel ? new Color(70, 70, 30) : BG_PANEL);
            setHorizontalAlignment(CENTER);
            try {
                int stock = Integer.parseInt(v.toString());
                if (stock <= 5)       setForeground(new Color(220, 80, 80));   // red
                else if (stock <= 20) setForeground(new Color(245, 196, 0));   // yellow
                else                  setForeground(new Color(100, 200, 100)); // green
            } catch (NumberFormatException e) {
                setForeground(TEXT_MAIN);
            }
            return this;
        }
    }

    // Available column — green Yes / red No
    private class AvailableRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            setBackground(sel ? new Color(70, 70, 30) : BG_PANEL);
            setHorizontalAlignment(CENTER);
            String val = v != null ? v.toString() : "";
            setForeground("Yes".equals(val)
                    ? new Color(100, 200, 100)
                    : new Color(220, 80, 80));
            return this;
        }
    }
}