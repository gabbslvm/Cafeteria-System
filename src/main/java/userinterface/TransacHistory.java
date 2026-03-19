package userinterface;

import database.OrderDB;
import model.Order;
import model.Staff;
import util.AppConstants;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class TransacHistory extends JFrame {

    private static final Color BG_DARK   = new Color(30, 30, 30);
    private static final Color BG_DARKER = new Color(20, 20, 20);
    private static final Color BG_PANEL  = new Color(40, 40, 40);
    private static final Color ACCENT    = new Color(245, 196, 0);
    private static final Color TEXT_MAIN = new Color(240, 240, 240);
    private static final Color TEXT_MUTED= new Color(150, 150, 150);
    private static final Color RED_BTN   = new Color(180, 60, 60);
    private static final Color ORANGE    = new Color(230, 140, 30);

    private final Staff currentStaff;
    private DefaultTableModel tableModel;
    private final OrderDB orderDB = new OrderDB();

    public TransacHistory(Staff staff) {
        this.currentStaff = staff;
        initUI();
        loadAll();
    }

    private void initUI() {
        setTitle(AppConstants.APP_TITLE + " — Transaction History");
        setSize(960, 580);
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

    // ── Header ──
    private JPanel header() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_DARKER);
        p.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("📊  Transaction History");
        title.setFont(new Font("Arial", Font.BOLD, 17));
        title.setForeground(ACCENT);

        // Date filter row
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterRow.setBackground(BG_DARKER);

        JLabel filterLbl = new JLabel("Filter by date:");
        filterLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        filterLbl.setForeground(TEXT_MUTED);

        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setPreferredSize(new Dimension(130, 28));
        dateSpinner.setBackground(BG_PANEL);
        dateSpinner.setForeground(TEXT_MAIN);

        JButton filterBtn = new JButton("Filter");
        filterBtn.setBackground(ACCENT);
        filterBtn.setForeground(BG_DARKER);
        filterBtn.setFont(new Font("Arial", Font.BOLD, 12));
        filterBtn.setFocusPainted(false);
        filterBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        filterBtn.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        filterBtn.addActionListener(e -> {
            java.util.Date selected = (java.util.Date) dateSpinner.getValue();
            loadByDate(new Date(selected.getTime()));
        });

        JButton allBtn = new JButton("Show All");
        allBtn.setBackground(new Color(60, 60, 60));
        allBtn.setForeground(Color.WHITE);
        allBtn.setFont(new Font("Arial", Font.BOLD, 12));
        allBtn.setFocusPainted(false);
        allBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        allBtn.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        allBtn.addActionListener(e -> loadAll());

        filterRow.add(filterLbl);
        filterRow.add(dateSpinner);
        filterRow.add(filterBtn);
        filterRow.add(allBtn);

        p.add(title,     BorderLayout.NORTH);
        p.add(filterRow, BorderLayout.SOUTH);
        return p;
    }

    // ── Content ──
    private JPanel content() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));

        String[] cols = {"Order ID", "Queue", "Date", "Total", "Discount", "Final", "Paid", "Change", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        styleTable(table);
        table.getColumn("Status").setCellRenderer(new StatusRenderer());

        JScrollPane scroll = new JScrollPane(table);
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
        t.getTableHeader().setBackground(BG_DARKER);
        t.getTableHeader().setForeground(TEXT_MUTED);
        t.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
    }

    // ── Footer ──
    private JPanel footer() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 8));
        p.setBackground(BG_DARKER);

        JButton back = new JButton("← Back to Menu");
        back.setBackground(new Color(60, 60, 60));
        back.setForeground(Color.WHITE);
        back.setFont(new Font("Arial", Font.BOLD, 12));
        back.setFocusPainted(false);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        back.addActionListener(e -> dispose());
        p.add(back);
        return p;
    }

    // ── Data loading ──
    private void loadAll() {
        populate(orderDB.getAllOrders());
    }

    private void loadByDate(Date date) {
        populate(orderDB.getOrdersByDate(date));
    }

    private void populate(List<Order> list) {
        tableModel.setRowCount(0);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm");
        for (Order o : list) {
            tableModel.addRow(new Object[]{
                    o.getOrderId(),
                    o.getQueueNumber(),
                    o.getOrderDate() != null ? sdf.format(o.getOrderDate()) : "—",
                    "₱ " + String.format("%.2f", o.getTotalAmount()),
                    "₱ " + String.format("%.2f", o.getDiscountAmount()),
                    "₱ " + String.format("%.2f", o.getFinalAmount()),
                    "₱ " + String.format("%.2f", o.getAmountPaid()),
                    "₱ " + String.format("%.2f", o.getChange()),
                    o.getStatus()
            });
        }
    }

    // ── Status color renderer ──
    private class StatusRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(
                JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            setBackground(BG_PANEL);
            setHorizontalAlignment(CENTER);
            String s = v != null ? v.toString() : "";
            switch (s.toLowerCase()) {
                case "done"    -> setForeground(new Color(100, 200, 100));
                case "voided"  -> setForeground(RED_BTN);
                case "pending" -> setForeground(ORANGE);
                default        -> setForeground(TEXT_MAIN);
            }
            return this;
        }
    }
}