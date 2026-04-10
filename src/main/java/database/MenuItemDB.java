package database;

import model.MenuItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.DBConnection;

public class MenuItemDB {
    public List<MenuItem> getAllItems() {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM menu_items";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MenuItem item = new MenuItem(
                        rs.getInt("item_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getBoolean("is_available"),
                        rs.getInt("stock"));
                items.add(item);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching items: " + e.getMessage());
        }

        return items;
    }

    public List<MenuItem> getAvailableItems() {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM menu_items WHERE is_available = 1";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MenuItem item = new MenuItem(
                        rs.getInt("item_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getBoolean("is_available"),
                        rs.getInt("stock"));
                items.add(item);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching available items: " + e.getMessage());
        }

        return items;
    }

    public void addItem(MenuItem item) {
        String sql = "INSERT INTO menu_items (name, category, price, is_available, stock) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, item.getName());
            stmt.setString(2, item.getCategory());
            stmt.setDouble(3, item.getPrice());
            stmt.setBoolean(4, item.isAvailable());
            stmt.setInt(5, item.getStock());
            stmt.executeUpdate();
            System.out.println("Menu item added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding item: " + e.getMessage());
        }
    }

    public void updateItem(MenuItem item) {
        String sql = "UPDATE menu_items SET name = ?, category = ?, price = ?, is_available = ?, stock = ? WHERE item_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, item.getName());
            stmt.setString(2, item.getCategory());
            stmt.setDouble(3, item.getPrice());
            stmt.setBoolean(4, item.isAvailable());
            stmt.setInt(5, item.getStock());
            stmt.setInt(6, item.getMenuItemId());
            stmt.executeUpdate();
            System.out.println("Menu item updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating item: " + e.getMessage());
        }
    }

    public void deleteItem(int itemId) {
        String sql = "DELETE FROM menu_items WHERE item_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, itemId);

            stmt.executeUpdate();
            System.out.println("Menu item deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting item: " + e.getMessage());
        }
    }

    public void deductStock(int itemId, int quantity) {
        String sql = "UPDATE menu_items SET stock = stock - ? WHERE item_id = ? AND stock >= ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, quantity);
            stmt.setInt(2, itemId);
            stmt.setInt(3, quantity);
            stmt.executeUpdate();
            System.out.println("Stock deducted for item ID: " + itemId);
        } catch (SQLException e) {
            System.out.println("Error deducting stock: " + e.getMessage());
        }
    }
}