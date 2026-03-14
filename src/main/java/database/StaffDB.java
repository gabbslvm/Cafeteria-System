package database;

import model.Staff;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StaffDB {

    // Returns a Staff object if login is valid, or null if credentials are wrong
    public Staff authenticate(String username, String password) {
        String sql = "SELECT * FROM staff WHERE username = ? AND password = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password); // use hashed password later if needed

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
}