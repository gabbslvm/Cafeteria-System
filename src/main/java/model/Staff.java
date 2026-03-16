package model;

public class Staff {

    private int staffId;
    private String username;
    private String password;
    private String fullName;
    private String role;

    // Constructor
    public Staff(int staffId, String username, String password, String fullName, String role) {
        this.staffId = staffId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    // Getters
    public int getStaffId() {
        return staffId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    // Setters
    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public void setUsername(String u) {
        this.username = u;
    }

    public void setPassword(String p) {
        this.password = p;
    }

    public void setFullName(String name) {
        this.fullName = name;
    }

    public void setRole(String role) {
        this.role = role;
    }
}