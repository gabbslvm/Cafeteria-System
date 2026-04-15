package service;

public class PaymentService {

    private static final double STUDENT_DISCOUNT_RATE = 0.20; 
    public double applyStudentDiscount(double total) {
        double discountAmount = total * STUDENT_DISCOUNT_RATE;
        return total - discountAmount;
    }
    public boolean isPaymentSufficient(double paid, double total) {
        return paid >= total;
    }

    public double computeChange(double paid, double total) {
        if (isPaymentSufficient(paid, total)) {
            return paid - total;
        }
        return 0.0; 
    }
}
