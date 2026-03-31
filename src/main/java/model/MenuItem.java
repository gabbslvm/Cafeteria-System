package model;

public class MenuItem {

    private int menuItemId;
    private String name;
    private double price;
    private String category;
    private boolean isAvailable;
    private int stock;

    // Constructor
    public MenuItem(int menuItemId, String name, String category, double price, boolean isAvailable, int stock) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.isAvailable = isAvailable;
        this.stock = stock;
    }

    // Getters
    public int getMenuItemId() {
        return menuItemId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public int getStock() {
        return stock;
    }

    // Setters
    public void setMenuItemId(int id) {
        this.menuItemId = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String toString() {
        return String.format("%s (%.2f) - %s", name, price, isAvailable ? "Available" : "Unavailable");
    }
}