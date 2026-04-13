package database;

import model.Staff;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDB {

    public Staff authenticate(String username, String password) {
        String sql = "SELECT * FROM staff WHERE username = ? AND password = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapStaff(rs);
        } catch (SQLException e) { System.out.println("Login error: " + e.getMessage()); }
        return null;
    }

    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff ORDER BY staff_id";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) staffList.add(mapStaff(rs));
        } catch (SQLException e) { System.out.println("Error fetching staff: " + e.getMessage()); }
        return staffList;
    }

    public void addStaff(Staff staff) {
        String sql = "INSERT INTO staff (username, password, full_name, role) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, staff.getUsername());
            stmt.setString(2, staff.getPassword());
            stmt.setString(3, staff.getFullName());
            stmt.setString(4, staff.getRole());
            stmt.executeUpdate();
        } catch (SQLException e) { System.out.println("Error adding staff: " + e.getMessage()); }
    }

    public void updateStaff(Staff staff) {
        String sql = "UPDATE staff SET username = ?, password = ?, full_name = ?, role = ? WHERE staff_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, staff.getUsername());
            stmt.setString(2, staff.getPassword());
            stmt.setString(3, staff.getFullName());
            stmt.setString(4, staff.getRole());
            stmt.setInt(5, staff.getStaffId());
            stmt.executeUpdate();
        } catch (SQLException e) { System.out.println("Error updating staff: " + e.getMessage()); }
    }

    public void deleteStaff(int staffId) {
        String sql = "DELETE FROM staff WHERE staff_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, staffId);
            stmt.executeUpdate();
        } catch (SQLException e) { System.out.println("Error deleting staff: " + e.getMessage()); }
    }

    public boolean usernameExists(String username, int excludeId) {
        String sql = "SELECT COUNT(*) FROM staff WHERE username = ? AND staff_id != ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setInt(2, excludeId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { System.out.println("Error checking username: " + e.getMessage()); }
        return false;
    }

    private Staff mapStaff(ResultSet rs) throws SQLException {
        return new Staff(
            rs.getInt("staff_id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("full_name"),
            rs.getString("role")
        );
    }
}