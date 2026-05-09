package lk.ijse.cmjd112.FoodOrderingSystem.dto.order;

import lk.ijse.cmjd112.FoodOrderingSystem.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for updating order status request.
 * Contains the new order status for admin status updates.
 */
public record OrderStatusUpdateRequest(
        @NotNull OrderStatus status
) {
}
