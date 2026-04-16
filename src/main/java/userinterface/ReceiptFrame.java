package userinterface;

import model.Order;
import model.OrderItem;
import model.Staff;
import util.AppConstants;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class ReceiptFrame extends JDialog {

    private static final Color BG = new Color(252, 252, 248);
    private static final Color DIVIDER = new Color(200, 200, 200);
    private static final Color DARK = new Color(30, 30, 30);
    private static final Color MUTED = new Color(100, 100, 100);

    private boolean goToMainMenu = false;
    private final Order order;
    private final Staff staff;

    public ReceiptFrame(JFrame parent, Order order, Staff staff) {
        super(parent, "Receipt — " + order.getQueueNumber(), true);
        this.order = order;
        this.staff = staff;
        initUI();
    }

    public boolean isGoToMainMenu() {
        return goToMainMenu;
    }

    private void initUI() {
        setMinimumSize(new Dimension(420, 620));
setLocationRelativeTo(getParent());
setResizable(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(buildReceipt(), BorderLayout.CENTER);
        root.add(buildButtons(), BorderLayout.SOUTH);
        add(root);
    }

    private JPanel buildReceipt() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(20, 28, 10, 28));

        JLabel store = new JLabel(AppConstants.APP_TITLE, SwingConstants.CENTER);
        store.setFont(new Font("Courier New", Font.BOLD, 18));
        store.setForeground(DARK);
        store.setAlignmentX(Component.CENTER_ALIGNMENT);
        store.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel tagline = new JLabel("Official Receipt", SwingConstants.CENTER);
        tagline.setFont(new Font("Courier New", Font.PLAIN, 12));
        tagline.setForeground(MUTED);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        tagline.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        p.add(store);
        p.add(Box.createVerticalStrut(4));
        p.add(tagline);
        p.add(Box.createVerticalStrut(10));
        p.add(divider());

        String dateStr = order.getOrderDate() != null
                ? new SimpleDateFormat("MMM dd, yyyy  hh:mm a").format(order.getOrderDate())
                : "—";
        p.add(receiptRow("Date", dateStr));
        String qNum = order.getQueueNumber();
        String queueDisp;
        String orderIdDisp;
        if (qNum != null && qNum.length() >= 8 && Character.isDigit(qNum.charAt(0))) {
            queueDisp = "Q-" + qNum.substring(5);
            orderIdDisp = qNum;
        } else {
            queueDisp = qNum;
            orderIdDisp = String.valueOf(order.getOrderId());
        }
        p.add(receiptRow("Queue #", queueDisp));
        p.add(receiptRow("Order ID", orderIdDisp));
        p.add(receiptRow("Cashier", staff.getFullName()));
        p.add(Box.createVerticalStrut(6));
        p.add(divider());

        JLabel itemsHeader = new JLabel("  ITEMS");
        itemsHeader.setFont(new Font("Courier New", Font.BOLD, 12));
        itemsHeader.setForeground(MUTED);
        itemsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(itemsHeader);
        p.add(Box.createVerticalStrut(4));

        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            for (OrderItem oi : order.getOrderItems()) {
                String itemLine = String.format("%-18s x%d",
                        truncate(oi.getMenuItem().getName(), 18), oi.getQuantity());
                String subLine = "₱ " + String.format("%.2f", oi.getSubtotal());
                p.add(receiptItemRow(itemLine, subLine));
            }
        } else {
            JLabel none = new JLabel("  (No item details)");
            none.setFont(new Font("Courier New", Font.ITALIC, 11));
            none.setForeground(MUTED);
            none.setAlignmentX(Component.LEFT_ALIGNMENT);
            p.add(none);
        }

        p.add(Box.createVerticalStrut(6));
        p.add(divider());

        p.add(receiptRow("Subtotal", "₱ " + String.format("%.2f", order.getTotalAmount())));
        if (order.getDiscountAmount() > 0) {
            p.add(receiptRow("Discount (20%)", "- ₱ " + String.format("%.2f", order.getDiscountAmount())));
        }
        JPanel finalRow = receiptRow("TOTAL", "₱ " + String.format("%.2f", order.getFinalAmount()));
        for (Component c : finalRow.getComponents()) {
            if (c instanceof JLabel) {
                c.setFont(new Font("Courier New", Font.BOLD, 14));
                ((JLabel) c).setForeground(DARK);
            }
        }
        p.add(finalRow);
        p.add(Box.createVerticalStrut(4));
        p.add(receiptRow("Amount Paid", "₱ " + String.format("%.2f", order.getAmountPaid())));
        p.add(receiptRow("Change", "₱ " + String.format("%.2f", order.getChange())));
        p.add(Box.createVerticalStrut(8));
        p.add(divider());

        JLabel thanks = new JLabel("Thank you for your purchase!", SwingConstants.CENTER);
        thanks.setFont(new Font("Courier New", Font.ITALIC, 12));
        thanks.setForeground(MUTED);
        thanks.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        thanks.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(Box.createVerticalStrut(8));
        p.add(thanks);

        JScrollPane scroll = new JScrollPane(p);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER));
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 12));
        p.setBackground(new Color(245, 245, 240));

        JButton mainMenuBtn = new JButton("Back to Main Menu");
        mainMenuBtn.setBackground(new Color(245, 196, 0));
        mainMenuBtn.setForeground(DARK);
        mainMenuBtn.setFont(new Font("Arial", Font.BOLD, 13));
        mainMenuBtn.setFocusPainted(false);
        mainMenuBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mainMenuBtn.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        mainMenuBtn.addActionListener(e -> {
            goToMainMenu = true;
            dispose();
        });

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(100, 100, 100));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFont(new Font("Arial", Font.BOLD, 13));
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        closeBtn.addActionListener(e -> dispose());

        p.add(mainMenuBtn);
        p.add(closeBtn);
        return p;
    }

    private JPanel receiptRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(BG);
        row.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        JLabel l = new JLabel(label);
        l.setFont(new Font("Courier New", Font.PLAIN, 12));
        l.setForeground(MUTED);

        JLabel v = new JLabel(value, SwingConstants.RIGHT);
        v.setFont(new Font("Courier New", Font.PLAIN, 12));
        v.setForeground(DARK);

        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        return row;
    }

    private JPanel receiptItemRow(String left, String right) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(BG);
        row.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        JLabel l = new JLabel(left);
        l.setFont(new Font("Courier New", Font.PLAIN, 11));
        l.setForeground(DARK);

        JLabel r = new JLabel(right, SwingConstants.RIGHT);
        r.setFont(new Font("Courier New", Font.PLAIN, 11));
        r.setForeground(DARK);

        row.add(l, BorderLayout.WEST);
        row.add(r, BorderLayout.EAST);
        return row;
    }

    private JSeparator divider() {
        JSeparator s = new JSeparator();
        s.setForeground(DIVIDER);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }
}