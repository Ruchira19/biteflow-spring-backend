package lk.ijse.cmjd112.FoodOrderingSystem.dto.food;

import lk.ijse.cmjd112.FoodOrderingSystem.entity.FoodStatus;

/**
 * DTO for food item information response.
 * Returns food details including ID, pricing, availability, and category.
 */
public record FoodItemResponse(
        Long id,
        String name,
        String description,
        Double price,
        Integer stockQuantity,
        FoodStatus status,
        Long categoryId,
        String categoryName
) {
}
