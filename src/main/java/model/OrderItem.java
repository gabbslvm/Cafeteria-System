package model;

public class OrderItem {

    private MenuItem menuItem;
    private int quantity;
    private double subtotal;

    // Constructor
    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.subtotal = computeSubtotal();
    }

    // Compute subtotal (price × quantity)
    public double computeSubtotal() {
        this.subtotal = menuItem.getPrice() * quantity;
        return this.subtotal;
    }

    // Getters
    public MenuItem getMenuItem() {
        return menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    // Setters
    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
        computeSubtotal();
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        computeSubtotal();
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return String.format("%s x%d - ₱%.2f", menuItem.getName(), quantity, subtotal);
    }
}