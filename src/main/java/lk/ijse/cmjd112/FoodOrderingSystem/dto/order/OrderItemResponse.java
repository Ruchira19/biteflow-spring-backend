package lk.ijse.cmjd112.FoodOrderingSystem.dto.order;

/**
 * DTO for order line item response.
 * Returns details of a single item in an order including price and quantity.
 */
public record OrderItemResponse(
        Long orderItemId,
        Long foodItemId,
        String foodName,
        Integer quantity,
        Double unitPrice,
        Double lineTotal
) {
}
