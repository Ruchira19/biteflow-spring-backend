package lk.ijse.cmjd112.FoodOrderingSystem.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for adding item to shopping cart request.
 * Contains food item ID and desired quantity.
 */
public record CartItemRequest(
        @NotNull Long foodItemId,
        @NotNull @Min(1) Integer quantity
) {
}
