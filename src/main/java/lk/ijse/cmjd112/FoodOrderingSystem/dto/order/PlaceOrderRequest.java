package lk.ijse.cmjd112.FoodOrderingSystem.dto.order;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for placing a new order request.
 * Contains delivery address for the customer's order.
 */
public record PlaceOrderRequest(
        @NotBlank String deliveryAddress
) {
}
