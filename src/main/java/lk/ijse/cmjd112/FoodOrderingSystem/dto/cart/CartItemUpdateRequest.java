package lk.ijse.cmjd112.FoodOrderingSystem.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for updating cart item quantity request.
 * Contains new quantity for the cart item.
 */
public record CartItemUpdateRequest(
        @NotNull @Min(1) Integer quantity
) {
}
