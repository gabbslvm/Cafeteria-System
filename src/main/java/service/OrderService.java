import database.OrderDAO;
import model.Order;
import java.util.Date;
import java.util.List;

public class OrderService {

    private OrderDAO orderDAO;

    public OrderService(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    public void placeOrder(Order order) {
        if (order.isPwdOrSenior()) {
            double discountAmount = order.getSubtotal() * 0.20;
            order.setDiscount(discountAmount);
            order.setFinalAmount(order.getSubtotal() - discountAmount);
        } else {
            order.setDiscount(0.0);
            order.setFinalAmount(order.getSubtotal());
        }

        String queueNumber = String.format("Q-%03d", (int)(Math.random() * 900) + 100);
        order.setQueueNumber(queueNumber);
        
        order.setStatus("Pending");

        orderDAO.saveOrder(order);
        System.out.println("Order processed successfully. Queue: " + order.getQueueNumber());
    }

    public boolean updateStatus(int orderId, String newStatus) {
        if (isValidStatus(newStatus)) {
            orderDAO.updateOrderStatus(orderId, newStatus);
            return true;
        }
        System.err.println("Error: Invalid status transition to '" + newStatus + "'");
        return false;
    }
