package model;

import java.util.List;
import java.sql.Timestamp;

public class Order {
    private int orderId;
    private String queueNumber;
    private List<OrderItem> orderItems;
    private double totalAmount;
    private double discountAmount;
    private double finalAmount;
    private double amountPaid;
    private double change;
    private String status;
    private int staffId;
    private Timestamp orderDate;

    // Constructor
    public Order(int staffId, List<OrderItem> orderItems) {
        this.staffId = staffId;
        this.orderItems = orderItems;
        this.status = "Pending";
        this.orderDate = new Timestamp(System.currentTimeMillis());
    }

    public void computeTotal() {
        this.totalAmount = 0;
        for (OrderItem item : orderItems) {
            this.totalAmount += item.getSubtotal();
        }
        this.finalAmount = this.totalAmount - this.discountAmount;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getQueueNumber() {
        return queueNumber;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public double getChange() {
        return change;
    }

    public String getStatus() {
        return status;
    }

    public int getStaffId() {
        return staffId;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setQueueNumber(String queueNumber) {
        this.queueNumber = queueNumber;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        computeTotal();
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
        computeTotal();
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
        this.change = this.amountPaid - this.finalAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public String toString() {
        return String.format("Order #%d | Queue: %s | Total: %.2f | Status: %s",
                orderId, queueNumber, totalAmount, status);
    }
}
