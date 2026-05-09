package lk.ijse.cmjd112.FoodOrderingSystem.dto.order;

import lk.ijse.cmjd112.FoodOrderingSystem.entity.OrderStatus;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.PaymentStatus;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for order information response.
 * Returns complete order details including items, payment status, and delivery info.
 */
public record OrderResponse(
        Long orderId,
        Long userId,
        String customerName,
        OrderStatus status,
        String deliveryAddress,
        Double totalAmount,
        LocalDateTime createdAt,
        List<OrderItemResponse> items,
        PaymentStatus paymentStatus
) {
}
