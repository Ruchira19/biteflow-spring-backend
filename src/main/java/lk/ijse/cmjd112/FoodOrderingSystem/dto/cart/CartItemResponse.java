package lk.ijse.cmjd112.FoodOrderingSystem.dto.cart;

/**
 * DTO for shopping cart item response.
 * Returns item details including price, quantity, and line total.
 */
public record CartItemResponse(
        Long cartItemId,
        Long foodItemId,
        String foodName,
        Double unitPrice,
        Integer quantity,
        Double lineTotal,
        Integer stockQuantity
) {
}
