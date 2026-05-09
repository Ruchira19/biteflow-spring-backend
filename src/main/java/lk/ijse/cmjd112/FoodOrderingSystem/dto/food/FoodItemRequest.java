package lk.ijse.cmjd112.FoodOrderingSystem.dto.food;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.FoodStatus;

/**
 * DTO for food item creation/update request.
 * Contains food details including name, description, price, stock, and category.
 */
public record FoodItemRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull @DecimalMin("0.0") Double price,
        @NotNull @Min(0) Integer stockQuantity,
        @NotNull FoodStatus status,
        @NotNull Long categoryId
) {
}
