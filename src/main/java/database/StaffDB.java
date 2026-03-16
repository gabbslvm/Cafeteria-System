package database;

import model.Staff;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffDB {

    // Returns a Staff object if login is valid, or null if credentials are wrong
    public Staff authenticate(String username, String password) {
        String sql = "SELECT * FROM staff WHERE username = ? AND password = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Login success — build and return a Staff object
                return new Staff(
                    rs.getInt("staff_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("role")
                );
            }

        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }

        return null; // Login failed
    }

    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Staff staff = new Staff(
                    rs.getInt("staff_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("role")
                );
                staffList.add(staff);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching staff: " + e.getMessage());
        }

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
        } catch (SQLException e) {
            System.out.println("Error adding staff: " + e.getMessage());
        }
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
            System.out.println("Staff updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating staff: " + e.getMessage());
        }
    }
}