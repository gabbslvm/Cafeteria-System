package userinterface;

import database.OrderDB;
import model.Order;
import model.Staff;
import util.AppConstants;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class OrderList extends JFrame {

    // ── Theme ──
    private static final Color BG_DARK    = new Color(30, 30, 30);
    private static final Color BG_DARKER  = new Color(20, 20, 20);
    private static final Color BG_PANEL   = new Color(40, 40, 40);
    private static final Color ACCENT     = new Color(245, 196, 0);
    private static final Color TEXT_MAIN  = new Color(240, 240, 240);
    private static final Color TEXT_MUTED = new Color(150, 150, 150);
    private static final Color RED_BTN    = new Color(180, 60, 60);
    private static final Color GREEN_BTN  = new Color(50, 160, 80);
    private static final Color ORANGE     = new Color(230, 140, 30);
    private static final Color BLUE       = new Color(70, 130, 200);
    private static final Color TEAL       = new Color(30, 180, 160);

    // ── Status Constants ──
    private static final String STATUS_PENDING         = "Pending";
    private static final String STATUS_PREPARING       = "Preparing";
    private static final String STATUS_READY_FOR_PICKUP = "Ready for Pickup";
    private static final String STATUS_VOIDED          = "Voided";

    private final Staff currentStaff;
    private DefaultTableModel tableModel;
    private JTable orderTable;
    private List<Order> orders;
    private final OrderDB orderDB = new OrderDB();

    public OrderList(Staff staff) {
        this.currentStaff = staff;
        initUI();
        loadOrders();
    }

    private void initUI() {
        setTitle(AppConstants.APP_TITLE + " — Order List");
        setSize(980, 580);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.add(header(),  BorderLayout.NORTH);
        root.add(content(), BorderLayout.CENTER);
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

        JLabel title = new JLabel("📋  Order List");
        title.setFont(new Font("Arial", Font.BOLD, 17));
        title.setForeground(ACCENT);

        JLabel sub = new JLabel("Cashier: " + currentStaff.getFullName()
                + "  |  Advance orders: Pending → Preparing → Ready for Pickup. Void to cancel.");
        sub.setFont(new Font("Arial", Font.PLAIN, 11));
        sub.setForeground(TEXT_MUTED);

        p.add(title, BorderLayout.NORTH);
        p.add(sub,   BorderLayout.SOUTH);
        return p;
    }

    // ══════════════════════════════════════════════
    //  CONTENT — orders table
    // ══════════════════════════════════════════════
    private JPanel content() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));

        String[] cols = {"Order ID", "Queue", "Items", "Total", "Discount", "Final", "Status", "Advance", "Void"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c == 7 || c == 8; }
        };

        orderTable = new JTable(tableModel);
        styleTable(orderTable);

        // "Advance" button column (replaces old "Done")
        orderTable.getColumn("Advance").setCellRenderer(new AdvanceBtnRenderer());
        orderTable.getColumn("Advance").setCellEditor(new AdvanceBtnEditor());
        orderTable.getColumn("Advance").setMaxWidth(130); orderTable.getColumn("Advance").setMinWidth(130);

        // "Void" button column
        orderTable.getColumn("Void").setCellRenderer(new ActionBtnRenderer("✕ Void", RED_BTN));
        orderTable.getColumn("Void").setCellEditor(new ActionBtnEditor("✕ Void", RED_BTN, "void"));
        orderTable.getColumn("Void").setMaxWidth(90); orderTable.getColumn("Void").setMinWidth(90);

        // Status column with color rendering
        orderTable.getColumn("Status").setCellRenderer(new StatusRenderer());

        JScrollPane scroll = new JScrollPane(orderTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(55, 55, 55)));
        scroll.getViewport().setBackground(BG_PANEL);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private void styleTable(JTable t) {
        t.setBackground(BG_PANEL);
        t.setForeground(TEXT_MAIN);
        t.setFont(new Font("Arial", Font.PLAIN, 13));
        t.setRowHeight(36);
        t.setGridColor(new Color(55, 55, 55));
        t.setSelectionBackground(new Color(60, 60, 60));
        t.getTableHeader().setBackground(BG_DARKER);
        t.getTableHeader().setForeground(TEXT_MUTED);
        t.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    // ══════════════════════════════════════════════
    //  FOOTER
    // ══════════════════════════════════════════════
    private JPanel footer() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 8));
        p.setBackground(BG_DARKER);

        // ── Status legend ──
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        legend.setBackground(BG_DARKER);
        legend.add(legendDot(ORANGE,     "Pending"));
        legend.add(legendDot(BLUE,       "Preparing"));
        legend.add(legendDot(TEAL,       "Ready for Pickup"));
        legend.add(legendDot(RED_BTN,    "Voided"));

        JButton back = btn("← Back to Menu", new Color(60, 60, 60));
        back.addActionListener(e -> dispose());

        JButton newOrder = btn("🛒  New Order", ACCENT);
        newOrder.setForeground(BG_DARKER);
        newOrder.addActionListener(e -> {
            new OrderingFrame(currentStaff).setVisible(true);
            dispose();
        });

        JButton refresh = btn("⟳  Refresh", new Color(60, 80, 120));
        refresh.addActionListener(e -> loadOrders());

        p.add(back);
        p.add(newOrder);
        p.add(refresh);
        p.add(Box.createHorizontalStrut(20));
        p.add(legend);
        return p;
    }

    /** Small colored dot + label for the legend. */
    private JPanel legendDot(Color color, String label) {
        JPanel dot = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(color);
                g.fillOval(0, 2, 10, 10);
            }
        };
        dot.setPreferredSize(new Dimension(10, 14));
        dot.setBackground(BG_DARKER);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl.setForeground(color);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        wrapper.setBackground(BG_DARKER);
        wrapper.add(dot);
        wrapper.add(lbl);
        return wrapper;
    }

    private JButton btn(String text, Color bg) {
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
    //  DATA
    // ══════════════════════════════════════════════
    private void loadOrders() {
        orders = orderDB.getAllOrders();
        tableModel.setRowCount(0);
        for (Order o : orders) {
            tableModel.addRow(new Object[]{
                    o.getOrderId(),
                    o.getQueueNumber(),
                    o.getOrderItems() != null ? o.getOrderItems().size() + " item(s)" : "—",
                    "₱ " + String.format("%.2f", o.getTotalAmount()),
                    "₱ " + String.format("%.2f", o.getDiscountAmount()),
                    "₱ " + String.format("%.2f", o.getFinalAmount()),
                    o.getStatus(),
                    advanceBtnLabel(o.getStatus()),   // column 7
                    "✕ Void"                          // column 8
            });
        }
    }

    /**
     * Returns the appropriate label for the Advance button
     * based on the order's current status.
     */
    private String advanceBtnLabel(String status) {
        if (status == null) return "▶ Prepare";
        switch (status) {
            case STATUS_PENDING:          return "▶ Prepare";
            case STATUS_PREPARING:        return "✔ Ready";
            case STATUS_READY_FOR_PICKUP: return "🧾 Receipt";
            default:                      return "—";
        }
    }

    /**
     * Returns the color for the Advance button based on the order's current status.
     */
    private Color advanceBtnColor(String status) {
        if (status == null) return BLUE;
        switch (status) {
            case STATUS_PENDING:          return BLUE;
            case STATUS_PREPARING:        return TEAL;
            case STATUS_READY_FOR_PICKUP: return GREEN_BTN;
            default:                      return new Color(80, 80, 80);
        }
    }

    // ══════════════════════════════════════════════
    //  ACTION HANDLER
    // ══════════════════════════════════════════════
    private void handleAction(int row, String action) {
        if (row < 0 || row >= orders.size()) return;
        Order order = orders.get(row);

        if ("advance".equals(action)) {
            advanceOrder(row, order);
        } else if ("void".equals(action)) {
            voidOrder(row, order);
        }
    }

    private void advanceOrder(int row, Order order) {
        String status = order.getStatus();

        if (STATUS_VOIDED.equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this,
                    "This order has been voided and cannot be advanced.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (STATUS_PENDING.equalsIgnoreCase(status)) {
            // Pending → Preparing
            orderDB.updateOrderStatus(order.getOrderId(), STATUS_PREPARING);
            order.setStatus(STATUS_PREPARING);
            tableModel.setValueAt(STATUS_PREPARING, row, 6);
            tableModel.setValueAt(advanceBtnLabel(STATUS_PREPARING), row, 7);
            JOptionPane.showMessageDialog(this,
                    "Order #" + order.getOrderId() + " (" + order.getQueueNumber()
                            + ") is now being Prepared.",
                    "Status Updated", JOptionPane.INFORMATION_MESSAGE);

        } else if (STATUS_PREPARING.equalsIgnoreCase(status)) {
            // Preparing → Ready for Pickup
            orderDB.updateOrderStatus(order.getOrderId(), STATUS_READY_FOR_PICKUP);
            order.setStatus(STATUS_READY_FOR_PICKUP);
            tableModel.setValueAt(STATUS_READY_FOR_PICKUP, row, 6);
            tableModel.setValueAt(advanceBtnLabel(STATUS_READY_FOR_PICKUP), row, 7);
            JOptionPane.showMessageDialog(this,
                    "Order #" + order.getOrderId() + " (" + order.getQueueNumber()
                            + ") is Ready for Pickup! 🎉",
                    "Ready for Pickup", JOptionPane.INFORMATION_MESSAGE);

        } else if (STATUS_READY_FOR_PICKUP.equalsIgnoreCase(status)) {
            // Already ready — just re-show receipt
            Order full = orderDB.getOrderById(order.getOrderId());
            showReceipt(full != null ? full : order);
        }
    }

    private void voidOrder(int row, Order order) {
        if (STATUS_VOIDED.equalsIgnoreCase(order.getStatus())) {
            JOptionPane.showMessageDialog(this, "This order is already voided.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Void Order #" + order.getOrderId() + " (" + order.getQueueNumber() + ")?\n"
                        + "This cannot be undone.",
                "Confirm Void", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            orderDB.updateOrderStatus(order.getOrderId(), STATUS_VOIDED);
            order.setStatus(STATUS_VOIDED);
            tableModel.setValueAt(STATUS_VOIDED, row, 6);
            tableModel.setValueAt("—", row, 7);
            JOptionPane.showMessageDialog(this,
                    "Order #" + order.getOrderId() + " has been voided.",
                    "Voided", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showReceipt(Order order) {
        ReceiptFrame receipt = new ReceiptFrame(this, order, currentStaff);
        receipt.setVisible(true);
        if (receipt.isGoToMainMenu()) {
            new MainMenuFrame(currentStaff).setVisible(true);
            dispose();
        }
    }

    // ══════════════════════════════════════════════
    //  RENDERERS & EDITORS
    // ══════════════════════════════════════════════

    /** Generic static-label button renderer (used for Void). */
    private static class ActionBtnRenderer extends JButton implements TableCellRenderer {
        ActionBtnRenderer(String text, Color bg) {
            setOpaque(true); setText(text);
            setBackground(bg); setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 11));
            setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            setFocusPainted(false);
        }
        public Component getTableCellRendererComponent(
                JTable t, Object v, boolean sel, boolean foc, int r, int c) { return this; }
    }

    /**
     * Advance button renderer — label AND color change based on the
     * Status column (col 6) of the same row.
     */
    private class AdvanceBtnRenderer extends JButton implements TableCellRenderer {
        AdvanceBtnRenderer() {
            setOpaque(true);
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 11));
            setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            setFocusPainted(false);
        }
        public Component getTableCellRendererComponent(
                JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            String status = t.getValueAt(r, 6) != null ? t.getValueAt(r, 6).toString() : "";
            setText(advanceBtnLabel(status));
            setBackground(advanceBtnColor(status));
            // Disable visually when voided
            if (STATUS_VOIDED.equalsIgnoreCase(status)) {
                setBackground(new Color(70, 70, 70));
                setForeground(TEXT_MUTED);
            } else {
                setForeground(Color.WHITE);
            }
            return this;
        }
    }

    /** Advance button editor — fires the "advance" action. */
    private class AdvanceBtnEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton btn = new JButton();
        private int editRow;

        AdvanceBtnEditor() {
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 11));
            btn.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            btn.setFocusPainted(false);
            btn.addActionListener(e -> {
                fireEditingStopped();
                SwingUtilities.invokeLater(() -> handleAction(editRow, "advance"));
            });
        }

        public Component getTableCellEditorComponent(
                JTable t, Object v, boolean sel, int row, int col) {
            editRow = row;
            String status = t.getValueAt(row, 6) != null ? t.getValueAt(row, 6).toString() : "";
            btn.setText(advanceBtnLabel(status));
            btn.setBackground(advanceBtnColor(status));
            if (STATUS_VOIDED.equalsIgnoreCase(status)) {
                btn.setBackground(new Color(70, 70, 70));
                btn.setForeground(TEXT_MUTED);
            } else {
                btn.setForeground(Color.WHITE);
            }
            return btn;
        }

        public Object getCellEditorValue() { return btn.getText(); }
    }

    /** Generic button editor (used for Void). */
    private class ActionBtnEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton btn;
        private int editRow;
        ActionBtnEditor(String text, Color bg, String actionKey) {
            btn = new JButton(text);
            btn.setBackground(bg); btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 11));
            btn.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            btn.setFocusPainted(false);
            btn.addActionListener(e -> {
                fireEditingStopped();
                SwingUtilities.invokeLater(() -> handleAction(editRow, actionKey));
            });
        }
        public Component getTableCellEditorComponent(
                JTable t, Object v, boolean sel, int row, int col) { editRow = row; return btn; }
        public Object getCellEditorValue() { return btn.getText(); }
    }

    /** Status column renderer — color-codes each status. */
    private class StatusRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(
                JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            setBackground(BG_PANEL);
            setHorizontalAlignment(CENTER);
            String status = v != null ? v.toString() : "";
            switch (status) {
                case STATUS_PENDING          -> setForeground(ORANGE);
                case STATUS_PREPARING        -> setForeground(BLUE);
                case STATUS_READY_FOR_PICKUP -> setForeground(TEAL);
                case STATUS_VOIDED           -> setForeground(RED_BTN);
                default                      -> setForeground(TEXT_MAIN);
            }
            return this;
        }
    }
}