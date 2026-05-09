package lk.ijse.cmjd112.FoodOrderingSystem.dto.cart;

import java.util.List;

/**
 * DTO for shopping cart response.
 * Returns complete cart contents with items and total amount.
 */
public record CartResponse(
        Long cartId,
        Long userId,
        List<CartItemResponse> items,
        Double totalAmount
) {
}
