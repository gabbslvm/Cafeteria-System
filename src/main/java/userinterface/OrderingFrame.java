package userinterface;

import database.MenuItemDB;
import database.OrderDB;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import model.Staff;
import util.AppConstants;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderingFrame extends JFrame {

    // ── Theme ──
    private static final Color BG_DARK   = new Color(30, 30, 30);
    private static final Color BG_DARKER = new Color(20, 20, 20);
    private static final Color BG_PANEL  = new Color(40, 40, 40);
    private static final Color ACCENT    = new Color(245, 196, 0);
    private static final Color TEXT_MAIN = new Color(240, 240, 240);
    private static final Color TEXT_MUTED= new Color(150, 150, 150);
    private static final Color RED_BTN   = new Color(180, 60, 60);
    private static final Color GREEN_BTN = new Color(50, 160, 80);
    private static final double DISCOUNT  = 0.20;

    // ── State ──
    private final Staff currentStaff;
    private final List<MenuItem> allMenuItems = new ArrayList<>();
    private final List<OrderItem> cart        = new ArrayList<>();
    private String currentCategory = "All";
    private boolean isSeniorPwd    = false;
    private final String queueNumber;

    // ── UI refs ──
    private JPanel menuGridPanel;
    private JPanel categoryBar;
    private DefaultTableModel cartModel;
    private JLabel totalLabel, discountLabel, finalLabel, changeLabel;
    private JTextField amountPaidField;

    public OrderingFrame(Staff staff) {
        this.currentStaff = staff;
        this.queueNumber  = generateQueue();
        new MenuItemDB().getAvailableItems().forEach(allMenuItems::add);
        initUI();
    }

    private String generateQueue() {
        return "Q-" + String.format("%03d", (int)(Math.random() * 999) + 1);
    }

    // ══════════════════════════════════════════════
    //  ROOT LAYOUT
    // ══════════════════════════════════════════════
    private void initUI() {
        setTitle(AppConstants.APP_TITLE + " — New Order");
        setSize(1120, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.add(header(),  BorderLayout.NORTH);
        root.add(center(),  BorderLayout.CENTER);
        root.add(footer(),  BorderLayout.SOUTH);
        add(root);
    }

    // ══════════════════════════════════════════════
    //  HEADER
    // ══════════════════════════════════════════════
    private JPanel header() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_DARKER);
        p.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("🛒  New Order");
        title.setFont(new Font("Arial", Font.BOLD, 17));
        title.setForeground(ACCENT);

        JLabel sub = new JLabel("Queue: " + queueNumber
                + "   |   Cashier: " + currentStaff.getFullName());
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(TEXT_MUTED);

        p.add(title, BorderLayout.NORTH);
        p.add(sub,   BorderLayout.SOUTH);
        return p;
    }

    // ══════════════════════════════════════════════
    //  CENTER  (menu | cart)
    // ══════════════════════════════════════════════
    private JSplitPane center() {
        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                menuPanel(), cartPanel());
        sp.setDividerLocation(640);
        sp.setDividerSize(4);
        sp.setEnabled(false);
        sp.setBorder(null);
        return sp;
    }

    // ── LEFT: menu ──
    private JPanel menuPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 6));

        // Build category bar
        categoryBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        categoryBar.setBackground(BG_DARK);
        rebuildCategoryBar();

        menuGridPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        menuGridPanel.setBackground(BG_DARK);
        menuGridPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JScrollPane scroll = new JScrollPane(menuGridPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_DARK);

        p.add(categoryBar, BorderLayout.NORTH);
        p.add(scroll,      BorderLayout.CENTER);

        refreshGrid();
        return p;
    }

    private void rebuildCategoryBar() {
        categoryBar.removeAll();
        List<String> cats = new ArrayList<>();
        cats.add("All");
        for (MenuItem mi : allMenuItems)
            if (!cats.contains(mi.getCategory())) cats.add(mi.getCategory());

        for (String cat : cats) {
            JButton btn = new JButton(cat);
            btn.setFont(new Font("Arial", Font.BOLD, 12));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
            applyTabStyle(btn, cat.equals(currentCategory));
            btn.addActionListener(e -> {
                currentCategory = cat;
                for (Component c : categoryBar.getComponents())
                    if (c instanceof JButton) applyTabStyle((JButton) c, false);
                applyTabStyle(btn, true);
                refreshGrid();
            });
            categoryBar.add(btn);
        }
        categoryBar.revalidate();
        categoryBar.repaint();
    }

    private void applyTabStyle(JButton b, boolean active) {
        b.setBackground(active ? ACCENT : BG_PANEL);
        b.setForeground(active ? BG_DARKER : TEXT_MUTED);
    }

    private void refreshGrid() {
        menuGridPanel.removeAll();
        for (MenuItem mi : allMenuItems) {
            if (!currentCategory.equals("All") && !mi.getCategory().equals(currentCategory)) continue;
            menuGridPanel.add(menuCard(mi));
        }
        menuGridPanel.revalidate();
        menuGridPanel.repaint();
    }

    private JPanel menuCard(MenuItem mi) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 55, 55)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel name = new JLabel("<html><body style='width:120px'>" + mi.getName() + "</body></html>");
        name.setFont(new Font("Arial", Font.BOLD, 13));
        name.setForeground(TEXT_MAIN);

        JLabel cat = new JLabel(mi.getCategory());
        cat.setFont(new Font("Arial", Font.PLAIN, 11));
        cat.setForeground(TEXT_MUTED);

        JLabel price = new JLabel("₱ " + String.format("%.2f", mi.getPrice()));
        price.setFont(new Font("Arial", Font.BOLD, 14));
        price.setForeground(ACCENT);

        JPanel info = new JPanel(new GridLayout(3, 1, 2, 2));
        info.setOpaque(false);
        info.add(name); info.add(cat); info.add(price);

        JButton add = new JButton("+ Add");
        add.setBackground(ACCENT);
        add.setForeground(BG_DARKER);
        add.setFont(new Font("Arial", Font.BOLD, 12));
        add.setFocusPainted(false);
        add.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add.setBorder(BorderFactory.createEmptyBorder(7, 0, 7, 0));
        add.addActionListener(e -> addToCart(mi));

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { card.setBackground(new Color(50, 50, 50)); }
            public void mouseExited (MouseEvent e) { card.setBackground(BG_PANEL); }
        });

        card.add(info, BorderLayout.CENTER);
        card.add(add,  BorderLayout.SOUTH);
        return card;
    }

    // ── RIGHT: cart ──
    private JPanel cartPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_DARKER);
        p.setBorder(BorderFactory.createEmptyBorder(10, 6, 10, 10));

        JLabel title = new JLabel("  🧾  Order Cart");
        title.setFont(new Font("Arial", Font.BOLD, 15));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        p.add(title, BorderLayout.NORTH);

        // Table
        String[] cols = {"Item", "Price", "Qty", "Subtotal", ""};
        cartModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c == 2; }
        };

        JTable table = new JTable(cartModel);
        styleTable(table);

        // Remove column
        TableColumn removeCol = table.getColumn("");
        removeCol.setCellRenderer(new RemoveBtnRenderer());
        removeCol.setCellEditor(new RemoveBtnEditor());
        removeCol.setMaxWidth(50);
        removeCol.setMinWidth(50);

        // Qty edit → recalculate subtotal
        cartModel.addTableModelListener(e -> {
            if (e.getColumn() != 2) return;
            int row = e.getFirstRow();
            if (row < 0 || row >= cart.size()) return;
            try {
                int qty = Integer.parseInt(cartModel.getValueAt(row, 2).toString());
                if (qty <= 0) { SwingUtilities.invokeLater(() -> removeRow(row)); return; }
                cart.get(row).setQuantity(qty);
                cartModel.setValueAt("₱ " + String.format("%.2f", cart.get(row).getSubtotal()), row, 3);
            } catch (NumberFormatException ignored) {}
            refreshTotals();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(55, 55, 55)));
        scroll.getViewport().setBackground(BG_PANEL);
        p.add(scroll, BorderLayout.CENTER);
        p.add(paymentPanel(), BorderLayout.SOUTH);
        return p;
    }

    private void styleTable(JTable t) {
        t.setBackground(BG_PANEL);
        t.setForeground(TEXT_MAIN);
        t.setFont(new Font("Arial", Font.PLAIN, 13));
        t.setRowHeight(34);
        t.setGridColor(new Color(55, 55, 55));
        t.setSelectionBackground(new Color(60, 60, 60));
        t.getTableHeader().setBackground(BG_DARKER);
        t.getTableHeader().setForeground(TEXT_MUTED);
        t.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)));
    }

    // ── Payment panel ──
    private JPanel paymentPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_DARKER);
        p.setBorder(BorderFactory.createEmptyBorder(10, 4, 4, 4));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.fill   = GridBagConstraints.HORIZONTAL;

        // Senior/PWD checkbox
        JCheckBox pwd = new JCheckBox("Senior / PWD Discount  (20%)");
        pwd.setFont(new Font("Arial", Font.BOLD, 12));
        pwd.setForeground(TEXT_MAIN);
        pwd.setBackground(BG_DARKER);
        pwd.setFocusPainted(false);
        pwd.addActionListener(e -> { isSeniorPwd = pwd.isSelected(); refreshTotals(); });
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(pwd, g);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 60, 60));
        g.gridy = 1; p.add(sep, g);

        g.gridwidth = 1;

        // Total
        g.gridy = 2; g.gridx = 0; p.add(lbl("Total:",        Font.PLAIN), g);
        totalLabel = lbl("₱ 0.00", Font.BOLD);
        totalLabel.setForeground(TEXT_MAIN);
        g.gridx = 1; p.add(totalLabel, g);

        // Discount
        g.gridy = 3; g.gridx = 0; p.add(lbl("Discount:", Font.PLAIN), g);
        discountLabel = lbl("₱ 0.00", Font.BOLD);
        discountLabel.setForeground(new Color(100, 200, 100));
        g.gridx = 1; p.add(discountLabel, g);

        // Final
        g.gridy = 4; g.gridx = 0; p.add(lbl("Final Amount:", Font.BOLD), g);
        finalLabel = lbl("₱ 0.00", Font.BOLD);
        finalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        finalLabel.setForeground(ACCENT);
        g.gridx = 1; p.add(finalLabel, g);

        // Amount Paid
        g.gridy = 5; g.gridx = 0; p.add(lbl("Amount Paid:", Font.PLAIN), g);
        amountPaidField = new JTextField("0");
        amountPaidField.setBackground(BG_PANEL);
        amountPaidField.setForeground(TEXT_MAIN);
        amountPaidField.setFont(new Font("Arial", Font.BOLD, 14));
        amountPaidField.setCaretColor(Color.WHITE);
        amountPaidField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        amountPaidField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { refreshChange(); }
            public void removeUpdate(DocumentEvent e)  { refreshChange(); }
            public void changedUpdate(DocumentEvent e) { refreshChange(); }
        });
        g.gridx = 1; p.add(amountPaidField, g);

        // Change
        g.gridy = 6; g.gridx = 0; p.add(lbl("Change:", Font.PLAIN), g);
        changeLabel = lbl("₱ 0.00", Font.BOLD);
        changeLabel.setForeground(new Color(100, 200, 100));
        g.gridx = 1; p.add(changeLabel, g);

        // Place Order button
        JButton placeBtn = new JButton("✔  Place Order");
        placeBtn.setBackground(GREEN_BTN);
        placeBtn.setForeground(Color.WHITE);
        placeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        placeBtn.setFocusPainted(false);
        placeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        placeBtn.setBorder(BorderFactory.createEmptyBorder(11, 0, 11, 0));
        placeBtn.addActionListener(e -> placeOrder());
        g.gridy = 7; g.gridx = 0; g.gridwidth = 2;
        g.insets = new Insets(12, 6, 4, 6);
        p.add(placeBtn, g);

        return p;
    }

    // ══════════════════════════════════════════════
    //  FOOTER
    // ══════════════════════════════════════════════
    private JPanel footer() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 8));
        p.setBackground(BG_DARKER);

        JButton back = footerBtn("← Back", new Color(60, 60, 60));
        back.addActionListener(e -> {
            if (!cart.isEmpty()) {
                int c = JOptionPane.showConfirmDialog(this,
                        "Discard current order and go back?", "Discard Order",
                        JOptionPane.YES_NO_OPTION);
                if (c != JOptionPane.YES_OPTION) return;
            }
            dispose();
        });

        JButton clear = footerBtn("🗑  Clear Cart", RED_BTN);
        clear.addActionListener(e -> {
            cart.clear();
            cartModel.setRowCount(0);
            refreshTotals();
        });

        p.add(back);
        p.add(clear);
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

    // ══════════════════════════════════════════════
    //  CART LOGIC
    // ══════════════════════════════════════════════
    private void addToCart(MenuItem mi) {
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).getMenuItem().getMenuItemId() == mi.getMenuItemId()) {
                int qty = cart.get(i).getQuantity() + 1;
                cart.get(i).setQuantity(qty);
                cartModel.setValueAt(qty, i, 2);
                cartModel.setValueAt("₱ " + String.format("%.2f", cart.get(i).getSubtotal()), i, 3);
                refreshTotals();
                return;
            }
        }
        OrderItem oi = new OrderItem(mi, 1);
        cart.add(oi);
        cartModel.addRow(new Object[]{
                mi.getName(),
                "₱ " + String.format("%.2f", mi.getPrice()),
                1,
                "₱ " + String.format("%.2f", oi.getSubtotal()),
                "✕"
        });
        refreshTotals();
    }

    private void removeRow(int row) {
        if (row < 0 || row >= cart.size()) return;
        cart.remove(row);
        cartModel.removeRow(row);
        refreshTotals();
    }

    private void refreshTotals() {
        double total    = cart.stream().mapToDouble(OrderItem::getSubtotal).sum();
        double discount = isSeniorPwd ? total * DISCOUNT : 0;
        double finalAmt = total - discount;
        totalLabel.setText("₱ " + String.format("%.2f", total));
        discountLabel.setText("- ₱ " + String.format("%.2f", discount));
        finalLabel.setText("₱ " + String.format("%.2f", finalAmt));
        refreshChange();
    }

    private void refreshChange() {
        try {
            double paid     = Double.parseDouble(amountPaidField.getText().trim());
            double total    = cart.stream().mapToDouble(OrderItem::getSubtotal).sum();
            double finalAmt = total - (isSeniorPwd ? total * DISCOUNT : 0);
            double change   = paid - finalAmt;
            changeLabel.setText("₱ " + String.format("%.2f", Math.max(0, change)));
            changeLabel.setForeground(change < 0 ? new Color(220, 80, 80) : new Color(100, 200, 100));
        } catch (NumberFormatException ignored) {
            changeLabel.setText("₱ 0.00");
        }
    }

    // ══════════════════════════════════════════════
    //  PLACE ORDER
    // ══════════════════════════════════════════════
    private void placeOrder() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double paid;
        try {
            paid = Double.parseDouble(amountPaidField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid amount paid.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double total    = cart.stream().mapToDouble(OrderItem::getSubtotal).sum();
        double discount = isSeniorPwd ? total * DISCOUNT : 0;
        double finalAmt = total - discount;

        if (paid < finalAmt) {
            JOptionPane.showMessageDialog(this,
                    String.format("Insufficient payment.\nFinal Amount: ₱ %.2f\nAmount Paid:  ₱ %.2f",
                            finalAmt, paid),
                    "Insufficient Payment", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Build and save order
        Order order = new Order(currentStaff.getStaffId(), new ArrayList<>(cart));
        order.setQueueNumber(queueNumber);
        order.setTotalAmount(total);
        order.setDiscountAmount(discount);
        order.setFinalAmount(finalAmt);
        order.setAmountPaid(paid);
        order.setChange(paid - finalAmt);
        order.setStatus("Pending");
        order.setOrderDate(Timestamp.valueOf(LocalDateTime.now()));

        int orderId = new OrderDB().saveOrder(order);
        if (orderId == -1) {
            JOptionPane.showMessageDialog(this, "Failed to save order. Please try again.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        order.setOrderId(orderId);

        // Open Order List (cashier tracks / marks done / voids)
        new OrderList(currentStaff).setVisible(true);
        dispose();
    }

    // ══════════════════════════════════════════════
    //  UTILITIES
    // ══════════════════════════════════════════════
    private JLabel lbl(String text, int style) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", style, 13));
        l.setForeground(TEXT_MUTED);
        return l;
    }

    // ── Remove-button renderer ──
    private class RemoveBtnRenderer extends JButton implements TableCellRenderer {
        RemoveBtnRenderer() {
            setOpaque(true);
            styleRemoveBtn(this);
        }
        public Component getTableCellRendererComponent(
                JTable t, Object v, boolean sel, boolean foc, int row, int col) { return this; }
    }

    // ── Remove-button editor ──
    private class RemoveBtnEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton btn = new JButton("✕");
        private int editRow;
        RemoveBtnEditor() {
            styleRemoveBtn(btn);
            btn.addActionListener(e -> {
                fireEditingStopped();
                SwingUtilities.invokeLater(() -> removeRow(editRow));
            });
        }
        public Component getTableCellEditorComponent(
                JTable t, Object v, boolean sel, int row, int col) {
            editRow = row; return btn;
        }
        public Object getCellEditorValue() { return "✕"; }
    }

    private void styleRemoveBtn(JButton b) {
        b.setText("✕");
        b.setBackground(RED_BTN);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        b.setFocusPainted(false);
    }
}