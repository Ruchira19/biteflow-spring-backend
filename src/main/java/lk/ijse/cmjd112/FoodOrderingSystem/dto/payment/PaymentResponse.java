package lk.ijse.cmjd112.FoodOrderingSystem.dto.payment;

import lk.ijse.cmjd112.FoodOrderingSystem.entity.PaymentStatus;
import java.time.LocalDateTime;

/**
 * DTO for payment information response.
 * Returns payment details including status, amount, and transaction reference.
 */
public record PaymentResponse(
        Long paymentId,
        Long orderId,
        Double amount,
        PaymentStatus status,
        LocalDateTime paidAt,
        String transactionReference
) {
}
