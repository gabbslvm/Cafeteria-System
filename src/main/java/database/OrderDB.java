package database;

import model.Order;
import model.OrderItem;
import model.MenuItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import util.DBConnection;

public class OrderDB {

    public int saveOrder(Order order) {
        String orderSql = "INSERT INTO orders (queue_number, staff_id, total_amount, discount_amount, final_amount, amount_paid, change_amount, status, order_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String orderItemSql = "INSERT INTO order_items (order_id, item_id, quantity, subtotal) VALUES (?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setString(1, order.getQueueNumber());
            orderStmt.setInt(2, order.getStaffId());
            orderStmt.setDouble(3, order.getTotalAmount());
            orderStmt.setDouble(4, order.getDiscountAmount());
            orderStmt.setDouble(5, order.getFinalAmount());
            orderStmt.setDouble(6, order.getAmountPaid());
            orderStmt.setDouble(7, order.getChange());
            orderStmt.setString(8, order.getStatus());
            orderStmt.setTimestamp(9, order.getOrderDate());
            orderStmt.executeUpdate();

            ResultSet generatedKeys = orderStmt.getGeneratedKeys();
            int orderId = 0;
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
                order.setOrderId(orderId);
            }

            PreparedStatement itemStmt = conn.prepareStatement(orderItemSql);
            for (OrderItem item : order.getOrderItems()) {
                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, item.getMenuItem().getMenuItemId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getSubtotal());
                itemStmt.executeUpdate();
            }

            System.out.println("Order saved successfully.");
            return orderId;

        } catch (SQLException e) {
            System.out.println("Error saving order: " + e.getMessage());
        }

        return -1;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, " +
                "(SELECT COUNT(*) FROM order_items oi WHERE oi.order_id = o.order_id) AS item_count " +
                "FROM orders o " +
                "ORDER BY o.order_date DESC";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = new Order(rs.getInt("staff_id"), new ArrayList<>());
                order.setOrderId(rs.getInt("order_id"));
                order.setQueueNumber(rs.getString("queue_number"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setDiscountAmount(rs.getDouble("discount_amount"));
                order.setFinalAmount(rs.getDouble("final_amount"));
                order.setAmountPaid(rs.getDouble("amount_paid"));
                order.setChange(rs.getDouble("change_amount"));
                order.setStatus(rs.getString("status"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                order.setItemCount(rs.getInt("item_count")); // ← NEW
                orders.add(order);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching orders: " + e.getMessage());
        }

        return orders;
    }

    public List<Order> getOrdersByDate(Date date) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, " +
                "(SELECT COUNT(*) FROM order_items oi WHERE oi.order_id = o.order_id) AS item_count " +
                "FROM orders o " +
                "WHERE DATE(o.order_date) = ? " +
                "ORDER BY o.order_date DESC";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDate(1, date);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = new Order(rs.getInt("staff_id"), new ArrayList<>());
                order.setOrderId(rs.getInt("order_id"));
                order.setQueueNumber(rs.getString("queue_number"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setDiscountAmount(rs.getDouble("discount_amount"));
                order.setFinalAmount(rs.getDouble("final_amount"));
                order.setAmountPaid(rs.getDouble("amount_paid"));
                order.setChange(rs.getDouble("change_amount"));
                order.setStatus(rs.getString("status"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                order.setItemCount(rs.getInt("item_count")); // ← NEW
                orders.add(order);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching orders by date: " + e.getMessage());
        }

        return orders;
    }

    public void updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
            System.out.println("Order status updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating order status: " + e.getMessage());
        }
    }

    public Order getOrderById(int orderId) {
        String orderSql = "SELECT * FROM orders WHERE order_id = ?";
        String itemsSql = "SELECT oi.*, mi.name, mi.category, mi.price, mi.is_available " +
                "FROM order_items oi " +
                "JOIN menu_items mi ON oi.item_id = mi.item_id " +
                "WHERE oi.order_id = ?";

        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement orderStmt = conn.prepareStatement(orderSql);
            orderStmt.setInt(1, orderId);
            ResultSet orderRs = orderStmt.executeQuery();

            if (orderRs.next()) {
                Order order = new Order(orderRs.getInt("staff_id"), new ArrayList<>());
                order.setOrderId(orderRs.getInt("order_id"));
                order.setQueueNumber(orderRs.getString("queue_number"));
                order.setTotalAmount(orderRs.getDouble("total_amount"));
                order.setDiscountAmount(orderRs.getDouble("discount_amount"));
                order.setFinalAmount(orderRs.getDouble("final_amount"));
                order.setAmountPaid(orderRs.getDouble("amount_paid"));
                order.setChange(orderRs.getDouble("change_amount"));
                order.setStatus(orderRs.getString("status"));
                order.setOrderDate(orderRs.getTimestamp("order_date"));

                PreparedStatement itemsStmt = conn.prepareStatement(itemsSql);
                itemsStmt.setInt(1, orderId);
                ResultSet itemsRs = itemsStmt.executeQuery();

                List<OrderItem> orderItems = new ArrayList<>();
                while (itemsRs.next()) {
                    MenuItem menuItem = new MenuItem(
                            itemsRs.getInt("item_id"),
                            itemsRs.getString("name"),
                            itemsRs.getString("category"),
                            itemsRs.getDouble("price"),
                            itemsRs.getBoolean("is_available"));
                    orderItems.add(new OrderItem(menuItem, itemsRs.getInt("quantity")));
                }

                order.setOrderItems(orderItems); // also populates size via getItemCount()
                return order;
            }

        } catch (SQLException e) {
            System.out.println("Error fetching order: " + e.getMessage());
        }

        return null;
    }

    public int getTodayOrderCount() {
        String sql = "SELECT COUNT(*) FROM orders WHERE DATE(order_date) = CURDATE()";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Error fetching today's order count: " + e.getMessage());
        }
        return 0;
    }
}